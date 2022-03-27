package com.example.project_uqac.ui.search.filter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.ui.post.PostFragmentLieuAnimal
import com.example.project_uqac.ui.service.LocationGPS
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class FilterTabPosition : Fragment(), OnMapReadyCallback {


    private lateinit var mapFragment: SupportMapFragment
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)


        //Pour ajouter le fragment de la map
        mapFragment = SupportMapFragment.newInstance()
        val fm = parentFragmentManager.beginTransaction()
        fm.add(R.id.mapView2, mapFragment)
        fm.commit()
        val position =  LocationGPS(context as MainActivity)
        getPositionBackground(position, this)

        return inflater.inflate(R.layout.fragment_filter_map, container, false)
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