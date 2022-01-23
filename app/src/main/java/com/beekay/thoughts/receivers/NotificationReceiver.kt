package com.beekay.thoughts.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.beekay.thoughts.db.ThoughtDB
import com.beekay.thoughts.viewmodel.ReminderViewModel
import com.beekay.thoughts.viewmodel.factory.ReminderViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderDao = ThoughtDB.getDatabase(context).reminderDAO
        val action = intent.action
        val tag = intent.getStringExtra("tag")
        val nId = intent.getIntExtra("id", -1)
        if (null != action && null != tag) {
            if (action == "Mark as done") {
                GlobalScope.launch {
                    reminderDao.markReminder(tag, 1)
                }

            } else {
                GlobalScope.launch {
                    reminderDao.markReminder(tag, 0)
                }

            }
            if (nId != -1) {
                val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
                nManager.cancel(nId)
            }
        }
    }
}