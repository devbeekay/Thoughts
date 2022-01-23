package com.beekay.thoughts.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.beekay.thoughts.dao.ReminderDAO
import com.beekay.thoughts.dao.ThoughtDAO
import com.beekay.thoughts.model.Reminder
import com.beekay.thoughts.model.Thought

/**
 * Created by Krishna by 15-11-2020
 */
@Database(entities = [Thought::class, Reminder::class],
version = 1)
abstract class ThoughtDB: RoomDatabase() {

    abstract val thoughtDAO: ThoughtDAO
    abstract val reminderDAO: ReminderDAO

    companion object {
        @Volatile
        private lateinit var INSTANCE: ThoughtDB
        fun getDatabase(context: Context): ThoughtDB {
            synchronized(ThoughtDB::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            ThoughtDB::class.java, "thoughts.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }



}