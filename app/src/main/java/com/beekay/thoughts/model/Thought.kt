package com.beekay.thoughts.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Krishna by 15-11-2020
 */
@Entity(tableName = "thought")
data class Thought(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "tid")
        @NonNull
        val id: Long = 0,

        @ColumnInfo(name = "thought_text")
        @NonNull
        val thought: String,

        @ColumnInfo(name = "createdon")
        @NonNull
        val createdOn: String,

        @ColumnInfo(name = "updatedon")
        @NonNull
        val updatedOn: String,

        @ColumnInfo(name = "img_src")
        val imgSource: String?,

        @ColumnInfo(name = "starred")
        @NonNull
        var starred: Boolean = false
)