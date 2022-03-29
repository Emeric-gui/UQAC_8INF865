package com.example.project_uqac.ui.search.filter

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.ui.post.PostFragmentLieuAnimal
import com.example.project_uqac.ui.service.LocationGPS
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.w3c.dom.Text
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class FilterTabPosition : Fragment(), OnMapReadyCallback {


    private lateinit var mapFragment: SupportMapFragment
    private var makeModif = false
    private var delCircle = false
    private var value_modif : Double = 0.0
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)

        val rootView = inflater.inflate(R.layout.fragment_filter_map, container, false)

        val seek = rootView.findViewById<SeekBar>(R.id.seekBar)

        val data_zone = rootView.findViewById<TextView>(R.id.var_progress)

        seek?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                makeModif = true
                data_zone.text = progress.toString()
                value_modif = progress.toDouble()
                addChangesToMap()
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
            }
        })

        //options
        val options = GoogleMapOptions()
        options.mapType(GoogleMap.MAP_TYPE_HYBRID)
            .compassEnabled(false)
            .rotateGesturesEnabled(false)
            .tiltGesturesEnabled(false)

        //Pour ajouter le fragment de la map
        mapFragment = SupportMapFragment.newInstance(options)
        val fm = parentFragmentManager.beginTransaction()
        fm.add(R.id.mapView2, mapFragment)
        fm.commit()
        val position =  LocationGPS(context as MainActivity)
        getPositionBackground(position, this)


        mapFragment.getMapAsync(this)

        return rootView
    }

    fun addChangesToMap(){
        mapFragment.getMapAsync(this)
    }

    fun getPositionBackground(
        position: LocationGPS,
        searchFilterFragment: FilterTabPosition
    ) {
        executorService.execute {
            try {

                mainThreadHandler.post {  position.getLocationPostAnimal(searchFilterFragment) }
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
        if(makeModif){
            googleMap.setOnCameraIdleListener {
                val midLatLng: LatLng = LatLng(lat, lon)
                googleMap.clear()
                googleMap.addCircle(
                    CircleOptions()
                        .center(midLatLng)
                        .radius(value_modif*1000)
                        .strokeWidth(1f)
                        .fillColor(0x880000FF.toInt()))
            }
            makeModif = false

        }


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