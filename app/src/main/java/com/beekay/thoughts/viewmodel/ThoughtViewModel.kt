package com.beekay.thoughts.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beekay.thoughts.dao.ThoughtDAO
import com.beekay.thoughts.model.Thought
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Krishna by 16-11-2020
 */
class ThoughtViewModel(private val thoughtDAO: ThoughtDAO): ViewModel() {

    val thoughts = thoughtDAO.getAllThoughts()

    fun insertThought(thought: Thought) {
        GlobalScope.launch {
            thoughtDAO.insertThought(thought)
        }
    }

    fun deleteThought(thought: Thought) {
        thoughtDAO.deleteThought(thought)
    }

}