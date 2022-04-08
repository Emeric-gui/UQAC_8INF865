package com.example.project_uqac.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.article.Article
import com.google.android.material.textfield.TextInputEditText

class PostFragmentType : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_type, container, false)

        val textModel : TextInputEditText = view.findViewById(R.id.textModele)
        val textMarque : TextInputEditText = view.findViewById(R.id.textMarque)
        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 34
        val args = Bundle()

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevtype)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentNature()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnexttype)
        buttonNext.setOnClickListener(){
            val fragment = PostFragmentDescriptionObjet()
            args.putString("model", textModel.text.toString())
            args.putString("marque", textMarque.text.toString())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }
        return view
    }

}