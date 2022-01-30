package com.beekay.thoughts.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.beekay.thoughts.dao.ReminderDAO
import com.beekay.thoughts.dao.ThoughtDAO
import com.beekay.thoughts.dao.UserDAO
import com.beekay.thoughts.model.Reminder
import com.beekay.thoughts.model.Thought
import com.beekay.thoughts.model.User

/**
 * Created by Krishna by 15-11-2020
 */
@Database(entities = [Thought::class, Reminder::class, User::class],
version = 2)
abstract class ThoughtDB: RoomDatabase() {

    abstract val thoughtDAO: ThoughtDAO
    abstract val reminderDAO: ReminderDAO
    abstract val userDAO: UserDAO

    companion object {
        @Volatile
        private lateinit var INSTANCE: ThoughtDB
        fun getDatabase(context: Context): ThoughtDB {
            synchronized(ThoughtDB::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ThoughtDB::class.java, "thoughts.db"
                    ).addMigrations(MIGRATION_1_2).build()
                }
            }
            return INSTANCE
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                database.execSQL("CREATE TABLE IF NOT EXISTS user(uid TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, email_id TEXT NOT NULL)")
                database.setTransactionSuccessful()
                database.endTransaction()
            }
        }
    }

}