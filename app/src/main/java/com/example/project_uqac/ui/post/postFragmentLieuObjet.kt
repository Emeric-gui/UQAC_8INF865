package com.example.project_uqac.ui.post

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.home.HomeFragment
import com.example.project_uqac.ui.my_account.myPosts.Post
import com.example.project_uqac.ui.search.SearchFragment
import com.example.project_uqac.ui.service.LocationGPS
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PostFragmentLieuObjet : Fragment(), OnMapReadyCallback {


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

        val fm = parentFragmentManager.beginTransaction()
        mapFragment = SupportMapFragment.newInstance()
        fm.add(R.id.mapView, mapFragment)
        fm.commit()

        val position =  LocationGPS(context as MainActivity)
        getPositionBackground(position, this)

        mapFragment.getMapAsync(this)


        val db = Firebase.firestore

        val buttonNext : Button = view.findViewById(R.id.buttonPublier)
        buttonNext.setOnClickListener(){

// Compute the GeoHash for a lat/lng point
            val lat = 51.5074
            val lng = 0.1278
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng))

            val article = Article("$textModel", "$textMarque", textDate,
                "$textDescription", "https://picsum.photos/600/300?random&$", "Nom",hash, lat, lng
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

            /*val londonRef = db.collection("Articles")
                .add(updates)
                .addOnCompleteListener {
                    // ...
                }

             */
        }


        return view
    }


    fun getPositionBackground(
        position: LocationGPS,
        postFragment: PostFragmentLieuObjet
    ) {
        executorService.execute {
            try {

                mainThreadHandler.post {  position.getLocationPostObjet(postFragment) }
            } catch (e: Exception) {

            }
        }
    }

    fun getCoordinate(lat : Double,lon : Double) {
        this.lat = lat
        this.lon = lon
        mapFragment.getMapAsync(this)
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