package com.example.project_uqac

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.project_uqac.databinding.ActivityMainBinding
import com.example.project_uqac.ui.service.LocationGPS
import com.example.project_uqac.ui.services.AppUtil
import com.example.project_uqac.ui.services.FirebaseNotificationService
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

   // private var intentReceiver = SimpleBroadcastReceiver()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var appUtil: AppUtil

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        appUtil = AppUtil()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val window: Window = this@MainActivity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.barStatus)

        getParametersForLunching ()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
            println(
                "Token : " + task.result
            )
            val db = Firebase.database
            val auth = Firebase.auth
            var userID : String? = null
            db.reference.child("Users_ID").get().addOnSuccessListener {
                it.getValue<Map<String, String>>()!!.forEach {
                    if (it.value == auth.currentUser?.email) {
                        userID = it.key
                    }
                }
                //Ajout du token dans firebase
                FirebaseDatabase.getInstance().getReference("Users").child(userID!!).child("token").setValue(task.result)
            }
        }
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