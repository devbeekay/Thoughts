package com.beekay.thoughts.model

data class Token(
    var uid: String,
    var token: String,
    var name: String
) {
    constructor(): this ("", "", "")
}