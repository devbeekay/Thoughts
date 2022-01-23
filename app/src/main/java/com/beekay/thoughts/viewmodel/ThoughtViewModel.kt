package com.beekay.thoughts.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beekay.thoughts.dao.ThoughtDAO
import com.beekay.thoughts.model.Thought
import kotlinx.coroutines.launch

/**
 * Created by Krishna by 16-11-2020
 */
class ThoughtViewModel(private val thoughtDAO: ThoughtDAO) : ViewModel() {

    val onlyStarred = MutableLiveData<Boolean>(false)

    val thoughts = Transformations.switchMap(onlyStarred) {
        if (it) {
            thoughtDAO.getStarredThoughts()
        } else {
            thoughtDAO.getAllThoughts()
        }
    }

    val selectedThought = MutableLiveData<Thought?>(null)

    fun insertThought(thought: Thought) {
        viewModelScope.launch {
            thoughtDAO.insertThought(thought)
        }
    }

    fun deleteThought(thought: Thought) {
        viewModelScope.launch {
            thoughtDAO.deleteThought(thought)
        }
    }

}