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
import com.example.project_uqac.ui.post.PostFragment
import com.example.project_uqac.ui.post.PostFragmentLieuAnimal
import com.example.project_uqac.ui.post.PostFragmentLieuObjet
import com.example.project_uqac.ui.search.SearchFragment
import com.example.project_uqac.ui.search.filter.FilterTabPosition

class LocationGPS(mainActivity: MainActivity) : LocationListener {

    private var lati : Double? = null
    private var long : Double? = null

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var app = mainActivity
    private var contextHomeFragment: HomeFragment? = null
    private var contextSearchFragment: SearchFragment? = null

    private var contextPostFragment : PostFragment? = null

    private var contextPostFragmentObjet : PostFragmentLieuObjet? = null
    private var contextPostFragmentAnimal : PostFragmentLieuAnimal? = null
    private var contextSearchFilterFragment : FilterTabPosition? = null



    fun getLocationHome(homeFragment : HomeFragment)  {
        getLocation()
        contextHomeFragment = homeFragment
    }
    fun getLocationSearch(searchFragment : SearchFragment)  {
        getLocation()
        contextSearchFragment = searchFragment
    }

    fun getLocationPost(postFragment: PostFragment){
        getLocation()
        contextPostFragment = postFragment
    }
    fun getLocationPostObjet(postFragmentObjet : PostFragmentLieuObjet)  {
        getLocation()
        contextPostFragmentObjet = postFragmentObjet
    }

    fun getLocationPostAnimal(postFragmentAnimal : PostFragmentLieuAnimal){
        getLocation()
        contextPostFragmentAnimal = postFragmentAnimal
    }

    fun getLocationPostAnimal(searchFilter : FilterTabPosition){
        getLocation()
        contextSearchFilterFragment = searchFilter
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
        this.lati = lat
        this.long = lon

        contextHomeFragment?.getCoordinate(lat, lon)
        contextSearchFragment?.getCoordinate(lat, lon)
        contextPostFragment?.getCoordinate(lat, lon)

        if(contextPostFragmentAnimal != null){
            contextPostFragmentAnimal?.getCoordinate(lat, lon)
        }
        if(contextPostFragmentObjet != null){
            contextPostFragmentObjet?.getCoordinate(lat, lon)
        }
        if(contextSearchFilterFragment != null){
            contextSearchFilterFragment?.getCoordinate(lat, lon)
        }
    }


}