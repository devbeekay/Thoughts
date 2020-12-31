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
version = 9)
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
                    ).addMigrations(MIGRATION_7_8, MIGRATION_8_9).build()
                }
            }
            return INSTANCE
        }
        private val MIGRATION_7_8 = object: Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                database.execSQL("CREATE TABLE IF NOT EXISTS thought(tid INTEGER PRIMARY KEY AUTOINCREMENT not null, thought_text text not null, createdon text not null, updatedon text not null, img_src text, starred integer not null)")
                database.execSQL("insert into thought (tid, thought_text, createdon, updatedon, img_src, starred) \n" +
                        "select id as tid, thought_text, t.timestamp as createdon, t.timestamp as updatedon, image_src as img_src, starred from thoughts t;")
                database.execSQL("drop table thoughts")
                database.execSQL("CREATE TABLE IF NOT EXISTS reminder(id INTEGER PRIMARY KEY AUTOINCREMENT not null, reminder_about text not null, date_when text not null, createdon text not null, status integer not null)")
                database.execSQL("insert into reminder (id, reminder_about, date_when, createdon, status) " +
                        "SELECT id as id, reminder as reminder_about, r.timestamp as createdon, date_when as date_when, done as status from reminders r")
                database.execSQL("drop table reminders")
                database.setTransactionSuccessful()
                database.endTransaction()
            }

        }

        private val MIGRATION_8_9 = object: Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                database.execSQL("ALTER TABLE reminder ADD COLUMN tag TEXT NOT NULL DEFAULT ''")
                database.setTransactionSuccessful()
                database.endTransaction()
            }
        }
    }



}