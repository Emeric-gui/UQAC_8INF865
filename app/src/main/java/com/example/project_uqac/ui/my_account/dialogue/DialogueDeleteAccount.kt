package com.example.project_uqac.ui.my_account.dialogue

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.my_account.MyAccountLogged
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DialogueDeleteAccount:DialogFragment() {

    private lateinit var mMyAccountLogged : MyAccountLogged
    private lateinit var db: FirebaseDatabase

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        var builder : AlertDialog.Builder = AlertDialog.Builder(activity)

        builder.setMessage(R.string.verification_suppression_message)
        builder.setTitle(R.string.verification_suppression)

        builder.setCancelable(false)
        builder.setNegativeButton(getString(R.string.annuler), null)
        builder.setPositiveButton(getString(R.string.confirmer)) { _: DialogInterface, _: Int ->

            // TODO Delete Account here
            //removeDiscutions()
            val user = Firebase.auth.currentUser!!
            val email : String? = user.email;
            // Delete profil pic in our storage
            if(user.photoUrl != null) {
                try {
                    Firebase.storage.reference.child("profil_pics/" + user.email).delete()
                } catch (e: Exception) {

                }
            }
            // Delete user account
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User account deleted.")
                        // Show back login page
                        if (email != null) {
                            mMyAccountLogged.deleteAllPosts(email)
                        }
                        mMyAccountLogged.goBackLogin(true)
                    }
                }
        }

        return builder.create()
    }

    private fun removeDiscutions() {
        db = Firebase.database
        var userID: String? = null
        var otherUserID: String? = null
        var listIdConversation: ArrayList<String>? = ArrayList()
        var listIdChat: ArrayList<String>? = ArrayList()
        var otherUserMail :String

        // We get the id of the user, conversations and chats
        val userIDRef = db.reference.get().addOnSuccessListener {
            val root = it
            // We get the ID of the user
            root.child("Users_ID").getValue<Map<String, String>>()!!.forEach {
                if (it.value == Firebase.auth.currentUser?.email) {
                    userID = it.key
                }
            }
            // We get the ID of the user's conversations
            userID?.let { it1 ->
                root.child("Users").child(it1).child("Conversations").children.forEach {
                    it.key?.let {
                        //Add ID conversation to the list
                            it2 -> listIdConversation?.add(it2)
                        //Get the id of the discution's chat
                        root.child("Conversations").child(it2).child("chat").getValue<String>()
                            ?.let { it3 -> listIdChat?.add(it3) }

                        //GEt the mail of the other user
                        if(root.child("Conversations").child(it2).child("user1Mail").getValue<String>()==Firebase.auth.currentUser?.email)
                        {
                            otherUserMail= root.child("Conversations").child(it2).child("user2Mail").getValue<String>().toString()
                        }else{
                            otherUserMail= root.child("Conversations").child(it2).child("user1Mail").getValue<String>().toString()
                        }

                        //Get the id of the other user
                        root.child("Users_ID").getValue<Map<String, String>>()!!.forEach {
                            if (it.value == otherUserMail) {
                                otherUserID = it.key
                            }
                        }

                        //Remove the conversation to the other user
                        otherUserID?.let { it3 -> db.reference.child("Users").child(it3).child("Conversations").child(it2).removeValue() }
                    }
                }
            }
            // We remove all the value
            listIdChat?.forEach {
                db.reference.child("Chat").child(it).removeValue()
            }
            listIdConversation?.forEach {
                db.reference.child("Conversations").child(it).removeValue()
                db.reference.child("Users")
            }

            userID?.let {
                db.reference.child("Users").child(it).removeValue()
                db.reference.child("Users_ID").child(it).removeValue()
            }
        }
    }

    fun arguments(elem : MyAccountLogged) {
        mMyAccountLogged = elem
    }
}