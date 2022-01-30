package com.beekay.thoughts.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beekay.thoughts.db.ThoughtDB
import com.beekay.thoughts.viewmodel.UserViewModel

class UserViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(ThoughtDB.getDatabase(context).userDAO) as T
    }
}