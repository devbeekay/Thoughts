package com.beekay.thoughts.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beekay.thoughts.MainActivity
import com.beekay.thoughts.R
import com.beekay.thoughts.databinding.FragmentAddThoughtBinding
import com.beekay.thoughts.model.Thought
import com.beekay.thoughts.viewmodel.ThoughtViewModel
import com.beekay.thoughts.viewmodel.factory.ThoughtViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val INSERT_IMAGE_REQ_ID = 1001
const val IMG_PATH = "images"
const val TAG = "AddThought"
val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

class AddThoughtFragment : Fragment() {

    private lateinit var binding: FragmentAddThoughtBinding
    private val viewModel: ThoughtViewModel by viewModels {
        ThoughtViewModelFactory(requireContext())
    }
    private var imgSrc: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddThoughtBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.findViewById<TextView>(R.id.toolbar_title).visibility = View.GONE
        (activity as MainActivity).setSupportActionBar(binding.toolbar as Toolbar)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.insert_photo) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, INSERT_IMAGE_REQ_ID)
        } else {
            //save
            validateAndSave()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateAndSave() {
        if (binding.entry.text.toString().trim().isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Thought cannot be empty", Toast.LENGTH_LONG).show()
        } else {
            save()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == INSERT_IMAGE_REQ_ID) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.data != null) {
                        setImagePreview(data.data!!)
                    }
                }
            }
        }
    }

    private fun setImagePreview(data: Uri) {
        binding.preview.setImageURI(data)
        imgSrc = data
    }

    private fun save() {
        val thoughtText = binding.entry.text.toString()
        val createdOn = sdf.format(Date())
        val starred = false
        val fName = if (null != imgSrc) {
            saveImage() ?: throw Exception("Failed to save image")
        } else {
            null
        }
        val thought = Thought(
                thought = thoughtText,
                createdOn = createdOn,
                updatedOn = createdOn,
                imgSource = fName,
                starred = starred
        )
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
        val mimeType = getExtension(requireContext()) ?: return null
        val fName = File(folder, "Img_${Date().time}.$mimeType")
        return try {
            val ops = fName.outputStream()
            val buf = ByteArray(1024)
            val ins = requireContext().contentResolver.openInputStream(imgSrc!!)
            ins?.read(buf)
            do {
                ops.write(buf)
            } while (ins?.read(buf) != -1)
            fName.absolutePath
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