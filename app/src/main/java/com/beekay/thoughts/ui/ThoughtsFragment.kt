package com.beekay.thoughts.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.beekay.thoughts.MainActivity
import com.beekay.thoughts.R
import com.beekay.thoughts.adapter.ClickListener
import com.beekay.thoughts.adapter.ClickType
import com.beekay.thoughts.adapter.ThoughtsAdapter
import com.beekay.thoughts.databinding.FragmentThoughtsBinding
import com.beekay.thoughts.model.Thought
import com.beekay.thoughts.viewmodel.ThoughtViewModel
import com.beekay.thoughts.viewmodel.factory.ThoughtViewModelFactory

/**
 * @author beekay
 */
class ThoughtsFragment : androidx.fragment.app.Fragment(), ClickListener<Thought> {

    private lateinit var binding: FragmentThoughtsBinding
    private val viewModel: ThoughtViewModel by viewModels {
        ThoughtViewModelFactory(requireContext().applicationContext)
    }

    private var isAddReminderVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentThoughtsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = binding.toolbar
        binding.toolbar.findViewById<TextView>(R.id.toolbar_title).setText("Thoughts")
        (requireActivity() as MainActivity).setSupportActionBar(toolbar as Toolbar)

        binding.thoughts.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ThoughtsAdapter(requireContext(), this)
        viewModel.thoughts.observe(requireActivity(), Observer {
            adapter.setThoughts(it)
        })

        binding.thoughts.adapter = adapter
        binding.addThought.setOnClickListener {
            if (isAddReminderVisible) {
                binding.addThought.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_add_white))
                isAddReminderVisible = false
                binding.addReminder.visibility = View.GONE
                findNavController().navigate(R.id.action_thoughtsFragment_to_addThoughtFragment)
            } else {
                isAddReminderVisible = true
                binding.addReminder.visibility = View.VISIBLE
                binding.addThought.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_note_add))
            }
        }

        binding.addReminder.setOnClickListener {
            showAddReminderDialog()
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reminders -> {
                findNavController().navigate(R.id.action_thoughtsFragment_to_remindersFragment)
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

        }
    }

    override fun onLongClick(entity: Thought) {

    }

}