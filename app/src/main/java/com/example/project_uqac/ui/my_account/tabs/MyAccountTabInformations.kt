package com.example.project_uqac.ui.my_account.tabs

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.my_account.MyAccountLogged
import com.example.project_uqac.ui.my_account.dialogue.DialogueDeleteAccount
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_account_informations.*

class MyAccountTabInformations : Fragment() {

    lateinit var username : String
    lateinit var email : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)
        val view =  inflater.inflate(R.layout.fragment_my_account_informations, container, false)

        var user = Firebase.auth.currentUser
        val usernameInput : TextInputEditText = view.findViewById(R.id.username_textedit)
        val emailInput : TextInputEditText = view.findViewById(R.id.email_textedit)
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
}