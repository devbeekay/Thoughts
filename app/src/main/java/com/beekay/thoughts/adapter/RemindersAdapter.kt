package com.beekay.thoughts.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beekay.thoughts.databinding.ItemReminderBinding
import com.beekay.thoughts.model.Reminder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Krishna by 21-11-2020
 */
class RemindersAdapter(): RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder>() {

    private val reminders: MutableList<Reminder> = mutableListOf()
    private val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())

    fun setReminders(reminders: List<Reminder>) {
        this.reminders.clear()
        this.reminders.addAll(reminders)
        notifyDataSetChanged()
    }

    class ReminderViewHolder(private val binding: ItemReminderBinding,
    private val currentDate: String): RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) {
            binding.createdOn.text = reminder.createdOn
            binding.scheduledFor.text = reminder.toBeDoneOn
            binding.reminderText.text = reminder.reminderAbout
            val status = if (reminder.status) {
                "Done"
            } else {
                if (currentDate > reminder.toBeDoneOn) {
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
        return RemindersAdapter.ReminderViewHolder(binding, currentDate)
    }

    override fun getItemCount() = reminders.size

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }
}