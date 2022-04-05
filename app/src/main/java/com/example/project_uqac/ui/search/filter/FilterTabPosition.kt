package com.example.project_uqac.ui.search.filter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader


class FilterTabPosition : Fragment(),  GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener, OnMapReadyCallback {


    private lateinit var mapFragment: SupportMapFragment
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var latObject : Double = 0.0
    private var lonObject : Double = 0.0
    private var  radius  = 14.0
    private lateinit var viewMap : MapView
    private lateinit var map: GoogleMap
    private lateinit var seekBarRadius : SeekBar

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)

        val rootView = inflater.inflate(R.layout.fragment_filter_map, container, false)

        seekBarRadius = rootView.findViewById(R.id.seekBar)
        val textSeekBarRadius : TextView = rootView.findViewById(R.id.var_progress)

        seekBarRadius!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latObject, lonObject),(((1-(i.toFloat()/100))*5)+7)))
                textSeekBarRadius!!.text = " $i"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        //Pour ajouter le fragment de la map
        //mapFragment = SupportMapFragment.newInstance(options)
        val fm = fragmentManager?.beginTransaction()
        mapFragment = SupportMapFragment.newInstance()
        fm?.add(R.id.mapView2, mapFragment)
        fm?.commit()

        //Lire les coordonnées sur le fichier de stockage interne de l'application
        readCoordinate()
        mapFragment.getMapAsync(this)
        viewMap = rootView.findViewById(R.id.mapView2)



        return rootView
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


    }


    fun uptdateCoordinates() {
        latObject = map.cameraPosition.target.latitude
        lonObject = map.cameraPosition.target.longitude
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