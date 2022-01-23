package com.beekay.thoughts.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.beekay.thoughts.model.Reminder

/**
 * Created by Krishna by 16-11-2020
 */
@Dao
interface ReminderDAO {

    @Query("SELECT * FROM reminder ORDER BY date_when DESC")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Insert(entity = Reminder::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminder: Reminder)

    @Delete(entity = Reminder::class)
    fun deleteReminder(reminder: Reminder)

    @Query("UPDATE reminder SET status = :i WHERE tag = :tag")
    suspend fun markReminder(tag: String, i: Int)

}