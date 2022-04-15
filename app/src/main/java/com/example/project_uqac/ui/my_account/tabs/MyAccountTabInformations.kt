package com.example.project_uqac.ui.my_account.tabs

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.my_account.MyAccountLogged
import com.example.project_uqac.ui.my_account.dialogue.DialogueDeleteAccount
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_account_informations.*

class MyAccountTabInformations : Fragment() {

    lateinit var username : String
    lateinit var email : String
    private lateinit var db: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)
        val view =  inflater.inflate(R.layout.fragment_my_account_informations, container, false)

        var user = Firebase.auth.currentUser
        val usernameInput : EditText = view.findViewById(R.id.username_textedit)
        val emailInput : EditText = view.findViewById(R.id.email_textedit)
        if (user != null) {
            usernameInput.setText(user.displayName.toString())
            username = user.displayName.toString()
            emailInput.setText(user.email.toString())
            email = user.email.toString()
        } else {
            usernameInput.setText("Error")
            emailInput.setText("Error")
        }


//        var toastTriggered : Boolean = false
        val buttonUpdateAccount : Button = view.findViewById(R.id.update_account)
        buttonUpdateAccount.setOnClickListener(){
            upUsername()
            Toast.makeText(context, "Vos informations ont ét mises à jour.", Toast.LENGTH_SHORT).show()
        }

        val buttonDeleteAccount : Button = view.findViewById(R.id.delete_account)
        buttonDeleteAccount.setOnClickListener(){
            //creation du fragment de dialogue
            val dialogPage = DialogueDeleteAccount()

            //ajout des infos dans le dialog
            dialogPage.arguments(this.parentFragment as MyAccountLogged)

            dialogPage.show(childFragmentManager, "Custom Dialog")
        }

//        val buttonDeleteAccount : Button = view.findViewById(R.id.delete_account)
//        buttonDeleteAccount.setOnClickListener(){
//            if (user != null) {
//                FirebaseAuth.getInstance().signOut()
////                val fragment = MyAccountLogged()
////                val transaction = fragmentManager?.beginTransaction()
////                transaction?.remove(fragment)?.commit()
////                Log.d("TAG", "Terminado pepito")
////                user = Firebase.auth.currentUser
//                val frag: MyAccountLogged? = this.parentFragment as MyAccountLogged?
//                frag?.goBackLogin()
////                parentFragmentManager.beginTransaction().replace(
////                    R.id.nav_host_fragment_activity_main,
////                    MyAccountFragment()
////                ).commit()
//
//            } else {
//                Log.d("TAG", "C po ko wtf")
//            }
//        }

        return view
    }

    fun upUsername(){
        val user = Firebase.auth.currentUser
        // Up username
        if(username_textedit.text.toString() != username){
//                if(!toastTriggered){
//                    toastTriggered = true
//                }
            val profileUpdates = userProfileChangeRequest {
                displayName = username_textedit.text.toString()
            }
            changeUserName(username_textedit.text.toString())
            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { innerTask ->
                    if (innerTask.isSuccessful) {
                        username = username_textedit.text.toString()
                        val frag: MyAccountLogged? = this.parentFragment as MyAccountLogged?
                        frag?.updateUsername(username)
                        upEmail()
                    } else {
                        Log.d(ContentValues.TAG, "Error with username.")
                    }
                }
        } else {
            upEmail()
        }
    }

    fun upEmail(){

        // Up email
        if(email_textedit.text.toString() != email){
//                if(!toastTriggered){
//                    toastTriggered = true
//                    Toast.makeText(context, "Vos informations ont ét mises à jour.", Toast.LENGTH_SHORT).show()
//                }
            Log.d(TAG, email_textedit.text.toString())
            val user = Firebase.auth.currentUser
            user?.email?.let { changeMail(it,email_textedit.text.toString()) }
            user!!.updateEmail(email_textedit.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        email = email_textedit.text.toString()
                        Log.d(TAG, "User email address updated.")
                        upPassword()
                    } else {
                        Log.d(TAG, "User email address not updated.")
                    }
                }
        } else {
            upPassword()
        }
    }

    fun upPassword(){
        val user = Firebase.auth.currentUser
        // Up password
        if(password_textedit.text.toString() != ""){
//                if(!toastTriggered){
//                    toastTriggered = true
//                    Toast.makeText(context, "Vos informations ont ét mises à jour.", Toast.LENGTH_SHORT).show()
//                }
            user!!.updatePassword(password_textedit.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                    }
                }
        }
    }

    private fun changeUserName( newUserName:String)
    {
        db = Firebase.database
        var userID: String? = null
        var oldUserName:String
        var idChat : String

        db.reference.get().addOnSuccessListener {
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
                        if(root.child("Conversations").child(it).child("user1Mail").getValue<String>()==Firebase.auth.currentUser?.email)
                        {
                            oldUserName= root.child("Conversations").child(it).child("user1").getValue<String>().toString()
                            db.reference.child("Conversations").child(it).child("user1").setValue(newUserName)
                        }else{
                            oldUserName= root.child("Conversations").child(it).child("user2").getValue<String>().toString()
                            db.reference.child("Conversations").child(it).child("user2").setValue(newUserName)
                        }

                        idChat = root.child("Conversations").child(it).child("chat").getValue<String>().toString()
                        //Log.i("VAlue ","chat : $idChat, olUser : $oldUserName")
                        root.child("Chat").child(idChat).child("Messages").children.forEach {
                            //Log.i("TEST gsugdd","${it.child("messageUser").value} && $oldUserName")
                            if(it.child("messageUser").getValue<String>()==oldUserName){
                                Log.i("TEST UI","dduushidigd")
                                it.key?.let { it2 ->
                                    db.reference.child("Chat").child(idChat).child("Messages").child(it2).child("messageUser").setValue(newUserName)
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private fun changeMail(oldMail :String,newMail :String)
    {
        db = Firebase.database
        var userID: String? = null

        db.reference.get().addOnSuccessListener {
            val root = it
            // We get the ID of the user
            root.child("Users_ID").getValue<Map<String, String>>()!!.forEach {
                if (it.value == oldMail) {
                    userID = it.key
                }
            }
            //We change the value of the mail
            userID?.let {
                    it1 -> db.reference.child("Users_ID").child(it1).setValue(newMail)
                    root.child("Users").child(it1).child("Conversations").children.forEach {
                        it.key?.let {
                            if (root.child("Conversations").child(it).child("user1Mail")
                                    .getValue<String>() == oldMail
                            ) {
                                db.reference.child("Conversations").child(it).child("user1Mail")
                                    .setValue(newMail)
                            } else {
                                db.reference.child("Conversations").child(it).child("user2Mail")
                                    .setValue(newMail)
                            }
                        }
                }
            }
        }

    }

}