package com.beekay.thoughts.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beekay.thoughts.MainActivity
import com.beekay.thoughts.R
import com.beekay.thoughts.adapter.ClickListener
import com.beekay.thoughts.adapter.ClickType
import com.beekay.thoughts.adapter.ThoughtsAdapter
import com.beekay.thoughts.adapter.ThoughtsContextualCallback
import com.beekay.thoughts.databinding.FragmentThoughtsBinding
import com.beekay.thoughts.model.Thought
import com.beekay.thoughts.model.ThoughtDao
import com.beekay.thoughts.util.decryptThought
import com.beekay.thoughts.util.encryptThought
import com.beekay.thoughts.viewmodel.ThoughtViewModel
import com.beekay.thoughts.viewmodel.factory.ThoughtViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.concurrent.Executor
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


/**
 * @author beekay
 */
class ThoughtsFragment : androidx.fragment.app.Fragment(), ClickListener<Thought>,
    SearchView.OnCloseListener, SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentThoughtsBinding
    private val viewModel: ThoughtViewModel by activityViewModels {
        ThoughtViewModelFactory(requireContext().applicationContext)
    }

    private lateinit var importLauncher: ActivityResultLauncher<String>

    private var isAddReminderVisible = false

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt

    private lateinit var thoughts: List<Thought>

    private lateinit var adapter: ThoughtsAdapter

    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        importLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { it: Uri? ->
                it?.let {
                    importThoughts(it)
                }
            }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThoughtsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar.toolbarTop)
        (requireActivity() as MainActivity).supportActionBar?.title = ""
        viewModel.selectedThought.postValue(null)

        binding.thoughts.layoutManager = LinearLayoutManager(requireContext())
        adapter = ThoughtsAdapter(requireContext(), this)
        viewModel.thoughts.observe(requireActivity(), {
            val t = it.map { thought -> thought.copy(thought = decryptThought(thought.thought)) }
            thoughts = t
            adapter.setThoughts(t)
        })

        binding.thoughts.adapter = adapter
        binding.addThought.setOnClickListener {
            if (isAddReminderVisible) {
                binding.addThought.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireContext().resources,
                        R.drawable.ic_add_white,
                        null
                    )
                )
                isAddReminderVisible = false
                binding.addReminder.visibility = View.GONE
                findNavController().navigate(R.id.action_thoughtsFragment_to_addThoughtFragment)
            } else {
                isAddReminderVisible = true
                binding.addReminder.visibility = View.VISIBLE
                binding.addThought.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        requireContext().resources,
                        R.drawable.ic_note_add,
                        null
                    )
                )
            }
        }

        binding.addReminder.setOnClickListener {
            isAddReminderVisible = false
            showAddReminderDialog()
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.action_search)
        searchView = item.actionView as ExpandEnableSearchView
        searchView.queryHint = "Search Thoughts"
        (searchView as ExpandEnableSearchView).toolbar = binding.toolbar
        searchView.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reminders -> {
                findNavController().navigate(R.id.action_thoughtsFragment_to_remindersFragment)
            }
            R.id.action_starred -> {
                viewModel.onlyStarred.value?.let {
                    if (it) {
                        item.title = "Starred Thoughts"
                    } else {
                        item.title = "All Thoughts"
                    }
                    viewModel.onlyStarred.postValue(it.not())
                }
            }
            R.id.action_import -> {
                importLauncher.launch("*/*")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAddReminderDialog() {
        findNavController().navigate(R.id.action_thoughtsFragment_to_addReminderFragment)
    }

    override fun onClick(entity: Thought, type: ClickType) {
        if (type == ClickType.STAR) {
            entity.starred = !entity.starred
            viewModel.insertThought(entity)
        } else if (type == ClickType.THOUGHT) {
            viewModel.selectedThought.postValue(entity)
            findNavController().navigate(R.id.action_thoughtsFragment_to_describeThoughtFragment)
        }
    }

    override fun onLongClick(entity: Thought, viewHolder: RecyclerView.ViewHolder) {
        requireActivity().startActionMode(
            ThoughtsContextualCallback(
                requireContext(),
                entity,
                viewHolder as ThoughtsAdapter.ThoughtViewHolder,
                viewModel,
                findNavController()
            )
        )
    }

    override fun onResume() {
        binding.whiteBackground.visibility = View.VISIBLE
        executor = ContextCompat.getMainExecutor(requireContext())

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    requireActivity().finish()
                }
                if (errorCode == BiometricPrompt.ERROR_CANCELED) {
                    requireActivity().finish()
                }
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    requireActivity().finish()
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                binding.whiteBackground.visibility = View.GONE
            }
        }
        biometricPrompt = BiometricPrompt(this, executor, callback)
        biometricPrompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Show Thoughts")
                .setNegativeButtonText("Cancel")
                .build()
        )

        super.onResume()
    }

    private fun importThoughts(path: Uri) {
        val contentResolver = requireContext().contentResolver
        val imagesDir = File(requireContext().filesDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        val zis = ZipInputStream(contentResolver.openInputStream(path))
        var ze: ZipEntry
        var count: Int
        val buffer = ByteArray(2048)
        while (zis.nextEntry.also { ze = it } != null) {
            if (ze.name.endsWith("thoughts.txt")) {
                val inputReader = InputStreamReader(zis)
                val br = BufferedReader(inputReader)
                var line: String?
                val sBuffer = StringBuilder()
                try {
                    while (br.readLine().also { line = it } != null) {
                        sBuffer.append(line)
                    }
                    val json = sBuffer.toString()
                    val gsonObj = Gson()
                    val typeToken = object : TypeToken<List<ThoughtDao>>() {}
                    val someThoughts = gsonObj.fromJson<List<ThoughtDao>>(json, typeToken.type)
                    val emptyByteArray = byteArrayOf(0)
                    someThoughts.forEach {
                        val fName = if (it.img != null && !it.img.contentEquals(emptyByteArray)) {
                            val img = File(imagesDir, "${it.id}.png")
                            img.createNewFile()
                            val fos = img.outputStream()
                            fos.write(it.img)
                            fos.flush()
                            fos.close()
                            img.name
                        } else if (!it.imgSource.isNullOrEmpty()) {
                            it.imgSource.substringAfterLast("/")
                        } else {
                            null
                        }
                        val thought = Thought(
                            id = it.id,
                            thought = encryptThought(it.thoughtText),
                            createdOn = reverseCreatedOn(it.timestamp),
                            imgSource = fName,
                            starred = it.starred
                        )
                        viewModel.insertThought(thought)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }
            } else if (ze.name == "pictures" && ze.isDirectory) {
                Log.i("ThoughtsFragment", "Encountered pictures folder")
            } else {
                val f = File(imagesDir, ze.name.split("/")[2])
                val fout = FileOutputStream(f)
                fout.use { it ->
                    while (zis.read(buffer).also { count = it } != -1) {
                        it.write(buffer, 0, count)
                    }
                }
            }
        }
    }

    private fun reverseCreatedOn(createdOn: String): String {
        val totalParts = createdOn.split(" ")
        return totalParts[0]
            .split("-")
            .reversed()
            .joinToString("-") + " " + totalParts[1]
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            adapter.setThoughts(thoughts.filter { thought ->
                thought.thought.contains(
                    it,
                    ignoreCase = true
                )
            })
        } ?: adapter.setThoughts(thoughts)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onClose(): Boolean {
        adapter.setThoughts(thoughts)
        return false
    }

}