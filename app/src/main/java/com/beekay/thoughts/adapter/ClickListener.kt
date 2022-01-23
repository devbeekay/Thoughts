package com.beekay.thoughts.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Krishna by 17-11-2020
 */
interface ClickListener<T> {
    fun onClick(entity: T, type: ClickType)
    fun onLongClick(entity: T, viewHolder: RecyclerView.ViewHolder)
}