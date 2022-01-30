package com.beekay.thoughts.viewmodel

import androidx.annotation.UiThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beekay.thoughts.dao.UserDAO
import com.beekay.thoughts.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserViewModel(private val userDAO: UserDAO) : ViewModel() {

    @UiThread
    fun getUserById(id: String): User? {
        return runBlocking { userDAO.getUserByUid(id) }
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            userDAO.insertUser(user)
        }
    }
}