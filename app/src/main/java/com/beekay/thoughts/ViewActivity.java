package com.beekay.thoughts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.beekay.thoughts.model.Thought;

public class ViewActivity extends AppCompatActivity {

    TextView totalThoughtView;
    ImageView totalImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Changes Related to Night mode
        if (getIntent().getBooleanExtra("Mode", false)) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
            getWindow().setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
        totalImageView = findViewById(R.id.totalImage);
        totalThoughtView = findViewById(R.id.totalThought);
        Thought thought = (Thought) getIntent().getSerializableExtra("thoughtSelected");
        totalThoughtView.setText(thought.getThoughtText());
        if ( thought.getImg() != null ) {
            Bitmap myBitMap = BitmapFactory.decodeByteArray(thought.getImg(),0,thought.getImg().length);
            totalImageView.setImageBitmap(myBitMap);
        }
    }
}
