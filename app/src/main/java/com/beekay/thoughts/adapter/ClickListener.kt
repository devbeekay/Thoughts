package com.beekay.thoughts.adapter

/**
 * Created by Krishna by 17-11-2020
 */
interface ClickListener<T> {
    fun onClick(entity: T, type: ClickType)
    fun onLongClick(entity: T)
}