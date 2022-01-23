package com.beekay.thoughts.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.beekay.thoughts.R
import java.util.*

/**
 * Created by Krishna by 21-11-2020
 */
class ShowNotification(private val appContext: Context, private val workerParameters: WorkerParameters):
        Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        val content = workerParameters.inputData.getString("remindAbout")
        val nManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val builder = try {
                val nChannel = nManager.getNotificationChannel("9395")
                Notification.Builder(appContext, "9395").apply {
                    setChannelId(nChannel.id)
                }
            } catch(ex: Exception) {
                createNotificationChannel(nManager)
                Notification.Builder(appContext, "9395").apply {
                    setChannelId("9395")
                }
            }

        val nId = Random().nextInt()

        val doneIntent = Intent(appContext, NotificationReceiver::class.java)
        doneIntent.action = "Mark as done"
        doneIntent.putExtra("tag", workerParameters.inputData.getString("tag"))
        doneIntent.putExtra("id", nId)
        val donePendingIntent = PendingIntent.getBroadcast(appContext, 1,
                doneIntent, PendingIntent.FLAG_ONE_SHOT)


        val missedIntent = Intent(appContext, NotificationReceiver::class.java)
        missedIntent.action = "Mark as missed"
        missedIntent.putExtra("tag", workerParameters.inputData.getString("tag"))
        missedIntent.putExtra("id", nId)
        val missedPendingIntent = PendingIntent.getBroadcast(appContext, 1,
                missedIntent, PendingIntent.FLAG_ONE_SHOT)

        builder.setSmallIcon(R.drawable.ic_remider)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentTitle("Reminder")
                .setContentText(content)
                .addAction(R.drawable.ic_done, "Mark as done", donePendingIntent)
                .addAction(R.drawable.ic_not_done, "Mark as missed", missedPendingIntent)
        nManager.notify(nId, builder.build())
        return Result.success()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(nManager: NotificationManager) {
        val nChannel = NotificationChannel("9395", "Reminders", NotificationManager.IMPORTANCE_HIGH)
        val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
        nChannel.apply {
            description = "Notification channel for Reminders"
            enableLights(true)
            vibrationPattern = longArrayOf(0, 200, 200, 200)
            setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes)
        }
        nManager.createNotificationChannel(nChannel)
    }
}