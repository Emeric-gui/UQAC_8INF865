package com.example.project_uqac.ui.search.filter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.project_uqac.BuildConfig
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.ui.chat.ChatFragment.Companion.TAG
import com.example.project_uqac.ui.search.SearchViewModel
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.android.datatransport.runtime.backends.BackendResponse
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import javax.net.ssl.SSLEngineResult


class FilterTabPosition(dialogueContext: DialogueFragmentFilter) : Fragment(),  GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener, OnMapReadyCallback {


    private lateinit var mapFragment: SupportMapFragment
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var latObject : Double = 0.0
    private var lonObject : Double = 0.0
    private var  radius  = 14.0
    private var zoomMap = 0
    private lateinit var viewMap : MapView
    private lateinit var map: GoogleMap
    private lateinit var seekBarRadius : SeekBar
    private lateinit var viewModel: SearchViewModel
    private var dialogueContext : DialogueFragmentFilter = dialogueContext


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true);
//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)

        val rootView = inflater.inflate(R.layout.fragment_filter_map, container, false)

        seekBarRadius = rootView.findViewById(R.id.seekBar)
        val textSeekBarRadius : TextView = rootView.findViewById(R.id.var_progress)

        // Init values
        textSeekBarRadius.text = " 1"

        seekBarRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

                zoomMap = i
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latObject, lonObject),(((1-(zoomMap.toFloat()/100))*5)+7)))

                if(zoomMap == 0){
                    textSeekBarRadius.text = " 1"
                } else {
                textSeekBarRadius!!.text = " $zoomMap"
                }

            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY, Locale.CANADA);
        }

        //Pour ajouter le fragment de la map
        //mapFragment = SupportMapFragment.newInstance(options)
        val fm = fragmentManager?.beginTransaction()
        mapFragment = SupportMapFragment.newInstance()
        autocompleteFragment = AutocompleteSupportFragment.newInstance()
        fm?.add(R.id.mapView2, mapFragment)
        fm?.add(R.id.autocomplete_fragment, autocompleteFragment)
        fm?.commit()

        //Lire les coordonnées sur le fichier de stockage interne de l'application
        readCoordinate()
        mapFragment.getMapAsync(this)
        viewMap = rootView.findViewById(R.id.mapView2)


        // Initialize the AutocompleteSupportFragment.
       // autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
        //            as AutocompleteSupportFragment
        // Specify the types of place data to return.

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
                // TODO: Handle the error.
                //TODO("Not yet implemented")
                Toast.makeText(activity,"Utilisez une API KEY valide!", Toast.LENGTH_LONG).show()

            }

            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
                latObject = place.latLng.latitude
                lonObject = place.latLng.longitude
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng,(((1-(zoomMap.toFloat()/100))*5)+7)))

            }
        })


        return rootView
    }
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place =Autocomplete.getPlaceFromIntent(data);

                lat = place.latLng?.latitude!!
                lon = place.latLng?.longitude!!
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                var status = Autocomplete.getStatusFromIntent(data)
                status.statusMessage?.let { Log.i("address", it) };
            }
        }
    }

 */

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
            Toast.makeText(activity,"file name cannot be blank", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(googleMap: GoogleMap) {
        var positions = LatLng(this.lat, this.lon)
        val zoomLevel = radius.toFloat()
        map = googleMap
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraIdleListener(this);

        val drawable = this.resources.getDrawable(R.drawable.ic_herepin)

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        val bitmapDrawable = BitmapDrawable(this.resources, bitmap)
        map.addMarker(
            MarkerOptions()
                .icon( BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapDrawable.bitmap, 150, 150, false)))
                .position(positions)
                .title("Marker")
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positions, zoomLevel))

        dialogueContext.setLat(this.lat)
        dialogueContext.setLon(this.lon)

    }


    fun uptdateCoordinates() {
        latObject = map.cameraPosition.target.latitude
        lonObject = map.cameraPosition.target.longitude
        dialogueContext.setLat(latObject)
        dialogueContext.setLon(lonObject)
        Log.v(map.cameraPosition.target.toString(), "CAMERAAAAAAAA")
    }

    override fun onCameraIdle() {
        uptdateCoordinates()
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

}