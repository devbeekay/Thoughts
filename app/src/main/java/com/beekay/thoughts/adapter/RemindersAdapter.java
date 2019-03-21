package com.beekay.thoughts.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beekay.thoughts.R;
import com.beekay.thoughts.model.Reminder;

import java.util.List;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.RemindersViewHolder> {

    private List<Reminder> reminders;

    public RemindersAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public RemindersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item,parent,false);
        return new RemindersAdapter.RemindersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemindersViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        if (reminder.isStatus()) {
            holder.constraintLayout.setBackgroundColor(Color.parseColor("#AACCAA"));
        } else {
            holder.constraintLayout.setBackgroundColor(Color.parseColor("#FFAAAA"));
        }
        holder.reminderField.setText(reminder.getReminderText());
        holder.statusField.setText("Status : " + (reminder.isStatus() ? "Completed" : "Incomplete"));
        holder.toBeDoneOnField.setText("To Be Done On \n" + reminder.getToBeDoneOn());
        holder.createdOnField.setText("Created On \n" + reminder.getCreatedOn());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    class RemindersViewHolder extends RecyclerView.ViewHolder {

        TextView reminderField;
        TextView toBeDoneOnField;
        TextView createdOnField;
        TextView statusField;
        ConstraintLayout constraintLayout;

        public RemindersViewHolder(View itemView) {
            super(itemView);
            reminderField = itemView.findViewById(R.id.reminder_text);
            statusField = itemView.findViewById(R.id.reminder_status);
            toBeDoneOnField = itemView.findViewById(R.id.scheduled_for);
            createdOnField = itemView.findViewById(R.id.createdOn);
            constraintLayout = itemView.findViewById(R.id.rConstraintLayout);
        }
    }
}
