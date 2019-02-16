package com.beekay.thoughts;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.beekay.thoughts.adapter.ThoughtsAdapter;
import com.beekay.thoughts.model.Thought;
import com.beekay.thoughts.util.Utilities;
import com.facebook.FacebookSdk;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ThoughtsAdapter adapter;
    List<Thought> thoughts;
    Utilities utilities;
    SearchView sView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        utilities = new Utilities(this);
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        recyclerView = findViewById(R.id.recycle);
        thoughts = utilities.getThoughts();
        adapter = new ThoughtsAdapter(thoughts,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Came to activity Result");
        thoughts.clear();
        thoughts = utilities.getThoughts();
        adapter.swap(thoughts);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        sView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        sView.setMaxWidth(Integer.MAX_VALUE);
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        List<Thought> thoughts = utilities.getThoughts();
//        for (Thought thought : thoughts ) {
//            if(thought.getImgSource() != null) {
//                File imgFile = new File(thought.getImgSource());
//                if(imgFile.exists()){
//                    BitmapFactory.Options options = new BitmapFactory.Options();
////                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                    Bitmap img = BitmapFactory.decodeFile(thought.getImgSource());
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    img.compress(Bitmap.CompressFormat.PNG, 0, stream);
//                    DataOpener db = new DataOpener(this);
//                    db.open();
//                    db.insertImage(String.valueOf(thought.getId()), stream.toByteArray());
//                    db.close();
//                    Log.i("successfully",
//                            "migrated!");
//                }
//            }
//        }
        if (item.getItemId() == R.id.action_search){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!sView.isIconified()) {
            sView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
