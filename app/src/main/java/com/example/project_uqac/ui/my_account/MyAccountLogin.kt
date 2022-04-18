package com.example.project_uqac.ui.my_account

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_login.*

class MyAccountLogin : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Configure google sign in
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)

        val buttonGoogleSignIn : SignInButton = view.findViewById(R.id.googleSignInButton)
        buttonGoogleSignIn.setOnClickListener(){
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 100)
        }

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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100){
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            } catch (e: Exception){
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?){
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                // login success
                Log.d(TAG, "firebaseAuthWithGoogleAccount: LoggedIn")

                // get user logged in
                val firebaseUser = auth.currentUser
                // get user info
                val uid = firebaseUser!!.uid
                val email = firebaseUser.email
                val url = firebaseUser.photoUrl

                val personName: String? = account.displayName
                val personGivenName: String? = account.givenName
                val personFamilyName: String? = account.familyName
                val personEmail: String? = account.email
                val personId: String? = account.id
                val personPhoto: Uri? = account.photoUrl

                Log.d(TAG, "firebaseAuthWithGoogleAccount: Uid: $uid")
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $email")

                val fragment = MyAccountLogged()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
            }
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