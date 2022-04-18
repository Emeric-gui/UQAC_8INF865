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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.my_account.MyAccountLogged
import com.example.project_uqac.ui.my_account.dialogue.DialogueDeleteAccount
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
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
    private var otherTypeOfAcc : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)
        val view =  inflater.inflate(R.layout.fragment_my_account_informations, container, false)

        val user = Firebase.auth.currentUser
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

        user?.let {
            var triggered: Boolean = false
            for (profile in it.providerData) {
                // Id of the provider (ex: google.com)
                if(triggered){
                    val providerId = profile.providerId.toString()
                    if (providerId != "password"){
                        otherTypeOfAcc = true
                    }
                    break
//                    Log.w(TAG, "provider $providerId")
                }
                triggered = true
            }
        }

//        var toastTriggered : Boolean = false
        val buttonUpdateAccount : Button = view.findViewById(R.id.update_account)
        buttonUpdateAccount.setOnClickListener(){
            if (otherTypeOfAcc){
                upUsernameGoogleAcc()
            } else {
                if (password_textedit.text.toString().isNotEmpty()){
                    upUsername()
                } else {
                    Toast.makeText(
                        context,
                        "Vous devez indiquer votre mot de passe.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                password_textedit.setText("")
//            Toast.makeText(context, "Vos informations ont été mises à jour.", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonDeleteAccount : Button = view.findViewById(R.id.delete_account)
        buttonDeleteAccount.setOnClickListener(){
            if (password_textedit.text.toString().isNotEmpty()){
                deleteAcc()
            } else {
                Toast.makeText(
                    context,
                    "Vous devez indiquer votre mot de passe.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        if (otherTypeOfAcc){
            val myAccMyInfUsername : TextView = view.findViewById(R.id.my_acc_my_inf_username)
            myAccMyInfUsername.setText(getString(R.string.nom_d_utilisateur))
            emailInput.visibility = View.GONE
            val passwordInputTextView : TextView = view.findViewById(R.id.password_textedit_textview)
            passwordInputTextView.visibility = View.GONE
            val emailInputTextView : TextView = view.findViewById(R.id.email_textedit_textview)
            emailInputTextView.visibility = View.GONE
            val passwordInput : EditText = view.findViewById(R.id.password_textedit)
            passwordInput.visibility = View.GONE
            val passwordNewInput : EditText = view.findViewById(R.id.password_new_textedit)
            passwordNewInput.visibility = View.GONE
            val passwordNewInputTextView : TextView = view.findViewById(R.id.password_new_textedit_textview)
            passwordNewInputTextView.visibility = View.GONE
//            email_textedit.visibility = View.GONE
//            password_textedit.isVisible = false
//            email_textedit.isVisible = false
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

    fun deleteAcc(){
        val user = Firebase.auth.currentUser
        val credential = EmailAuthProvider
            .getCredential(user?.email.toString(), password_textedit.text.toString())

// Prompt the user to re-provide their sign-in credentials
        user?.reauthenticate(credential)?.addOnSuccessListener {
            Log.d(TAG, "User re-authenticated.")
            //creation du fragment de dialogue
            val dialogPage = DialogueDeleteAccount()

            //ajout des infos dans le dialog
            dialogPage.arguments(this.parentFragment as MyAccountLogged)

            dialogPage.show(childFragmentManager, "Custom Dialog")
        }?.addOnFailureListener(){
            password_textedit.setText("")
            Toast.makeText(
                context,
                "Votre mot de passe est erroné.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun upUsernameGoogleAcc(){
        val user = Firebase.auth.currentUser
        if(username_textedit.text.toString() != username){
            val profileUpdates = userProfileChangeRequest {
                displayName = username_textedit.text.toString()
            }
            changeUserName(username_textedit.text.toString())
            user?.updateProfile(profileUpdates)?.addOnCompleteListener { innerTask ->
                if (innerTask.isSuccessful) {
                    username = username_textedit.text.toString()
                    val frag: MyAccountLogged? = this.parentFragment as MyAccountLogged?
                    frag?.updateUsername(username)
                    Toast.makeText(
                        context,
                        "Votre nom d'utilisateur a été mis à jour.",
                        Toast.LENGTH_SHORT
                    ).show()
                    upEmail()
                } else {
                    Log.d(ContentValues.TAG, "Error with username.")
                }
            }
        }
    }

    private fun upUsername(){
        val user = Firebase.auth.currentUser
        // Up username
        if(username_textedit.text.toString() != username && password_textedit.text.toString().isNotEmpty()){
//                if(!toastTriggered){
//                    toastTriggered = true
//                }
            val credential = EmailAuthProvider
                .getCredential(user?.email.toString(), password_textedit.text.toString())

// Prompt the user to re-provide their sign-in credentials
            user?.reauthenticate(credential)?.addOnSuccessListener {
                val profileUpdates = userProfileChangeRequest {
                    displayName = username_textedit.text.toString()
                }
                changeUserName(username_textedit.text.toString())
                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { innerTask ->
                        if (innerTask.isSuccessful) {
                            username = username_textedit.text.toString()
                            val frag: MyAccountLogged? = this.parentFragment as MyAccountLogged?
                            frag?.updateUsername(username)
                            Toast.makeText(
                                context,
                                "Votre nom d'utilisateur a été mis à jour.",
                                Toast.LENGTH_SHORT
                            ).show()
                            upEmail()
                        } else {
                            Log.d(ContentValues.TAG, "Error with username.")
                        }
                    }
            }?.addOnFailureListener(){
                password_textedit.setText("")
                Toast.makeText(
                    context,
                    "Votre mot de passe est erroné.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            upEmail()
        }
    }

    private fun upEmail(){
        val user = Firebase.auth.currentUser
        // Up email
        if(email_textedit.text.toString() != email && password_textedit.text.toString().isNotEmpty()){
            var credential = EmailAuthProvider
                .getCredential(user?.email.toString(), password_textedit.text.toString())

// Prompt the user to re-provide their sign-in credentials
            user?.reauthenticate(credential)?.addOnSuccessListener {
//                Log.d(TAG, "User re-authenticated.")
//                if(!toastTriggered){
//                    toastTriggered = true
//                    Toast.makeText(context, "Vos informations ont ét mises à jour.", Toast.LENGTH_SHORT).show()
//                }
//                Log.d(TAG, email_textedit.text.toString())


                user.updateEmail(email_textedit.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            email = email_textedit.text.toString()

                            // Relog with new mail
                            credential = EmailAuthProvider
                                .getCredential(user.email.toString(), password_textedit.text.toString())
                            user.reauthenticate(credential)

// Prompt the user to re-provide their sign-in credentials
                            Toast.makeText(
                                context,
                                "Votre email a été mis à jour.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(TAG, "User email address updated.")
                            upPassword()
                        } else {
                            Log.d(TAG, "User email address not updated.")
                            upPassword()
                        }
                    }
                user.email?.let { changeMail(it, email_textedit.text.toString()) }
            }?.addOnFailureListener(){
                password_textedit.setText("")
                Toast.makeText(
                    context,
                    "Votre mot de passe est erroné.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            upPassword()
        }
    }

    private fun upPassword() {
        val user = Firebase.auth.currentUser
        if(password_textedit.text.toString() != ""){
        val credential = EmailAuthProvider
            .getCredential(user?.email.toString(), password_textedit.text.toString())

// Prompt the user to re-provide their sign-in credentials
        user?.reauthenticate(credential)?.addOnSuccessListener {
            Log.d(TAG, "User re-authenticated.")
            // Up password
            if (password_new_textedit.text.toString() != "") {
//                if(!toastTriggered){
//                    toastTriggered = true
//                    Toast.makeText(context, "Vos informations ont ét mises à jour.", Toast.LENGTH_SHORT).show()
//                }
                user.updatePassword(password_new_textedit.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Votre mot de passe a été mis à jour.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(TAG, "User password updated.")
                        }
                    }.addOnFailureListener() {
                        Toast.makeText(
                            context,
                            "Le nouveau mot de passe trop court.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
//                Toast.makeText(
//                    context,
//                    "Le nouveau mot de passe ne peut pas être vide.",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }
            ?.addOnFailureListener {
                Log.d(TAG, "wrong pw")
                Toast.makeText(
                    context,
                    "Mot de passe non mis à jour, l'ancien mot de passe est erroné",
                    Toast.LENGTH_LONG
                ).show()
            }
    } else {
//            Toast.makeText(
//                context,
//                "L'ancien mot de passe ne peut pas être vide.",
//                Toast.LENGTH_SHORT
//            ).show()
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