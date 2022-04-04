package com.example.project_uqac.ui.post

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.ui.article.Article
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader


class PostFragmentLieuAnimal : Fragment(), OnMapReadyCallback,
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener
{

    private lateinit var mapFragment: SupportMapFragment
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var latObject : Double = 0.0
    private var lonObject : Double = 0.0
   private lateinit var viewMap : MapView
    private lateinit var map: GoogleMap


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
        viewMap = view.findViewById(R.id.mapView)

        val db = Firebase.firestore

        val buttonNext : Button = view.findViewById(R.id.buttonPublier)
        buttonNext.setOnClickListener(){


// Compute the GeoHash for a lat/lng point
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latObject, lonObject))


            val article = Firebase.auth.currentUser?.email?.let { it1 ->
                Article("$textSpecie", "$textRace", textDate,
                    "$textDescription", "https://picsum.photos/600/300?random&$", "Nom",hash,latObject, lonObject,
                    it1
                )
            }

            if (article != null) {
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

             }


        return view
    }



    private fun readCoordinate() {

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
            this.lat = coordinates[0].toDouble()
            this.lon = coordinates[1].toDouble()
        }else{
            Toast.makeText(activity,"file name cannot be blank",Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(googleMap: GoogleMap) {
        val lat = this.lat
        val lng = this.lon
        var positions = LatLng(lat, lng)
        val radius  = 15.0
        val zoomLevel = radius.toFloat()
        map = googleMap
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraIdleListener(this);
        map.addMarker(
            MarkerOptions()
                .position(positions)
                .title("Marker")
        )
        /*googleMap.addCircle( CircleOptions()
           .center(googleMap.cameraPosition.target)
           .radius(googleMap.maxZoomLevel.toDouble()*25)
           .strokeWidth(10f)
           .fillColor(0x550000FF));
           */
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positions, zoomLevel))
    }

    override fun onCameraMoveStarted(reason: Int) {

        var reasonText = "UNKNOWN_REASON"
        when (reason) {
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                reasonText = "GESTURE"

            }
            GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION -> {
                reasonText = "API_ANIMATION"

            }
            GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> {
                reasonText = "DEVELOPER_ANIMATION"
            }
        }
        Log.d(TAG, "onCameraMoveStarted($reasonText)")
        //uptdateCoordinates()
    }


    fun uptdateCoordinates() {
        latObject = map.cameraPosition.target.latitude
        lonObject = map.cameraPosition.target.longitude
        Log.v(map.cameraPosition.target.toString(), "CAMERAAAAAAAA")

    }

    override fun onCameraIdle() {
        uptdateCoordinates()
    }


}

