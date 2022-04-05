package com.example.project_uqac.ui.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.project_uqac.MainActivity
import com.example.project_uqac.ui.home.HomeFragment
import com.example.project_uqac.ui.post.PostFragment
import com.example.project_uqac.ui.post.PostFragmentLieuAnimal
import com.example.project_uqac.ui.post.PostFragmentLieuObjet
import com.example.project_uqac.ui.search.SearchFragment
import com.example.project_uqac.ui.search.filter.FilterTabPosition
import java.io.*
import java.util.*

class LocationGPS(mainActivity: MainActivity) : LocationListener {

    private var lati : Double? = null
    private var long : Double? = null

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var app = mainActivity

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

        var lat = location.latitude
        var lon = location.longitude
       // this.lati = lat
        //this.long = lon

        val fileOutputStream: FileOutputStream
        val file:String = "Coordinates"
        val data:String = "$lat=$lon"

        try {

            fileOutputStream = app.openFileOutput(file, Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
            /*Toast.makeText(
                app,
                "Ecriture data: $data",
                Toast.LENGTH_SHORT
            ).show()

             */
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }catch (e: Exception){
            e.printStackTrace()
        }

        if (app != null){
            app?.getCoordinate()
        }

    }


}