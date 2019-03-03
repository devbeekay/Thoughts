package com.beekay.thoughts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.beekay.thoughts.adapter.ThoughtsAdapter;
import com.beekay.thoughts.model.Thought;
import com.beekay.thoughts.util.Utilities;

import java.util.List;

public class StarredActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Thought> thoughts;
    ThoughtsAdapter adapter;
    Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starred);
        //Night Mode settings
        Intent i = getIntent();
        Bundle intentBundle = i.getExtras();
        boolean nightMode = intentBundle.containsKey("Mode") && intentBundle.getBoolean("Mode");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (nightMode) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
            getWindow().setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
            getWindow().getDecorView().setSystemUiVisibility(0);
        }

        utilities = new Utilities(this);

        recyclerView = findViewById(R.id.starredRecycle);
        thoughts = utilities.getStarredThoughts();
        adapter = new ThoughtsAdapter(thoughts, this, nightMode);
        recyclerView.setLayoutManager(new LinearLayoutManager(StarredActivity.this));
        recyclerView.setAdapter(adapter);
    }
}
