package com.beekay.thoughts.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.navigation.NavController
import com.beekay.thoughts.R
import com.beekay.thoughts.model.Thought
import com.beekay.thoughts.viewmodel.ThoughtViewModel
import java.io.File

class ThoughtsContextualCallback(
    private val context: Context,
    private val thought: Thought,
    private val viewHolder: ThoughtsAdapter.ThoughtViewHolder,
    private val thoughtViewModel: ThoughtViewModel,
    private val navController: NavController
): ActionMode.Callback {
    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        menu?.add("Delete")
        menu?.add("Edit")
        menu?.add("Copy")
        val selectIndicator = viewHolder.itemView.findViewById<LinearLayout>(R.id.selectIndicator)
        selectIndicator.visibility = View.VISIBLE
        thought.selected = true
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(actionMode: ActionMode?, item: MenuItem?): Boolean {
        item?.let {
            if (it.title == "Delete") {
                thoughtViewModel.deleteThought(thought)
                thought.imgSource?.let { fName ->
                    val imagesDir = File(context.filesDir, "images")
                    val img = File(imagesDir, fName)
                    if (img.exists()) {
                        img.delete()
                    }
                }
            } else if (it.title == "Edit") {
                thoughtViewModel.selectedThought.postValue(thought)
                navController.navigate(R.id.action_thoughtsFragment_to_addThoughtFragment)
            } else {
                val clipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val data =
                    ClipData.newPlainText("Thought", thought.thought)
                clipboardManager.setPrimaryClip(data)
            }
        }
        actionMode?.finish()
        return false
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        thought.selected = false
        val selectIndicator = viewHolder.itemView.findViewById<LinearLayout>(R.id.selectIndicator)
        selectIndicator.visibility = View.GONE
    }
}