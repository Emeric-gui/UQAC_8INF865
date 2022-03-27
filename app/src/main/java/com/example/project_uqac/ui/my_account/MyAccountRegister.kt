package com.example.project_uqac.ui.my_account

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_account_sign_up.*

class MyAccountRegister : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_account_sign_up, container, false)
        // Initialize Firebase Auth
        auth = Firebase.auth

        val buttonObject2 : Button = view.findViewById(R.id.sign_up)
        buttonObject2.setOnClickListener {
            signUpUser()
        }

        return view
    }

    private fun signUpUser() {

        if (sign_up_username.text.toString().isEmpty()) {
            sign_up_username.error = "Veuillez entrer un nom d' utilisateur"
            sign_up_username.requestFocus()
            return
        }

        if (sign_up_email.text.toString().isEmpty()) {
            sign_up_email.error = "Veuillez entrer une adresse courriel"
            sign_up_email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(sign_up_email.text.toString()).matches()) {
            sign_up_email.error = "Veuillez entrer une adresse courriel valide"
            sign_up_email.requestFocus()
            return
        }

        if (sign_up_password.text.toString().isEmpty()) {
            sign_up_password.error = "Veuillez entrer un mot de passe"
            sign_up_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(sign_up_email.text.toString(), sign_up_password.text.toString()).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                Toast.makeText(context, "Account Created !",
                    Toast.LENGTH_SHORT).show()
                Log.d(TAG, auth.currentUser.toString())

                // Set username
                val profileUpdates = userProfileChangeRequest {
                    displayName = sign_up_username.text.toString()
                }
                val user = Firebase.auth.currentUser
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { innerTask ->
                        if (innerTask.isSuccessful) {
                            val fragment = MyAccountLogged()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
                        } else {
                            Log.d(TAG, "Error with username.")
                        }
                    }
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(context, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()

            }
        }
    }


}