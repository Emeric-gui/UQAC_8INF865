package com.example.project_uqac.ui.article

import com.firebase.geofire.core.GeoHash

class Article(
    val title: String = "", val marque: String = "", val date: Int = 0,

    val description: String = "", val image: String = "", val nom: String = "", val geoHash: String = "", val lat: Double = 0.0, val lon: Double = 0.0,

    val author: String = "", val objet : Boolean = true){


    companion object {
        private var objectId = 0
    }
}