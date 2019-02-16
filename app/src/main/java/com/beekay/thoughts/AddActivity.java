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

import java.io.File;
import java.io.IOException;

public class AddActivity extends AppCompatActivity {

    EditText thoughtField;
    ImageView previewImage;
    ImageButton saveButton;
    String imgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        thoughtField = findViewById(R.id.thoughtField);
        previewImage = findViewById(R.id.add_img_preview);
        saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thoughtField.getText().toString().trim().length() == 0 ){
                    Toast.makeText(AddActivity.this, "Cannot imsert empty though", Toast.LENGTH_SHORT).show();
                } else {
                    DataOpener db = new DataOpener(AddActivity.this);
                    db.open();
                    try {
                        db.insert(thoughtField.getText().toString(), imgPath);
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
