package com.example.project_uqac.ui.home.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.project_uqac.R

class DialogueFragmentHome : DialogFragment(){

    //private var searchFragment : SearchFragment = searchFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        return inflater.inflate(R.layout.fragment_home_information, container, false)
    }


}
