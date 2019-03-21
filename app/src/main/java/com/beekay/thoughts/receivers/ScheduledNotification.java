package com.beekay.thoughts.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.beekay.thoughts.NotificationAction;
import com.beekay.thoughts.R;

public class ScheduledNotification extends BroadcastReceiver {

    NotificationManager nManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String reminder = intent.getStringExtra("Reminder");
        int nId = intent.getIntExtra("nId", 0);

        Intent failIntent = new Intent(context, NotificationAction.class);
        failIntent.putExtra("NotificationID", nId);
        failIntent.putExtra("Done", false);

        Intent successIntent = new Intent(context, NotificationAction.class);
        successIntent.putExtra("NotificationID", nId);
        successIntent.putExtra("Done", true);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_remider);
        builder.setContentText(reminder);
        builder.setSubText("You have a reminder");
        builder.setOngoing(true);
        builder.addAction(R.drawable.ic_not_done,
                "Not Done",
                PendingIntent
                        .getActivity(context, 1, failIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.addAction(R.drawable.ic_done,
                "Done",
                PendingIntent
                        .getActivity(context, 1, successIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("9473",
                    "Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications For Reminders");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{200,200});
            builder.setChannelId("9473");
            nManager.createNotificationChannel(channel);

        }

//        Notification notification = new Notification(R.mipmap.ic_launcher, reminder, System.currentTimeMillis());
        Notification notification = builder.build();
        System.out.println("Showing Notification With ID " + nId);
        nManager.notify(nId, notification);
    }
}
