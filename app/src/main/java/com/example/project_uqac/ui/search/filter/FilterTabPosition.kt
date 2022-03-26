package com.example.project_uqac.ui.search.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class FilterTabPosition : Fragment(), OnMapReadyCallback {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)


        //Pour ajouter le fragment de la map
        val mapFragment = SupportMapFragment.newInstance()
        val fm = parentFragmentManager.beginTransaction()
        fm.add(R.id.mapView2, mapFragment)
        fm.commit()
        mapFragment.getMapAsync(this)


        return inflater.inflate(R.layout.fragment_filter_map, container, false)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        //val positions = LatLng(0.0, 0.0)
        val positions = LatLng(-34.0, 151.0)

        googleMap.addMarker(
            MarkerOptions()
                .position(positions)
                .title("Marker")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(positions))
    }
}