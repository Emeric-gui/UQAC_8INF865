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

class PostFragmentDescriptionObjet : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_description_objet, container, false)

        val textDescription : TextInputEditText = view.findViewById(R.id.textDescriptionObjet)
        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 60

        val args = Bundle()

        val textModel = requireArguments().getString("model")
        val textMarque = requireArguments().getString("marque")


        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevdescriptionObjet)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentType()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnextdescriptionObjet)
        buttonNext.setOnClickListener(){
            val fragment = PostFragmentDateObjet()
            args.putString("model", textModel.toString())
            args.putString("marque", textMarque.toString())
            args.putString("description", textDescription.text.toString())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view
    }
}