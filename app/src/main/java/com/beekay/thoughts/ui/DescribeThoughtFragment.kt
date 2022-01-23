package com.beekay.thoughts.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.beekay.thoughts.MainActivity
import com.beekay.thoughts.R
import com.beekay.thoughts.databinding.FragmentDescribeThoughtBinding
import com.beekay.thoughts.viewmodel.ThoughtViewModel
import com.beekay.thoughts.viewmodel.factory.ThoughtViewModelFactory
import java.io.File

class DescribeThoughtFragment : Fragment() {

    private lateinit var binding: FragmentDescribeThoughtBinding

    private val viewModel: ThoughtViewModel by activityViewModels {
        ThoughtViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDescribeThoughtBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.toolbarTitle.text = requireContext().resources.getString(R.string.thought)
        (activity as MainActivity).setSupportActionBar(binding.toolbar.toolbarTop)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        NavigationUI.setupActionBarWithNavController(requireActivity() as MainActivity, findNavController())
        binding.toolbar.toolbarTop.setNavigationOnClickListener {
            requireActivity().currentFocus?.let {
                val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(
                    it.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
            NavigationUI.navigateUp(findNavController(), null)
        }
        (activity as MainActivity).supportActionBar?.title = ""
        val imagesDir = File(requireContext().filesDir, "images")
        viewModel.selectedThought.observe(requireActivity()) {
            it?.let { thought ->
                binding.entry.text = thought.thought
                if (!thought.imgSource.isNullOrEmpty()) {
                    binding.preview.setImageURI(
                        Uri.fromFile(File(imagesDir, thought.imgSource))
                    )
                }
            }
        }
    }

}