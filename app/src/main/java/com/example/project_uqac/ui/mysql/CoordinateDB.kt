package com.example.project_uqac.ui.mysql

import android.graphics.Point
import android.icu.util.MeasureUnit.POINT

class CoordinateDB (

    var id : Int = 0, var date: String = "", var geoHash: String = "", var point : Point = Point(0,0), var lat: Double = 0.0, var lon: Double = 0.0){

    //val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latObject, lonObject))

    companion object {
        private var objectId = 0
        /*
        fun createContactsList(numObject: Int) : ArrayList<Article> {
            val articles = ArrayList<Article>()
            for (i in 1..numObject) {
                ++objectId
                articles.add(

                    Article("Objet $objectId", "Lieu $objectId", 0,
                        "Description $objectId", "https://picsum.photos/600/300?random&$i",
                        "Nom&Prenom $objectId")

                )
            }
            return articles
        }
        */

    }
}