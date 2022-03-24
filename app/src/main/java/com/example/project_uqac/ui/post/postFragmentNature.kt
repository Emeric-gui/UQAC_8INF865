package com.example.project_uqac.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.project_uqac.R

class PostFragmentNature : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_nature, container, false)
        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 20


        val buttonAnimal : Button = view.findViewById(R.id.buttonAnimal)
        buttonAnimal.setOnClickListener(){
            val fragment = PostFragmentEspece()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonObject : Button = view.findViewById(R.id.buttonObjet)
        buttonObject.setOnClickListener(){
            val fragment = PostFragmentType()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view
    }

}