package com.beekay.thoughts;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.beekay.thoughts.db.DataOpener;

public class NotificationAction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        boolean done = getIntent().getBooleanExtra("Done", false);
        int nId = getIntent().getIntExtra("NotificationID", 0);
        DataOpener db = new DataOpener(this);
        db.open();
        if(done) {
            db.updateReminder(nId, 1);
        } else {
            db.updateReminder(nId, 0);
        }
        db.close();
        nManager.cancel(nId);
        this.finish();
    }
}
