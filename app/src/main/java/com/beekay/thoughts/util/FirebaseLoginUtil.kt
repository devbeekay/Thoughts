package com.beekay.thoughts.util

import com.beekay.thoughts.model.Token
import com.beekay.thoughts.model.User
import com.beekay.thoughts.viewmodel.UserViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseLoginUtil(
    private val userStatusListener: UserStatusListener,
    private val viewModel: UserViewModel
) {

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().reference
    private lateinit var token: Token

    fun isAuthenticated() {
        auth.currentUser?.let {
            viewModel.getUserById(it.uid)?.let { vUser ->
                userStatusListener.onSuccess(vUser)
            } ?: userStatusListener.onError("User not found. Please re-login")
        } ?: userStatusListener.onError("User not found. Please re-login")
    }

    fun login(user: String, pass: String) {
        auth.signInWithEmailAndPassword(user, pass)
            .addOnCompleteListener(loginCompleteListener)
    }

    private val loginCompleteListener = OnCompleteListener<AuthResult> {
        if (it.isSuccessful) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(authTokenListener)
        } else {
            userStatusListener.onError(it.exception?.message ?: "Failed to login")
        }
    }

    private val existingTokenListener = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                for (p in p0.children) {
                    dbRef.child("tokens").child(p.key!!).setValue(token)
                }
            } else {
                dbRef.child("tokens").push().setValue(token)
            }

            dbRef.child("users").orderByChild("uid").equalTo(auth.currentUser?.uid).limitToFirst(1)
                .addListenerForSingleValueEvent(userValueEventListener)
        }

        override fun onCancelled(p0: DatabaseError) {

        }
    }

    private val authTokenListener = OnCompleteListener<String> {
        if (it.isSuccessful) {
            val user = auth.currentUser
            token = Token(user?.uid!!, it.result!!, user.displayName!!)
            dbRef.child("tokens").orderByChild("uid").equalTo(user.uid).limitToFirst(1)
                .addListenerForSingleValueEvent(existingTokenListener)
        } else {
            userStatusListener.onError(it.exception!!.message!!)
        }
    }

    private val userValueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.children.singleOrNull()?.let {
                it.getValue(User::class.java)?.let { vUser ->
                    viewModel.insertUser(vUser)
                    userStatusListener.onSuccess(vUser)
                } ?: userStatusListener.onError("Could not decode the user")
            } ?: userStatusListener.onError("Could not find the user")
        }

        override fun onCancelled(p0: DatabaseError) {
            userStatusListener.onError(p0.message)
        }
    }
}