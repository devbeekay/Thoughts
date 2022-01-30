package com.beekay.thoughts.util

import com.beekay.thoughts.model.User

interface UserStatusListener {

    fun onSuccess(user: User)
    fun onError(message: String)
}