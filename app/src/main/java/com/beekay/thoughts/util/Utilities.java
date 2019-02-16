package com.beekay.thoughts.util;

import android.content.Context;
import android.database.Cursor;

import com.beekay.thoughts.db.DataOpener;
import com.beekay.thoughts.model.Thought;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utilities {
    Context context;

    public Utilities(Context context) {
        this.context = context;
    }

    public List<Thought> getThoughts() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS", Locale.getDefault());
        DataOpener db = new DataOpener(context);
        db.openRead();
        Cursor cursor = db.retrieve();
        List<Thought> thoughts = new ArrayList<>();
        while(cursor.moveToNext()){
            Thought thought = new Thought();
            Long id = cursor.getLong(cursor.getColumnIndex("id"));
            String t = cursor.getString(cursor.getColumnIndex("thought_text"));
            String d = cursor.getString(cursor.getColumnIndex("timestamp"));
//            System.out.println(d);
            String i = cursor.getString(cursor.getColumnIndex("image_src"));
            byte[] pic = cursor.getBlob(cursor.getColumnIndex("image"));
//            if (pic.length > 0){
            thought.setImg(pic);
//            }
            thought.setId(id);
            thought.setThoughtText(t);

            thought.setTimestamp(d);
            thought.setImgSource(i.trim().length()>0?i:null);
            thoughts.add(thought);
//            Log.i("id", id);
        }
        cursor.close();
        db.close();

        return thoughts;
    }
}
