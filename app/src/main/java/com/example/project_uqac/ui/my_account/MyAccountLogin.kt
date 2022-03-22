package com.example.project_uqac.ui.my_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.post.PostFragmentEspece
import com.example.project_uqac.ui.post.PostFragmentType

class MyAccountLogin : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)

        val buttonObject : Button = view.findViewById(R.id.log_in)
        buttonObject.setOnClickListener(){
            val fragment = MyAccountLogged()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
        }

        return view
    }

}