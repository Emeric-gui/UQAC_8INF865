package com.example.project_uqac.ui.my_account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyAccountTabInformations : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)
        val view =  inflater.inflate(R.layout.fragment_my_account_informations, container, false)

        val user = Firebase.auth.currentUser
        val usernameInput : TextInputEditText = view.findViewById(R.id.username_textedit)
        val emailInput : TextInputEditText = view.findViewById(R.id.email_textedit)
        if (user != null) {
            usernameInput.setText(user.displayName.toString())
            emailInput.setText(user.email.toString())
        } else {
            usernameInput.setText("Error")
            emailInput.setText("Error")
        }

        val buttonDeleteAccount : Button = view.findViewById(R.id.delete_account)
        buttonDeleteAccount.setOnClickListener(){
            if (user != null) {

                Log.d("TAG", user.email.toString())
            } else {
                Log.d("TAG", "C po ko")
            }
        }

        return view
    }
}