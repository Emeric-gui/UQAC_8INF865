package com.example.project_uqac.ui.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.example.project_uqac.MainActivity
import com.example.project_uqac.ui.home.HomeFragment
import com.example.project_uqac.ui.search.SearchFragment

class LocationGPS(mainActivity: MainActivity) : LocationListener {
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var app = mainActivity
    private var contextHomeFragment: HomeFragment? = null
    private var contextSearchFragment: SearchFragment? = null



    fun getLocationHome(HomeFragment : HomeFragment)  {
        getLocation()
        contextHomeFragment = HomeFragment
    }
    fun getLocationSearch(SearchFragment : SearchFragment)  {
        getLocation()
        contextSearchFragment = SearchFragment
    }
    fun getLocation() {

        locationManager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                app,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                app,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                app,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 5f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 5f, this)
        }
    }

    override fun onLocationChanged(location: Location) {

        val lat = location.latitude
        val lon = location.longitude
        contextHomeFragment?.getCoordinate(lat, lon)
        contextSearchFragment?.getCoordinate(lat, lon)

    }


}