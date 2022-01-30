package com.beekay.thoughts.ui.reminders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beekay.thoughts.R
import com.beekay.thoughts.adapter.RemindersAdapter
import com.beekay.thoughts.databinding.FragmentRemindersBinding
import com.beekay.thoughts.viewmodel.ReminderViewModel
import com.beekay.thoughts.viewmodel.factory.ReminderViewModelFactory

class RemindersFragment : Fragment() {

    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory(requireContext())
    }
    private lateinit var binding: FragmentRemindersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRemindersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.toolbarTitle.text = requireContext().resources.getString(R.string.reminders)
        val adapter = RemindersAdapter()
        binding.recycleReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleReminders.adapter = adapter
        viewModel.reminders.observe(requireActivity(), {
            adapter.setReminders(it)
        })
    }

}