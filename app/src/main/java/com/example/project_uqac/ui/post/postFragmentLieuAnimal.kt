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
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostFragmentLieuAnimal : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_lieu_animal, container, false)

        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 100

        val textSpecie = requireArguments().getString("specie")
        val textRace = requireArguments().getString("race")
        val textDescription = requireArguments().getString("description")
        val textDate = requireArguments().getInt("date")

        val args = Bundle()

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevLieuAnimal)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentDateAnimal()
            args.putString("specie", textSpecie.toString())
            args.putString("race", textRace.toString())
            args.putString("description", textDescription.toString())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val db = Firebase.firestore

        val buttonNext : Button = view.findViewById(R.id.buttonPublier)
        buttonNext.setOnClickListener(){


// Compute the GeoHash for a lat/lng point
            val lat = 48.4223952
            val lng = -71.0578483
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng))


            val article = Article("$textSpecie", "$textRace", textDate,
                "$textDescription", "https://picsum.photos/600/300?random&$", "Nom",hash,lat, lng
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