package com.beekay.thoughts.viewmodel

import androidx.lifecycle.ViewModel
import com.beekay.thoughts.dao.ReminderDAO
import com.beekay.thoughts.model.Reminder
import com.beekay.thoughts.model.Thought
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Krishna by 16-11-2020
 */
class ReminderViewModel(private val reminderDAO: ReminderDAO): ViewModel() {

    val reminders = reminderDAO.getAllReminders()

    fun insertThought(reminder: Reminder) {
        GlobalScope.launch {
            reminderDAO.insertReminder(reminder)
        }
    }

    fun deleteThought(reminder: Reminder) {
        reminderDAO.deleteReminder(reminder)
    }

}