package com.beekay.thoughts.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Krishna by 15-11-2020
 */
@Entity(tableName = "reminder")
data class Reminder(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        @NonNull
        val id: Long = 0,

        @ColumnInfo(name = "reminder_about")
        @NonNull
        val reminderAbout: String,

        @ColumnInfo(name = "date_when")
        @NonNull
        val toBeDoneOn: String,

        @ColumnInfo(name = "createdon")
        @NonNull
        val createdOn: String,

        @ColumnInfo(name = "tag")
        @NonNull
        val tag: String,

        @ColumnInfo(name = "status")
        @NonNull
        val status: Boolean
) {
}