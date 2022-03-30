package com.example.project_uqac.ui.post

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.service.LocationGPS
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PostFragmentLieuAnimal : Fragment(), OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

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

        val fm = fragmentManager?.beginTransaction()
        mapFragment = SupportMapFragment.newInstance()
        fm?.add(R.id.mapView, mapFragment)
        fm?.commit()

        //Lire les coordonnées sur le fichier de stockage interne de l'application
        readCoordinate()
        mapFragment.getMapAsync(this)

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

    private fun readCoordinate() {

        this.lat = lat
        this.lon = lon
        val filename = "Coordinates"
        if(filename!=null && filename.trim()!=""){
            var fileInputStream: FileInputStream? = (activity as MainActivity).openFileInput(filename)
            var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while (run {
                    text = bufferedReader.readLine()
                    text
                } != null) {
                stringBuilder.append(text)
            }
            //Displaying data on EditText
            val coordinates = stringBuilder.split("=")
            lat = coordinates[0].toDouble()
            lon = coordinates[1].toDouble()
            //Toast.makeText(activity,"STRING"+stringBuilder,Toast.LENGTH_LONG).show()
            //Toast.makeText(activity,"LAAAAAAAA"+ coordinates[0] + " / " + coordinates[1] + "FINI",Toast.LENGTH_LONG).show()
            //fileData.setText(stringBuilder.toString()).toString()
        }else{
            Toast.makeText(activity,"file name cannot be blank",Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val lat = this.lat
        val lng = this.lon
        val positions = LatLng(lat, lng)

        googleMap.addMarker(
            MarkerOptions()
                .position(positions)
                .title("Marker")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(positions))
    }


}