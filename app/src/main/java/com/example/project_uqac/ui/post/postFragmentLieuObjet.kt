package com.example.project_uqac.ui.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.article.Article
import com.google.firebase.firestore.FirebaseFirestore

import com.example.project_uqac.ui.my_account.MyAccountFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostFragmentLieuObjet : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_lieu_objet, container, false)

        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 100

        val textModel = requireArguments().getString("model")
        val textMarque = requireArguments().getString("marque")
        val textDescription = requireArguments().getString("description")
        val textDate = requireArguments().getInt("date")

        val args = Bundle()

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevLieuObjet)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentDateObjet()
            args.putString("model", textModel.toString())
            args.putString("marque", textMarque.toString())
            args.putString("description", textDescription.toString())
            fragment.arguments = args

            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

       val db = Firebase.firestore

        val buttonNext : Button = view.findViewById(R.id.buttonPublier)
        buttonNext.setOnClickListener(){


            val article = Article("$textModel", "$textMarque", textDate,
                "$textDescription", "https://picsum.photos/600/300?random&$", null, null
            )

            db.collection("Articles")
                .add(article)
                .addOnSuccessListener {
                    Toast.makeText(context, "Objet posté !", Toast.LENGTH_SHORT).show()
                    val fragment = PostFragmentNature()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()

                }
                .addOnFailureListener {
                    Toast.makeText(context, "Objet non Posté !", Toast.LENGTH_SHORT).show()
                    Log.e("HA", "Error saving : Err :" + it.message)
                }
           }




        return view
    }

}