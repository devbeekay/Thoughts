package com.beekay.thoughts;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.beekay.thoughts.db.DataOpener;
import com.beekay.thoughts.model.Thought;
import com.beekay.thoughts.util.Utilities;

import java.io.File;
import java.io.IOException;

public class AddActivity extends AppCompatActivity {

    EditText thoughtField;
    ImageView previewImage;
    ImageButton saveButton;
    String imgPath = "";
    Thought thought = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        thoughtField = findViewById(R.id.thoughtField);
        previewImage = findViewById(R.id.add_img_preview);
        Intent i = getIntent();
        if (Intent.ACTION_SEND.equals(i.getAction()) && i.getType() != null) {
            if ("text/plain".equals(i.getType())) {
                thoughtField.setText(i.getStringExtra(Intent.EXTRA_TEXT));
            } else if (i.getType().startsWith("image/")) {
                Uri uri = i.getParcelableExtra(Intent.EXTRA_STREAM);
                if ( uri != null){
                    Toast.makeText(AddActivity.this, getPathFromUri(uri), Toast.LENGTH_SHORT).show();
                    String realPath = getPathFromUri(uri);
                    boolean found = false;
                    File img = new File(realPath);
                    if(img.exists()) {
                        imgPath = realPath;
                        Bitmap selectedImg = BitmapFactory.decodeFile(imgPath);
                        previewImage.setImageBitmap(selectedImg);
                    } else {
                        Toast.makeText(this, "Image Doesn't exist", Toast.LENGTH_LONG).show();
                    }
                } else {
                    System.out.println("URI is null");
                }
            }
        }
        Bundle intentBundle = i.getExtras();
        boolean editField = intentBundle.containsKey("Edit") && intentBundle.getBoolean("Edit");
        if (editField){
            Utilities utilities = new Utilities(this);
            thought = utilities.getThought(intentBundle.getString("id"));
        }
        // Get night mode option via intent
        boolean nightMode = intentBundle.containsKey("Mode") && intentBundle.getBoolean("Mode");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Changes Related to night mode
        if (nightMode) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
            getWindow().setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
            getWindow().getDecorView().setSystemUiVisibility(0);
        }



        if (thought != null) {
            thoughtField.setText(thought.getThoughtText());
            if (thought.getImg() != null && thought.getImg().length > 1) {
//                System.out.println(thought.getImg().length);

                Bitmap myBitMap = BitmapFactory.decodeByteArray(thought.getImg(), 0, thought.getImg().length);
                previewImage.setImageBitmap(myBitMap);
            } else if(thought.getImgSource() != null) {
//                System.out.println(thought.getImgSource());
                File f = new File(thought.getImgSource());
                if (f.exists()) {
//                    System.out.println("File Exists");
                    Bitmap fileBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
//                    totalImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    previewImage.setImageBitmap(fileBitmap);
                }
            }
        }
        saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thoughtField.getText().toString().trim().length() == 0 ){
                    Toast.makeText(AddActivity.this, "Cannot insert empty thought", Toast.LENGTH_SHORT).show();
                } else {
                    DataOpener db = new DataOpener(AddActivity.this);
                    db.open();
                    try {
                        if (thought != null) {
                            db.update(String.valueOf(thought.getId()), thoughtField.getText().toString(), imgPath);
                        } else {
                            db.insert(thoughtField.getText().toString(), imgPath);
                        }
                    } catch (IOException ex) {
                        Toast.makeText(AddActivity.this, "Could not be inserted", Toast.LENGTH_LONG).show();
                    }
                    db.close();
                    AddActivity.this.finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.insert_photo) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 100);
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 100){
                Uri uri = data.getData();
                if ( uri != null){
                    Toast.makeText(AddActivity.this, getPathFromUri(uri), Toast.LENGTH_SHORT).show();
                    String realPath = getPathFromUri(uri);
                    boolean found = false;

                    File img = new File(realPath);
                    if(img.exists()) {
                        imgPath = realPath;
                        Bitmap selectedImg = BitmapFactory.decodeFile(imgPath);
                        previewImage.setImageBitmap(selectedImg);
                    } else {
                        Toast.makeText(this, "Image Doesn't exist", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String res = " ";
        System.out.println(uri.getEncodedPath());
        String[] proj = {MediaStore.Images.Media.DATA};
//        String sel = MediaStore.Images.Media._ID + "=?";
//        String id = DocumentsContract.getDocumentId(uri);
        Cursor cursor = this.getContentResolver().query(uri,proj, null, null, null);

        if(cursor.moveToFirst()){
//            System.out.println("Came into cursor loop");
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            System.out.println(columnIndex);
            res = cursor.getString(columnIndex);
        }
        cursor.close();
        return res;
    }

}
