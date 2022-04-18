package com.example.project_uqac.ui.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.project_uqac.MainActivity
import com.example.project_uqac.ui.mysql.CoordinateDB
import com.example.project_uqac.ui.mysql.MySQL
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class LocationGPS(mainActivity: MainActivity) : LocationListener {

    private var lati : Double? = null
    private var long : Double? = null
    private var dbHandler: MySQL? = null

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var app = mainActivity
    private var activityLunching = false

    fun getLocationActivity() {

        getLocation()
        activityLunching = true

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

            app.start()

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

        //update database
        updateDB(lat, lon)


        if (activityLunching){
            app.start()
            activityLunching = false
        }

    }

    fun updateDB(lat: Double, lon: Double) {
        // checking input text should not be null
//        Toast.makeText(app, "UPDATEDB", Toast.LENGTH_LONG).show()


        //init db
        dbHandler = MySQL(app)
        Log.v(dbHandler!!.getAllCoordinates(), "Elements beforeeeeeeeee")
        Log.v(dbHandler!!.anyCoordinatesInsideRadius(lat, lon).toString(), "last elementVVVVVV")

        dbHandler!!.deleteCoordinates7DaysLater()

        Log.v(dbHandler!!.getAllCoordinates(), "Elements AFTERrrrrrrrrrrrr")


        if (!dbHandler!!.anyCoordinatesInsideRadius(lat, lon)) {

            val coordinates: CoordinateDB = CoordinateDB()
            var success: Boolean = false

           //val  cal : Calendar = GregorianCalendar . getInstance ()
            //cal.time = Calendar.getInstance().time
            val todayDate: java.util.Date = Calendar.getInstance().time //cal.time
            val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val formattedTodayDate: String = df.format(todayDate)

            coordinates.date = formattedTodayDate
            coordinates.geoHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lon))
            coordinates.lat = lat
            coordinates.lon = lon
            success = dbHandler!!.addCoordinates(coordinates)

            if (success) {
//                Toast.makeText(app, "Saved Successfully", Toast.LENGTH_LONG).show()
            }
        }
    }
    }