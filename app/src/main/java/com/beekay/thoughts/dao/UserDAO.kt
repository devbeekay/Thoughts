package com.beekay.thoughts.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beekay.thoughts.model.User

@Dao
interface UserDAO {

    @Query("SELECT * FROM user")
    fun getUsers(): LiveData<User>

    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getUserByUid(uid: String) : User?

    @Insert(entity = User::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}