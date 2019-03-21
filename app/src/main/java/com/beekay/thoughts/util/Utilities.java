package com.beekay.thoughts.util;

import android.content.Context;
import android.database.Cursor;

import com.beekay.thoughts.db.DataOpener;
import com.beekay.thoughts.model.Reminder;
import com.beekay.thoughts.model.Thought;

import java.util.ArrayList;
import java.util.List;

public class Utilities {
    Context context;

    public Utilities(Context context) {
        this.context = context;
    }

    public List<Thought> getThoughts() {
        DataOpener db = new DataOpener(context);
        db.openRead();
        Cursor cursor = db.retrieve();
        List<Thought> thoughts = createThoughtFromCursor(cursor);
        cursor.close();
        db.close();

        return thoughts;
    }

    public Thought getThought(String id) {
        DataOpener db = new DataOpener(context);
        db.openRead();
        Cursor cursor = db.retrieveById(id);
        try {
            Thought thought = createThoughtFromCursor(cursor).get(0);
            return thought;
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public List<Thought> getStarredThoughts() {
        DataOpener db = new DataOpener(context);
        db.openRead();
        Cursor cursor = db.retrieveStarredThoughts();
        List<Thought> thoughts = createThoughtFromCursor(cursor);
        cursor.close();
        db.close();
        return thoughts;
    }

    public List<Reminder> getReminders() {
        DataOpener db = new DataOpener(context);
        db.openRead();
        Cursor cursor = db.retrieveReminders();
        List<Reminder> reminders = new ArrayList<>();
        while (cursor.moveToNext()) {
            Reminder reminder = new Reminder();
            reminder.setId(cursor.getInt(cursor.getColumnIndex("id")));
            reminder.setReminderText(cursor.getString(cursor.getColumnIndex("reminder")));
            reminder.setToBeDoneOn(cursor.getString(cursor.getColumnIndex("date_when")));
            reminder.setStatus(cursor.getInt(cursor.getColumnIndex("done")) == 1);
            reminder.setCreatedOn(cursor.getString(cursor.getColumnIndex("timestamp")));
            reminders.add(reminder);
        }
        cursor.close();
        db.close();
        return reminders;
    }

    private List<Thought> createThoughtFromCursor(Cursor cursor) {
        List<Thought> thoughts = new ArrayList<>();
        while(cursor.moveToNext()) {
            Thought thought = new Thought();
            thought.setId(Long.valueOf(cursor.getString(cursor.getColumnIndex("id"))));
            thought.setThoughtText(cursor.getString(cursor.getColumnIndex("thought_text")));
            thought.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            thought.setImgSource(cursor.getString(cursor.getColumnIndex("image_src")));
            thought.setImg(cursor.getBlob(cursor.getColumnIndex("image")));
            thought.setStarred(cursor.getInt(cursor.getColumnIndex("starred")) != 0);
            thoughts.add(thought);
        }
        return thoughts;
    }
}
