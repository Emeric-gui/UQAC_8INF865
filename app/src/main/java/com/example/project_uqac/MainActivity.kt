package com.example.project_uqac

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.project_uqac.databinding.ActivityMainBinding
import com.example.project_uqac.ui.service.LocationGPS
import com.example.project_uqac.ui.service.LocationProviderChangedReceiver
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

   // private var intentReceiver = SimpleBroadcastReceiver()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_post, R.id.navigation_discussions, R.id.navigation_my_account
            )
        )
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        progressBar2.visibility = VISIBLE
        //Get the localization of the phone NETWORK/GPS
        getLocation ()
        /*Toast.makeText(
            this,
            "MainActivity Ecriture data",
            Toast.LENGTH_SHORT
        ).show()

         */


        val br: BroadcastReceiver = LocationProviderChangedReceiver()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(br, filter)

        /*
        val intentFilter = IntentFilter(
            Intent.ACTION_CAMERA_BUTTON)
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        registerReceiver(intentReceiver, intentFilter)
        */




    }

    fun  getLocation () {
        val position =  LocationGPS(this)
        position.getLocation()
    }

    fun  stopLoading () {
        progressBar2.visibility = GONE

    }

    fun  startLoading () {
        progressBar2.visibility = VISIBLE

    }



}