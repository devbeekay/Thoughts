package com.beekay.thoughts.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.BitmapCompat;

import com.beekay.thoughts.model.Thought;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataOpener {

    private static final String DB_NAME = "thoughts.db";

    //Thoughts Table
    private static final String TABLE_NAME = "thoughts";
    private static final String ID = "id";
    private static final String TIMESTAMP = "timestamp";
    private static final String THOUGHT_TEXT = "thought_text";
    private static final String IMG_SRC = "image_src";
    private static final String IMG = "image";
    private static final String STARRED = "starred";

    private static final String CREATE_STATEMENT = "create table if not exists thoughts" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "timestamp DATE DEFAULT (datetime('now','localtime')) NOT NULL," +
            "thought_text TEXT NOT NULL," +
            "image_src TEXT," +
            "image BLOB, " +
            "location TEXT, " +
            "starred INTEGER DEFAULT 0)";

    private static final String RTABLE_NAME = "reminders";
    private static final String REMINDER = "reminder";
    private static final String DATE_WHEN = "date_when";
    private static final String DONE = "done";

    private static final String RCREATE_STATEMENT = "create table if not exists reminders" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "timestamp DATE DEFAULT (datetime('now', 'localtime')) NOT NULL, " +
            "reminder TEXT NOT NULL, " +
            "date_when DATE NOT NULL, " +
            "done INTEGER DEFAULT 0)";

    private static final int VERSION = 7;
    private static final int BYTE_MAX = 2 * 1024 * 1024;
    private SQLiteDatabase db;
    private  DbHelper helper;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("d-M-Y H:m");
    Context context;

    public DataOpener(Context context){
        this.context = context;
        helper = new DbHelper(context);
    }

    public DataOpener open(){
        db = helper.getWritableDatabase();
        return this;
    }

    public DataOpener openRead(){
        db = helper.getReadableDatabase();
        return this;
    }

    public Cursor retrieve(){
        return db.query(TABLE_NAME,new String[]{ID, TIMESTAMP, THOUGHT_TEXT, IMG_SRC, IMG, STARRED},
                null, null, null, null, "timestamp desc");
    }

    public Cursor retrieveReminders() {
        return db.query(RTABLE_NAME, new String[] {ID, REMINDER, TIMESTAMP, DATE_WHEN, DONE},
                null, null, null, null, "timestamp desc");
    }

    public Cursor retrieveReminders(int doneValue) {
        return db.query(RTABLE_NAME, new String[] {ID, REMINDER, TIMESTAMP, DATE_WHEN, DONE},
                "done=?", new String[] {""+doneValue}, null, null, "timestamp desc");
    }

    public Cursor retrieveById(String id) {
        return db.query(TABLE_NAME, new String[]{ID, TIMESTAMP, THOUGHT_TEXT, IMG_SRC, IMG, STARRED},ID+"=?",new String[]{id}, null, null, "1");
    }

    public Cursor retrieveStarredThoughts() {
        return db.query(TABLE_NAME, new String[]{ID, TIMESTAMP, THOUGHT_TEXT, IMG_SRC, IMG, STARRED},STARRED+"=?",new String[]{"1"}, null, null, "1");
    }

    public void updateStar(String id, boolean flag) {
        ContentValues values = new ContentValues();
        values.put(STARRED, flag ? 1 : 0);
        db.update(TABLE_NAME, values, ID+"=?", new String[]{id});
    }

    public void delete(String id) {
        db.delete(TABLE_NAME,ID+"=?",new String[]{id});
    }

    public void update(String id, String thought, String imgSrc) throws IOException {
        ContentValues values = new ContentValues();
        values.put(THOUGHT_TEXT, thought);
        values.put(IMG_SRC, imgSrc);
        db.update(TABLE_NAME, values, ID + "=?", new String[]{id});
        if (imgSrc != null && imgSrc.trim().length() > 0) {
            Bitmap img = getBitmapFromSource(imgSrc);
            if (img != null) {
                int bitmapSize = BitmapCompat.getAllocationByteCount(img);
                if (bitmapSize >= BYTE_MAX) {
                    String imagesFolderPath = context.getFilesDir().getAbsolutePath() + "/images";
                    File fileDir = new File(imagesFolderPath);
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    String imagePath = imagesFolderPath + "/" + id + ".png";
                    FileOutputStream fos = new FileOutputStream(new File(imagePath));
                    img.compress(Bitmap.CompressFormat.PNG, 0, fos);
                    fos.close();
                    insertImage(id, imagePath);
                } else {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    byte[] imgBytes = stream.toByteArray();
                    insertImage(id, imgBytes);
                    stream.close();
                }
            }
        }
    }

    private Bitmap getBitmapFromSource(String imgSrc) {
        File imgFile = new File(imgSrc);
        if(imgFile.exists()) {
            Bitmap img = BitmapFactory.decodeFile(imgSrc);
            return img;
        }
        return null;
    }

    public long insertReminder(String reminder, Date dateWhen) {
        ContentValues values = new ContentValues();
        values.put(REMINDER, reminder);
        values.put(DATE_WHEN, sdf.format(dateWhen));
        return db.insertOrThrow(RTABLE_NAME, null, values);
    }

    public void updateReminder(int id, int doneValue) {
        ContentValues values = new ContentValues();
        values.put(DONE, 1);
        values.put(DONE, doneValue);
        db.update(RTABLE_NAME, values, "id=?", new String[]{""+id});
    }

    public long insert(Thought thought) throws  IOException {
        ContentValues values = new ContentValues();
        values.put(ID, thought.getId());
        values.put(THOUGHT_TEXT, thought.getThoughtText());
        values.put(TIMESTAMP, reverse(thought.getTimestamp()));
        values.put(IMG, thought.getImg());
        values.put(IMG_SRC, thought.getImgSource());
        return db.insertOrThrow(TABLE_NAME, null, values);
    }

    private String reverse(String tStamp) {
        String[] timeArray = tStamp.split(" ");
        String[] dateArray = timeArray[0].split("-");
        return dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0] + " " + timeArray[1];
    }


    public long insert(String thought, String imgSrc) throws IOException {
        ContentValues values = new ContentValues();
        values.put(THOUGHT_TEXT, thought);

        long id = db.insertOrThrow(TABLE_NAME,null,values);
        if (imgSrc != null && imgSrc.trim().length() > 0) {
            Bitmap img = getBitmapFromSource(imgSrc);
            if (img != null) {
                int bitmapSize = BitmapCompat.getAllocationByteCount(img);
                if (bitmapSize >= BYTE_MAX) {
                    String imagesFolderPath = context.getFilesDir().getAbsolutePath() + "/images";
                    File fileDir = new File(imagesFolderPath);
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    String imagePath = imagesFolderPath + "/" + id + ".png";
                    FileOutputStream fos = new FileOutputStream(new File(imagePath));
                    img.compress(Bitmap.CompressFormat.PNG, 0, fos);
                    fos.close();
                    insertImage(String.valueOf(id), imagePath);
                } else {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    byte[] imgBytes = stream.toByteArray();
                    insertImage(String.valueOf(id), imgBytes);
                    stream.close();
                }
            }
        }
        return id;
    }

    private void migrate() {
        Cursor cursor = retrieve();
        while(cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex("id"));
            String imgSrc = cursor.getString(cursor.getColumnIndex("image_src"));
            if (imgSrc!=null && imgSrc.trim().length()>0){
                File imgFile = new File(imgSrc);
                if(imgFile.exists()) {
                    Bitmap img = BitmapFactory.decodeFile(imgSrc);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    insertImage(String.valueOf(id), stream.toByteArray());
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void insertImage(String id, byte[] img) {
        ContentValues values = new ContentValues();
        values.put(IMG, img);
        values.put(IMG_SRC,"");
        db.update(TABLE_NAME,values,ID + "=?", new String[]{id});
    }

    private void insertImage(String id, String imgSrc) {
        ContentValues values = new ContentValues();
        values.put(IMG_SRC, imgSrc);
        values.put(IMG, "");
        db.update(TABLE_NAME,values,ID + "=?", new String[]{id});
    }

    public void close(){
        helper.close();
    }



    private class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_STATEMENT);
            db.execSQL(RCREATE_STATEMENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion < 7){
                onCreate(db);
            }

        }
    }




}
