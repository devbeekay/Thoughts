package com.beekay.thoughts.model

data class ThoughtDao(
    val id: Long,
    val thoughtText: String,
    val timestamp: String,
    val imgSource: String?,
    val img: ByteArray?,
    val starred: Boolean
)
