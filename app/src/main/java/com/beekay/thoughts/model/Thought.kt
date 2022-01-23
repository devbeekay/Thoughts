package com.beekay.thoughts.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Krishna by 15-11-2020
 */
private val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
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
        val updatedOn: String = LocalDateTime.now().format(dateFormat),

        @ColumnInfo(name = "img_src")
        val imgSource: String?,

        @ColumnInfo(name = "starred")
        @NonNull
        var starred: Boolean = false
)