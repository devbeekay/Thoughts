//package com.beekay.thoughts;
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.widget.Toolbar;
//import android.widget.TextView;
//
//import com.beekay.thoughts.adapter.ThoughtsAdapter;
//import com.beekay.thoughts.util.Utilities;
//
//import java.util.List;
//
//public class StarredActivity extends AppCompatActivity {
//
//    RecyclerView recyclerView;
//    List<Thought> thoughts;
//    ThoughtsAdapter adapter;
//    Utilities utilities;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_starred);
//        //Night Mode settings
//        Intent i = getIntent();
//        Bundle intentBundle = i.getExtras();
//        boolean nightMode = intentBundle.containsKey("Mode") && intentBundle.getBoolean("Mode");
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        TextView toolText = toolbar.findViewById(R.id.toolbar_title);
//        toolText.setText("");
//        setSupportActionBar(toolbar);
////        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        if (nightMode) {
//            toolbar.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
//            getWindow().setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
//            getWindow().getDecorView().setSystemUiVisibility(0);
//        }
//
//        utilities = new Utilities(this);
//
//        recyclerView = findViewById(R.id.starredRecycle);
//        thoughts = utilities.getStarredThoughts();
//        adapter = new ThoughtsAdapter(thoughts, this, nightMode);
//        recyclerView.setLayoutManager(new LinearLayoutManager(StarredActivity.this));
//        recyclerView.setAdapter(adapter);
//    }
//}
