package com.beekay.thoughts.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beekay.thoughts.databinding.ItemReminderBinding
import com.beekay.thoughts.model.Reminder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Krishna by 21-11-2020
 */
class RemindersAdapter : RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder>() {

    private val reminders: MutableList<Reminder> = mutableListOf()
    private val currentDate = LocalDateTime.now()

    @SuppressLint("NotifyDataSetChanged")
    fun setReminders(reminders: List<Reminder>) {
        this.reminders.clear()
        this.reminders.addAll(reminders)
        notifyDataSetChanged()
    }

    class ReminderViewHolder(
        private val binding: ItemReminderBinding,
        private val currentDate: LocalDateTime
    ) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

        fun bind(reminder: Reminder) {
            binding.createdOn.text = reminder.createdOn
            binding.scheduledFor.text = reminder.toBeDoneOn
            binding.reminderText.text = reminder.reminderAbout
            val remindOn = LocalDateTime.parse(reminder.toBeDoneOn, dateFormatter)
            val status = if (reminder.status) {
                "Done"
            } else {
                if (currentDate.isBefore(remindOn)) {
                    "Pending"
                } else {
                    "Missed"
                }
            }
            binding.reminderStatus.text = status
            if (status == "Done") {
                binding.root.setBackgroundColor(Color.parseColor("#3DA159"))
            } else if (status == "Missed") {
                binding.root.setBackgroundColor(Color.parseColor("#e8416f"))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemReminderBinding.inflate(inflater, parent, false)
        return ReminderViewHolder(binding, currentDate)
    }

    override fun getItemCount() = reminders.size

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }
}