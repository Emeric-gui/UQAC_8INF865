package com.example.project_uqac.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.google.android.material.textfield.TextInputEditText

class PostFragmentDescriptionAnimal : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_description_animal, container, false)

        val textDescription : EditText = view.findViewById(R.id.textDescriptionAnimal)
        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 51

        val args = Bundle()

        val textSpecie = requireArguments().getString("specie")
        val textRace = requireArguments().getString("race")



        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevdescriptionAnimal)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentEspece()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnextdescriptionAnimal)
        buttonNext.setOnClickListener(){
            val fragment = PostFragmentPhotoAnimal()
            args.putString("specie", textSpecie.toString())
            args.putString("race", textRace.toString())
            args.putString("description", textDescription.text.toString())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view

    }

}