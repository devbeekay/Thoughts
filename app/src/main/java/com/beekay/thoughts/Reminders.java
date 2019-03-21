package com.beekay.thoughts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.beekay.thoughts.adapter.RemindersAdapter;
import com.beekay.thoughts.model.Reminder;
import com.beekay.thoughts.util.Utilities;

import java.util.List;

public class Reminders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Intent i = getIntent();
        Bundle intentBundle = i.getExtras();
        boolean nightMode = intentBundle.containsKey("Mode") && intentBundle.getBoolean("Mode");
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolText = toolbar.findViewById(R.id.toolbar_title);
        toolText.setText("Reminders");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (nightMode) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
            getWindow().setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
        Utilities utilities = new Utilities(this);
        List<Reminder> reminders = utilities.getReminders();
        RecyclerView rView = findViewById(R.id.recycleReminders);
        rView.setLayoutManager(new LinearLayoutManager(Reminders.this));
        RemindersAdapter adapter = new RemindersAdapter(reminders);
        rView.setAdapter(adapter);
    }
}
