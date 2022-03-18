package com.example.project_uqac.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.project_uqac.R

class PostFragmentDescriptionAnimal : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_description_animal, container, false)

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevdescriptionAnimal)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentEspece()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnextdescriptionAnimal)
        buttonNext.setOnClickListener(){
            val fragment = PostFragmentDateAnimal()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view

    }

}