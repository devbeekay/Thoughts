package com.beekay.thoughts;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beekay.thoughts.adapter.ThoughtsAdapter;
import com.beekay.thoughts.db.DataOpener;
import com.beekay.thoughts.model.Thought;
import com.beekay.thoughts.receivers.ScheduledNotification;
import com.beekay.thoughts.util.Utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ThoughtsAdapter adapter;
    List<Thought> thoughts;
    Utilities utilities;
    SearchView sView;
    List<Thought> searchedThoughts = new ArrayList<>();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    boolean nightMode;
    boolean secondaryFab = false;

    EditText dialogTime;
    SimpleDateFormat format = new SimpleDateFormat("d-M-Y k:m a");
    Calendar calendar = null;
    Toolbar toolbar;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_main);

            // Get night mode settings and apply them
            setMode();

            utilities = new Utilities(this);

            toolbar = findViewById(R.id.toolbar);
            toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

            //Changes related to night mode. Make toolbar dark and remove status bar settings via setSystemUiVisibility(0)
            if (nightMode) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
                getWindow().setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
                getWindow().getDecorView().setSystemUiVisibility(0);
            }
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            recyclerView = findViewById(R.id.recycle);
            thoughts = utilities.getThoughts();

            adapter = new ThoughtsAdapter(thoughts, this, nightMode);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            recyclerView.setAdapter(adapter);

            final FloatingActionButton fab = findViewById(R.id.fab_thought);
            final FloatingActionButton fabReminder = findViewById(R.id.fab_reminder);

            fabReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Dialog);
                    dialog.setTitle("Add Reminder");
                    dialog.setContentView(R.layout.dialog_reminder);
                    dialogTime = dialog.findViewById(R.id.reminder_date_field);
                    dialogTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDateDialog();
                        }
                    });
                    final EditText reminderField = dialog.findViewById(R.id.reminder_field);
                    Button cancel = dialog.findViewById(R.id.reminder_cancel);
                    Button ok = dialog.findViewById(R.id.reminder_ok);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (reminderField.getText() == null ||
                                    reminderField.getText().toString() == null ||
                                    reminderField.getText().toString().trim().length() == 0 ||
                                    calendar == null) {
                                Toast.makeText(MainActivity.this, "Both the fields are Mandator",
                                        Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            } else {
                                setReminder(reminderField.getText().toString(), calendar.getTime());
                                dialog.cancel();
                            }
                        }
                    });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            });
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (secondaryFab) {
                        secondaryFab = false;
                        fabReminder.hide();
//                        RotateAnimation rotate = new RotateAnimation(45f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                        rotate.setFillAfter(true);
//                        fab.startAnimation(rotate);
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white));
                        Intent intent = new Intent(MainActivity.this, AddActivity.class);
                        intent.putExtra("Edit", false);
                        intent.putExtra("Mode", nightMode);
                        startActivityForResult(intent, 1);
                    } else {
//                        RotateAnimation rotate = new RotateAnimation(0f, 45f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                        rotate.setFillAfter(true);
//                        fab.startAnimation(rotate);
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_note_add));
                        fabReminder.show();
                        secondaryFab = true;
                    }
                }
            });
//            throw new Exception("something");
        } catch (Exception ex) {
            //fallback export db save your ass\
            ex.printStackTrace();
            backupDB();
        }
    }

    private void setReminder(String reminder, Date date) {
        System.out.println(date);
        DataOpener db = new DataOpener(MainActivity.this);
        db.open();
        int nId = (int) db.insertReminder(reminder, date);
        db.close();
        Intent intent = new Intent(this, ScheduledNotification.class);
        intent.putExtra("Reminder", reminder);
        intent.putExtra("nId", nId);
        PendingIntent pIndent = PendingIntent
                .getBroadcast(this, nId, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pIndent);

        Toast.makeText(this, "Reminder set at " + format.format(date), Toast.LENGTH_LONG).show();
    }

    private void showDateDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Dialog);
        dialog.setTitle("Select Date and Time");
        dialog.setContentView(R.layout.dialog_date_time_picker);
        final CalendarView cv = dialog.findViewById(R.id.dateView);
        Button cancelButton = dialog.findViewById(R.id.dateCancel);
        Button setButton = dialog.findViewById(R.id.dateSet);
        final Spinner hoursSpinner = dialog.findViewById(R.id.hours);
        final Spinner minutesSpinner = dialog.findViewById(R.id.minutes);
        final Spinner msSpinner = dialog.findViewById(R.id.ms);
        final Integer[] hours = new Integer[12];
        final Integer[] minutes = new Integer[60];
        for (int i = 0; i < 12; i++) {
            hours[i] = i;
        }
        for (int i = 0; i <= 59; i++) {
            minutes[i] = i;
        }
        String[] ms = new String[]{"AM", "PM"};
        hoursSpinner.setAdapter(new ArrayAdapter<Integer>(
                MainActivity.this, android.R.layout.simple_list_item_1, hours));
        minutesSpinner.setAdapter(new ArrayAdapter<Integer>(
                MainActivity.this, android.R.layout.simple_list_item_1, minutes));
        msSpinner.setAdapter(new ArrayAdapter<>(
                MainActivity.this, android.R.layout.simple_list_item_1, ms));
        calendar = Calendar.getInstance();
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(
                    @NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = hoursSpinner.getSelectedItem() == null
                        ? 6 : (int) hoursSpinner.getSelectedItem();

                calendar.set(Calendar.HOUR_OF_DAY,
                        msSpinner.getSelectedItem().equals("AM") ? hour : hour + 12);
                calendar.set(Calendar.MINUTE,
                        minutesSpinner.getSelectedItem() == null ?
                                0 : (int) minutesSpinner.getSelectedItem());

                dialogTime.setText(format.format(calendar.getTime()));
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void setMode() {
        sharedPreferences = getSharedPreferences("modePreference", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("night_mode")) {
            nightMode = sharedPreferences.getBoolean("night_mode", false);
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            }
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("night_mode", false);
            nightMode = false;
            editor.commit();
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            System.out.println("Came to activity Result");
            thoughts.clear();
            thoughts = utilities.getThoughts();
            adapter.swap(thoughts);
        } else if (requestCode == 101) {
            String uri = data.getData().getPath();
            Toast.makeText(this, uri, Toast.LENGTH_LONG).show();
            importDb(data.getData());
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_night_mode);
        if (nightMode) {
            item.setTitle("Disable Night Mode");
        } else {
            item.setTitle("Enable Night Mode");
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        sView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        sView.setQueryHint("Search here");
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() == 0) {
                    adapter.swap(thoughts);
                } else {
                    searchedThoughts.clear();
                    for (Thought t : thoughts) {
                        if (t.getThoughtText().toLowerCase().contains(query.toLowerCase())) {
                            searchedThoughts.add(t);
                        }
                    }
                    adapter.swap(searchedThoughts);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        sView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarTitle.setVisibility(View.GONE);
            }
        });

        sView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toolbarTitle.setVisibility(View.VISIBLE);
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
        if (item.getItemId() == R.id.action_night_mode) {
            if (nightMode) {
                nightMode = false;
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            } else {
                nightMode = true;
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            }
            editor = sharedPreferences.edit();
            editor.putBoolean("night_mode", nightMode);
            editor.commit();
            this.recreate();
        } else if (item.getItemId() == R.id.action_starred) {
            Intent intent = new Intent(MainActivity.this, StarredActivity.class);
            intent.putExtra("Mode", nightMode);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_reminders) {
            Intent intent = new Intent(MainActivity.this, Reminders.class);
            intent.putExtra("Mode", nightMode);
            startActivity(intent);
        } else if (item.getItemId() == R.id.exportData) {
            exportDb();
        } else if (item.getItemId() == R.id.importData) {
            importData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void importData() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 101);
    }

    @Override
    public void onBackPressed() {
        if (!sView.isIconified()) {
            sView.setIconified(true);
            adapter.swap(thoughts);
            return;
        }
        super.onBackPressed();
    }

    private void backupDB() {
        setContentView(R.layout.fallback);
        Button export = findViewById(R.id.exportDB);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDb();

            }
        });
    }

    private void exportDb() {
        final int BUFFER = 2048;
        try {
            File download_folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File desFile = new File(download_folder.getAbsolutePath() + "//thougts.zip");
            File data = Environment.getDataDirectory();
            File db = new File(data.getAbsolutePath() + "//data//" + getPackageName() + "//databases");
            File images = new File(data.getAbsolutePath() + "//data//" + getPackageName() + "//files//images");

            FileOutputStream fos = new FileOutputStream(desFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            File[] files = db.listFiles();
            for (File f : files) {
                byte[] buf = new byte[BUFFER];
                FileInputStream fis = new FileInputStream(f);
                zos.putNextEntry(new ZipEntry(f.getAbsolutePath()));
                int length;
                while ((length = fis.read(buf)) > 0) {
                    zos.write(buf, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }

            File[] imageList = images.listFiles();

            for (File f : imageList) {
                byte[] buf = new byte[BUFFER];
                FileInputStream fis = new FileInputStream(f);
                zos.putNextEntry(new ZipEntry(f.getAbsolutePath()));
                int length;
                while ((length = fis.read(buf)) > 0) {
                    zos.write(buf, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }

            zos.close();
            Toast.makeText(this, "Data Exported to Downloads Folder", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, "Could Not Export Data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Could Not Export Data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void importDb(Uri path) {
        ZipInputStream zis = null;
        ContentResolver cr = getContentResolver();
        try {
            zis = new ZipInputStream(cr.openInputStream(path));
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[2048];
            while ((ze = zis.getNextEntry()) != null) {
                File f = new File("/", ze.getName());
                File dir = ze.isDirectory() ? f : f.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs()) {
                    throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                }

                if (ze.isDirectory()) {
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(f);
                try {
                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }
                } finally {
                    fout.close();
                }
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, "Could not import db", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
