package com.beekay.thoughts.ui.auth

import com.beekay.thoughts.model.Token
import com.beekay.thoughts.model.User
import com.beekay.thoughts.util.UserStatusListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseSignUpUtil(
    private val userStatusListener: UserStatusListener
) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().reference
    private lateinit var user: FirebaseUser

    lateinit var cUser: User
    lateinit var name: String
    lateinit var mail: String
    lateinit var pass: String

    fun signUp(name: String, mail: String, pass: String) {
        this.name = name
        this.mail = mail
        this.pass = pass
        auth.createUserWithEmailAndPassword(mail, pass)
            .addOnCompleteListener(registerAuthListener)
    }

    private val registerAuthListener = OnCompleteListener<AuthResult> {
        if (it.isSuccessful) {
            user = auth.currentUser!!
            val pChangeReq: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(pChangeReq).addOnCompleteListener(profileChangeListener)

        } else {
            userStatusListener.onError(it.exception!!.message!!)
        }
    }

    private val profileChangeListener = OnCompleteListener<Void> {
        if (it.isSuccessful) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(authTokenListener)
        } else {
            userStatusListener.onError(it.exception!!.message!!)
        }
    }

    private val authTokenListener = OnCompleteListener<String> {
        if (it.isSuccessful) {
            val token = Token(user.uid, it.result!!, name)
            dbRef.child("tokens").push().setValue(token)
            cUser = User(name = user.displayName!!, emailId = user.email!!, uid = user.uid)
            dbRef.child("users").push().setValue(cUser)
            dbRef.child("users").orderByChild("emailId").equalTo(user.getEmail()).limitToFirst(1)
                .addListenerForSingleValueEvent(userValueEventListener)
        } else {
            userStatusListener.onError(it.exception!!.message!!)
        }
    }

    private val userValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.children.singleOrNull()?.let {
                it.getValue(User::class.java)?.let { vUser ->
                    userStatusListener.onSuccess(vUser)
                } ?: userStatusListener.onError("Could not decode the user")
            } ?: userStatusListener.onError("Could not find the user")
        }

        override fun onCancelled(dataSnapShot: DatabaseError) {

        }
    }
}