package com.example.project_uqac.ui.mysql

import android.R.attr.*
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Point
import android.graphics.PointF
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


class MySQL (context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($ID INTEGER  PRIMARY KEY, $DATE TEXT, $GEOHASH TEXT, $LATITUDE DOUBLE, $LONGITUDE DOUBLE)"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Called when the database needs to be upgraded
    }

    //Inserting (Creating) data
    fun addCoordinates(coordinates: CoordinateDB): Boolean {
        //Create and/or open a database that will be used for reading and writing.
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DATE, coordinates.date)
        values.put(GEOHASH, coordinates.geoHash)
        values.put(LATITUDE, coordinates.lat)
        values.put( LONGITUDE, coordinates.lon)
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedID", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    //get last coordinates
    @SuppressLint("Range")
    fun getLastCoordinates(): String {
        var lastCoordinate: String = "";
        val db = readableDatabase
        val selectLASTQuery = "SELECT * FROM $TABLE_NAME ORDER BY id DESC LIMIT 1"
        val cursor = db.rawQuery(selectLASTQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
               // do {
                    var id = cursor.getString(cursor.getColumnIndex(ID))
                    var date = cursor.getString(cursor.getColumnIndex(DATE))
                    var geohash = cursor.getString(cursor.getColumnIndex(GEOHASH))
                    var lat = cursor.getString(cursor.getColumnIndex(LATITUDE))
                    var lon = cursor.getString(cursor.getColumnIndex(LONGITUDE))

                    lastCoordinate = "$lastCoordinate\n$id $date $geohash $lat $lon"
               // } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return lastCoordinate
    }


    //get all users
    @SuppressLint("Range")
    fun getAllCoordinates(): String {
        var allCoordinate: String = "";
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var id = cursor.getString(cursor.getColumnIndex(ID))
                    var date = cursor.getString(cursor.getColumnIndex(DATE))
                    var geohash = cursor.getString(cursor.getColumnIndex(GEOHASH))
                    var lat = cursor.getString(cursor.getColumnIndex(LATITUDE))
                    var lon = cursor.getString(cursor.getColumnIndex(LONGITUDE))

                    allCoordinate = "$allCoordinate\n$id $date $geohash $lat $lon"
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return allCoordinate
    }

    @SuppressLint("Range")
     fun deleteCoordinates7DaysLater() {
        var deleted: Boolean = false

        //Find day of choose periode
        val  cal : Calendar = GregorianCalendar . getInstance ()
        cal.time = Calendar.getInstance().time
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val daysBeforeDate : java.util.Date = cal.time
        val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val formattedSevenDateBefore: String = df.format(daysBeforeDate)

        val db = writableDatabase
        db.delete(TABLE_NAME,"$DATE < ?", arrayOf(formattedSevenDateBefore))

        db.close()
    }

    //get all users
    @SuppressLint("Range")
    fun anyCoordinatesInsideRadius(lat: Double, lon: Double): Boolean {

        val center = PointF(lat.toFloat(), lon.toFloat())
        val mult = 1.0 // mult = 1.1; is more reliable
        val radius = 500.0
        val p1 = calculateDerivedPosition(center, mult * radius, 0)
        val p2 = calculateDerivedPosition(center, mult * radius, 90)
        val p3 = calculateDerivedPosition(center, mult * radius, 180)
        val p4 = calculateDerivedPosition(center, mult * radius, 270)

        val selectALLQuery =  "SELECT * FROM $TABLE_NAME WHERE $LATITUDE > " + p3.x.toString() + " AND $LATITUDE < " + p1.x.toString() + " AND $LONGITUDE < " + p2.y.toString() + " AND $LONGITUDE > " + p4.y.toString()
        val db = readableDatabase

        val cursor = db.rawQuery(selectALLQuery, null)
        var destPoint : PointF
        var foundOneClose : Boolean = false
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var lat = cursor.getString(cursor.getColumnIndex(LATITUDE))
                    var lon = cursor.getString(cursor.getColumnIndex(LONGITUDE))
                    destPoint = PointF(lat.toFloat(), lon.toFloat())
                    if (pointIsInCircle(destPoint, center, radius)) {
                        foundOneClose = true
                    }
                } while (!foundOneClose && cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return foundOneClose
    }

    fun calculateDerivedPosition(
        point: PointF,
        range: Double, bearing: Int
    ): PointF {
        val EarthRadius = 6371000.0 // m
        val latA = Math.toRadians(point.x.toDouble())
        val lonA = Math.toRadians(point.y.toDouble())
        val angularDistance = range / EarthRadius
        val trueCourse = Math.toRadians(bearing.toDouble())
        var lat = Math.asin(
            Math.sin(latA) * Math.cos(angularDistance) +
                    (Math.cos(latA) * Math.sin(angularDistance)
                            * Math.cos(trueCourse))
        )
        val dlon = Math.atan2(
            Math.sin(trueCourse) * Math.sin(angularDistance)
                    * Math.cos(latA),
            Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat)
        )
        var lon = (lonA + dlon + Math.PI) % (Math.PI * 2) - Math.PI

        lat = Math.toDegrees(lat)
        lon = Math.toDegrees(lon)

        return PointF(lat.toFloat(), lon.toFloat())
    }

    fun pointIsInCircle(
        pointForCheck: PointF, center: PointF,
        radius: Double
    ): Boolean {
        return if (getDistanceBetweenTwoPoints(pointForCheck, center) <= radius) true else false
    }

    fun getDistanceBetweenTwoPoints(p1: PointF, p2: PointF): Double {
        val R = 6371000.0 // m
        val dLat = Math.toRadians((p2.x - p1.x).toDouble())
        val dLon = Math.toRadians((p2.y - p1.y).toDouble())
        val lat1 = Math.toRadians(p1.x.toDouble())
        val lat2 = Math.toRadians(p2.x.toDouble())
        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.sin(dLon / 2)
                    * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(
                lat2
            ))
        val c =
            2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }



    companion object {
        private val DB_NAME = "CoordinatesDB"
        private val DB_VERSIOM = 1;
        private val TABLE_NAME = "coordinates"
        private val ID = "id"
        private val DATE: String = "Date"
        private val GEOHASH: String = "GeoHash"
        private val LATITUDE: String = "Latitude"
        private val LONGITUDE: String = "Longitude"
    }


}