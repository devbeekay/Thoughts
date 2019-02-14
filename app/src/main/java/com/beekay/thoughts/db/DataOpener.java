package com.beekay.thoughts.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class DataOpener {

    private static final String DB_NAME = "thoughts.db";

    //Thoughts Table
    private static final String TABLE_NAME = "thoughts";
    private static final String ID = "id";
    private static final String TIMESTAMP = "timestamp";
    private static final String THOUGHT_TEXT = "thought_text";
    private static final String IMG_SRC = "image_src";
    private static final String IMG = "image";
    //private static final String CREATE_STATEMENT = "create table if not exists thoughts(timestamp date default (datetime('now','localtime')) not null, thought_text text not null, image_src text)";
    private static final String CREATE_STATEMENT = "create table if not exists thoughts" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "timestamp DATE DEFAULT (datetime('now','localtime')) NOT NULL," +
            "thought_text TEXT NOT NULL," +
            "image_src TEXT," +
            "image BLOB, " +
            "location TEXT)";

    private static final int VERSION = 3;
    private SQLiteDatabase db;
    private  DbHelper helper;
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
        return db.query(TABLE_NAME,new String[]{ID, TIMESTAMP, THOUGHT_TEXT, IMG_SRC, IMG},null, null, null, null, "timestamp desc");
    }

    public long insert(String thought, String imgSrc) throws IOException {
        ContentValues values = new ContentValues();
        values.put(THOUGHT_TEXT, thought);
        values.put(IMG_SRC,imgSrc);
        long id = db.insertOrThrow(TABLE_NAME,null,values);
        if (imgSrc!=null && imgSrc.trim().length()>0){
            File imgFile = new File(imgSrc);
            if(imgFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap img = BitmapFactory.decodeFile(imgSrc);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 0, stream);
                insertImage(String.valueOf(id), stream.toByteArray());
                stream.close();
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
                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
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

    public void insertImage(String id, byte[] img) {
        ContentValues values = new ContentValues();
        values.put(IMG, img);
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

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion<3){
                db.beginTransaction();
                db.execSQL("ALTER TABLE thoughts rename to old_thoughts");
                db.execSQL(CREATE_STATEMENT);
                db.execSQL("INSERT INTO thoughts (timestamp, thought_text, image_src) select timestamp, thought_text, image_src from old_thoughts");
                db.execSQL("DROP TABLE old_thoughts");
                db.setTransactionSuccessful();
                db.endTransaction();
//                migrate();
            }
            onCreate(db);
        }
    }




}
