package com.beekay.thoughts.ui.thoughts

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.beekay.thoughts.MainActivity
import com.beekay.thoughts.R
import com.beekay.thoughts.databinding.FragmentAddThoughtBinding
import com.beekay.thoughts.model.Thought
import com.beekay.thoughts.util.encryptThought
import com.beekay.thoughts.viewmodel.ThoughtViewModel
import com.beekay.thoughts.viewmodel.factory.ThoughtViewModelFactory
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val IMG_PATH = "images"
const val TAG = "AddThought"
private val format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

class AddThoughtFragment : Fragment() {

    private lateinit var binding: FragmentAddThoughtBinding
    private val viewModel: ThoughtViewModel by activityViewModels {
        ThoughtViewModelFactory(requireContext())
    }
    private var imgSrc: Uri? = null

    private var selectedThought: Thought? = null

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddThoughtBinding.inflate(inflater)
        return binding.root.also {
            imagePickerLauncher =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    uri?.let {
                        setImagePreview(it)
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.toolbarTitle.text =
            requireContext().resources.getString(R.string.add_thought)
        (activity as MainActivity).setSupportActionBar(binding.toolbar.toolbarTop)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as MainActivity,
            findNavController()
        )
        binding.toolbar.toolbarTop.setNavigationOnClickListener {
            requireActivity().currentFocus?.let {
                val inputManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(
                    it.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
            NavigationUI.navigateUp(findNavController(), null)
        }
        (activity as MainActivity).supportActionBar?.title = ""
        setHasOptionsMenu(true)
        val imagesDir = File(requireContext().filesDir, "images")
        viewModel.selectedThought.observe(requireActivity()) {
            it?.let { thought ->
                selectedThought = thought
                binding.entry.setText(thought.thought)
                if (!thought.imgSource.isNullOrEmpty()) {
                    binding.preview.setImageURI(
                        Uri.fromFile(File(imagesDir, thought.imgSource))
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        selectedThought?.let {
            menu.removeItem(R.id.insert_photo)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.insert_photo) {
            imagePickerLauncher.launch("image/*")
        } else if (item.itemId == R.id.save) {
            validateAndSave()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateAndSave() {
        if (binding.entry.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Thought cannot be empty", Toast.LENGTH_LONG).show()
        } else {
            save()
        }
    }

    private fun setImagePreview(data: Uri) {
        binding.preview.setImageURI(data)
        imgSrc = data
    }

    private fun save() {
        val thought = selectedThought?.copy(
            thought = encryptThought(binding.entry.text.toString()),
            updatedOn = LocalDateTime.now().format(format)
        ) ?: let {
            val thoughtText = encryptThought(binding.entry.text.toString())
            val createdOn = LocalDateTime.now().format(format)
            val starred = false
            val fName = if (null != imgSrc) {
                saveImage() ?: throw Exception("Failed to save image")
            } else {
                null
            }
            Thought(
                thought = thoughtText,
                createdOn = createdOn,
                updatedOn = createdOn,
                imgSource = fName,
                starred = starred
            )
        }
        save(thought)
    }

    private fun save(thought: Thought) {
        viewModel.insertThought(thought)
        findNavController().popBackStack()
    }


    private fun saveImage(): String? {
        val folder = File(requireContext().filesDir, IMG_PATH)
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Toast.makeText(requireContext(), "Could not save image", Toast.LENGTH_LONG).show()
                return null
            }
        }
        val fName = File(folder, "Img_${Date().time}.${getExtension(requireContext())}")
        return try {
            val ops = fName.outputStream()
            val buf = ByteArray(1024)
            val ins = requireContext().contentResolver.openInputStream(imgSrc!!)
            ins?.read(buf)
            do {
                ops.write(buf)
            } while (ins?.read(buf) != -1)
            fName.name
        } catch (ex: Exception) {
            Log.e(TAG, ex.message, ex)
            null
        }
    }

    private fun getExtension(context: Context): String? {
        val resolver = context.contentResolver
        val mimeType = MimeTypeMap.getSingleton()
        return mimeType.getExtensionFromMimeType(resolver.getType(imgSrc!!))
    }

}