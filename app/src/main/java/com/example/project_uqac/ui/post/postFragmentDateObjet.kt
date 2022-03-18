package com.example.project_uqac.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.project_uqac.R

class PostFragmentDateObjet : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_date_objet, container, false)

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevDateObjet)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentDescriptionObjet()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnextDateObjet)
        buttonNext.setOnClickListener(){
            val fragment = PostFragmentLieuObjet()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view

    }

}