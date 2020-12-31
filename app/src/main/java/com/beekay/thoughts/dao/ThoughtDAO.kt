package com.beekay.thoughts.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.beekay.thoughts.model.Thought

/**
 * Created by Krishna by 15-11-2020
 */
@Dao
interface ThoughtDAO {

    @Query("SELECT * FROM thought ORDER BY tid desc")
    fun getAllThoughts(): LiveData<List<Thought>>

    @Query("SELECT * FROM thought WHERE starred='1' ORDER BY tid desc")
    fun getStarredThoughts(): LiveData<List<Thought>>

    @Insert(entity = Thought::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertThought(thought: Thought)

    @Delete(entity = Thought::class)
    fun deleteThought(thought: Thought)

}