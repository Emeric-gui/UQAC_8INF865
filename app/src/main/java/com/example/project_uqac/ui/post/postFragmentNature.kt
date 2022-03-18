package com.example.project_uqac.ui.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton
import com.example.project_uqac.R
import com.google.android.material.button.MaterialButton

class postFragmentNature : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_nature, container, false)

        val buttonAnimal : Button = view.findViewById(R.id.buttonAnimal)
        buttonAnimal.setOnClickListener(){
            val fragment = postFragmentEspece()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonObject : Button = view.findViewById(R.id.buttonObjet)
        buttonObject.setOnClickListener(){
            val fragment = postFragmentType()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view
    }

}