package com.beekay.thoughts.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beekay.thoughts.db.ThoughtDB
import com.beekay.thoughts.viewmodel.ThoughtViewModel

/**
 * Created by Krishna by 15-11-2020
 */
class ThoughtViewModelFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ThoughtViewModel(ThoughtDB.getDatabase(context).thoughtDAO) as T
    }

}