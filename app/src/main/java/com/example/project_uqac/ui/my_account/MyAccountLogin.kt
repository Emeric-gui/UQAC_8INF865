package com.example.project_uqac.ui.my_account

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.post.PostFragmentEspece
import com.example.project_uqac.ui.post.PostFragmentType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_login.*

class MyAccountLogin : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)

        val buttonObject : Button = view.findViewById(R.id.log_in)
        buttonObject.setOnClickListener(){
            logInUser()
        }

        val buttonObject2 : Button = view.findViewById(R.id.btn_sign_up)
        buttonObject2.setOnClickListener(){
            val fragment = MyAccountRegister()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Firebase.auth.currentUser != null){
            val fragment = MyAccountLogged()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
        }
    }

    private fun logInUser() {
        if (email.text.toString().isEmpty()) {
            email.error = "Please enter email"
            email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = "Please enter valid email"
            email.requestFocus()
            return
        }

        if (password.text.toString().isEmpty()) {
            password.error = "Please enter password"
            password.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

//                    user?.let {
//                        // Name, email address, and profile photo Url
//                        val name = user.displayName
//                        val email = user.email
//                        val photoUrl = user.photoUrl
//
//                        // Check if user's email is verified
//                        val emailVerified = user.isEmailVerified
//
//                        // The user's ID, unique to the Firebase project. Do NOT use this value to
//                        // authenticate with your backend server, if you have one. Use
//                        // FirebaseUser.getToken() instead.
//                        val uid = user.uid
//                    }

                    val fragment = MyAccountLogged()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}