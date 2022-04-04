package com.example.project_uqac.ui.my_account.tabs

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.my_account.MyAccountLogged
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_account_informations.*

class MyAccountTabHome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_my_account_home, container, false)
        val user = Firebase.auth.currentUser

        val buttonDeleteAccount : Button = view.findViewById(R.id.my_account_disconnect)
        buttonDeleteAccount.setOnClickListener(){
            if (user != null) {
                FirebaseAuth.getInstance().signOut()
                val frag: MyAccountLogged? = this.parentFragment as MyAccountLogged?
                frag?.goBackLogin(false)

            } else {
                Log.d("TAG", "C po ko wtf")
            }
        }

        return view
    }
}