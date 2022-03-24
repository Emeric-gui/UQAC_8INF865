package com.example.project_uqac.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.google.android.material.textfield.TextInputEditText

class PostFragmentEspece : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_espece, container, false)

        val textSpecie : TextInputEditText = view.findViewById(R.id.textSpecie)
        val textMRace : TextInputEditText = view.findViewById(R.id.textRace)
        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 40
        val args = Bundle()

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevrespece)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentNature()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnextespece)
        buttonNext.setOnClickListener(){
            val fragment = PostFragmentDescriptionAnimal()
            args.putString("specie", textSpecie.text.toString())
            args.putString("race", textMRace.text.toString())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view

    }


}