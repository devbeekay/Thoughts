package com.beekay.thoughts.ui

import android.content.Context
import android.view.View
import androidx.appcompat.widget.SearchView
import com.beekay.thoughts.databinding.ToolbarBinding

class ExpandEnableSearchView(context: Context) : SearchView(context) {

    lateinit var toolbar: ToolbarBinding
    override fun onActionViewExpanded() {
        toolbar.toolbarTitle.visibility = View.GONE
        super.onActionViewExpanded()
    }
}