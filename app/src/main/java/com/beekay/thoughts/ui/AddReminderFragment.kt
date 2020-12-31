package com.beekay.thoughts.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.beekay.thoughts.databinding.FragmentAddReminderBinding
import com.beekay.thoughts.model.Reminder
import com.beekay.thoughts.receivers.ShowNotification
import com.beekay.thoughts.viewmodel.ReminderViewModel
import com.beekay.thoughts.viewmodel.factory.ReminderViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AddReminderFragment : Fragment() {

    private val storedSdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    private lateinit var binding: FragmentAddReminderBinding
    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory(requireContext())
    }

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddReminderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hours = mutableListOf<Int>()
        val minutes = mutableListOf<Int>()
        val ampm = mutableListOf("AM", "PM")
        for (i in 0..11) {
            hours.add(i)
        }
        for (i in 0..59) {
            minutes.add(i)
        }
        binding.datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        binding.hours.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, hours)
        binding.minutes.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, minutes)
        binding.ms.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ampm)
        binding.closeFragment.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.dateSet.setOnClickListener {
            val hour = if ((binding.ms.selectedItem as String) == "PM") binding.hours.selectedItemPosition + 12 else 0
            calendar.set(Calendar.HOUR, hour)
            calendar.set(Calendar.MINUTE, binding.minutes.selectedItemPosition)
            validateAndAddReminder()
        }

    }

    private fun validateAndAddReminder() {
        if (binding.remindAbout.text.toString().trim().isNullOrEmpty()) {
            Snackbar.make(requireView(), "Please enter what to remind about", Snackbar.LENGTH_LONG).show()
        } else if (calendar.timeInMillis < System.currentTimeMillis()) {
            Snackbar.make(requireView(), "Time to remind should be in future", Snackbar.LENGTH_LONG).show()
        } else {
            addReminder()
        }

    }

    private fun addReminder() {
        val remindAbout = binding.remindAbout.text.toString().trim()
        val tag = UUID.randomUUID().toString()
        println("${System.currentTimeMillis()} system time")
        println("${calendar.timeInMillis} calendar time")
        val timeToDelay = calendar.timeInMillis - System.currentTimeMillis()
        println(timeToDelay)
        val request = OneTimeWorkRequestBuilder<ShowNotification>()
                .setInitialDelay(timeToDelay, TimeUnit.MILLISECONDS)
                .setInputData(
                        Data.Builder()
                                .putString("remindAbout", remindAbout)
                                .putString("tag", tag)
                                .build()
                )
                .addTag(tag)
                .build()
        WorkManager.getInstance(requireContext())
                .enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, request)
        val createdOn = storedSdf.format(Date())
        val remindOn = storedSdf.format(calendar.time)
        val reminder = Reminder(
                reminderAbout = remindAbout,
                createdOn = createdOn,
                toBeDoneOn = remindOn,
                tag = tag,
                status = false
        )
        viewModel.insertThought(reminder)
        findNavController().popBackStack()
    }


}