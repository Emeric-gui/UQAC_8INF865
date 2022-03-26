package com.example.project_uqac

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.project_uqac.databinding.ActivityMainBinding
import com.example.project_uqac.ui.service.LocationGPS
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    //private val locationPermissionCode = 2
    private var lat : Double = 0.0
    private var lon : Double = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_post, R.id.navigation_discussions, R.id.navigation_my_account
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Start background service of GPS Location
       /*
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            val position =  LocationGPS(this)
            position.getLocation()
        }

        */

    }

    fun  getLocation () {
        val position =  LocationGPS(this)
        position.getLocation()
    }



    fun getCoordinate(lat : Double,lon : Double) {
        this.lat = lat
        this.lon = lon
        Toast.makeText(
            this,
            "Latitude: $lat , Longitude: $lon",
            Toast.LENGTH_SHORT
        ).show()

    }







}