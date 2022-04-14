package com.example.project_uqac

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val window: Window = this@MainActivity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.barStatus)

        getParametersForLunching ()

        //val br: BroadcastReceiver = LocationProviderChangedReceiver()
        //val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        //registerReceiver(br, filter)


    }

    fun  getParametersForLunching () {
        //Get the localization of the phone NETWORK/GPS
        val position =  LocationGPS(this)
        position.getLocationActivity()
    }

    fun start() {
        setNavView()
    }
/*
    fun  stopLoading () {
        progressBar2.visibility = GONE

    }

    fun  startLoading () {
        progressBar2.visibility = VISIBLE

    }
*/
    fun setNavView() {

        navView = binding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_post, R.id.navigation_discussions, R.id.navigation_my_account
            )
        )

        navView.visibility = VISIBLE

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.navigate(R.id.navigation_home)
    }



}