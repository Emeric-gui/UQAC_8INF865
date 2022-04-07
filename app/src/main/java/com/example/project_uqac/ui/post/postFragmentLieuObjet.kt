package com.example.project_uqac.ui.post

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import com.example.project_uqac.BuildConfig
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.chat.ChatFragment
import com.example.project_uqac.ui.home.HomeFragment
import com.example.project_uqac.ui.my_account.myPosts.Post
import com.example.project_uqac.ui.search.SearchFragment
import com.example.project_uqac.ui.service.LocationGPS
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PostFragmentLieuObjet : Fragment(), OnMapReadyCallback,
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener {


    private lateinit var mapFragment: SupportMapFragment
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var latObject : Double = 0.0
    private var lonObject : Double = 0.0
    private var  radius  = 14.0
    private lateinit var map: GoogleMap
    private var zoomMap = 0
    private lateinit var seekBarRadius : SeekBar

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

        seekBarRadius = view.findViewById(R.id.seekBar)
        val textSeekBarRadius : TextView = view.findViewById(R.id.var_progress)

        seekBarRadius!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                zoomMap = i
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latObject, lonObject),(((1-(zoomMap.toFloat()/100))*5)+7)))
                textSeekBarRadius!!.text = " $zoomMap"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY, Locale.CANADA);
        }

        val fm = fragmentManager?.beginTransaction()
        mapFragment = SupportMapFragment.newInstance()
        autocompleteFragment = AutocompleteSupportFragment.newInstance()
        fm?.add(R.id.mapView, mapFragment)
        fm?.add(R.id.autocomplete_fragment, autocompleteFragment)
        fm?.commit()

        readCoordinate()

        mapFragment.getMapAsync(this)


        val db = Firebase.firestore

        val buttonNext : Button = view.findViewById(R.id.buttonPublier)
        buttonNext.setOnClickListener(){

// Compute the GeoHash for a lat/lng point
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latObject, lonObject))

            val article = Firebase.auth.currentUser?.email?.let { it1 ->
                Firebase.auth.currentUser?.displayName?.let { it2 ->
                    Article("$textModel", "$textMarque", textDate,
                        "$textDescription", "https://picsum.photos/600/300?random&$",
                        it2,hash, latObject, lonObject,
                        it1
                    )
                }
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

            /*val londonRef = db.collection("Articles")
                .add(updates)
                .addOnCompleteListener {
                    // ...
                }

             */
        }

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
               // Log.i(ChatFragment.TAG, "An error occurred: $status")
                Toast.makeText(context,"Utilisez une API KEY valide!", Toast.LENGTH_LONG).show()

            }

            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(ChatFragment.TAG, "Place: ${place.name}, ${place.id}")
                latObject = place.latLng.latitude
                lonObject = place.latLng.longitude
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng,(((1-(zoomMap.toFloat()/100))*5)+7)))

            }
        })

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


    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(googleMap: GoogleMap) {
        val lat = this.lat
        val lng = this.lon
        var positions = LatLng(lat, lng)
        val zoomLevel = radius.toFloat()
        map = googleMap
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraIdleListener(this);
        map.addMarker(
            MarkerOptions()
                .position(positions)
                .title("Marker")
        )
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
        Log.d(ContentValues.TAG, "onCameraMoveStarted($reasonText)")
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