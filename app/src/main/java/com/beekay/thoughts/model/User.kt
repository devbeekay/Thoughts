package com.beekay.thoughts.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    var uid: String,
    @NonNull
    @ColumnInfo(name = "name")
    val name: String,
    @NonNull
    @ColumnInfo(name = "email_id")
    val emailId: String
) {
    constructor() : this("", "", "")
}
