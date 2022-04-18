package com.example.project_uqac.ui.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class AppUtil {

    companion object {

        private const val SECOND_MILLIS: Int = 1000
        private const val MINUTE_MILLIS: Int = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS: Int = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS: Int = 24 * HOUR_MILLIS
    }

    fun getUserID(): String? {
        val db = Firebase.database
        val auth = Firebase.auth
        var userID : String? = null
        db.reference.child("Users_ID").get().addOnSuccessListener {
            it.getValue<Map<String, String>>()!!.forEach {
                if (it.value == auth.currentUser?.email) {
                    userID = it.key
                }
            }
        }
        return userID
    }

}