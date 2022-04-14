package com.example.project_uqac.ui.post

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.project_uqac.BuildConfig
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.chat.ChatFragment
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*


class PostFragmentLieuAnimal : Fragment(), OnMapReadyCallback,
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener
{

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private var reloaded : Boolean = false

    private var lat : Double = 37.406474
    private var lon : Double = -122.078184
    private var latObject : Double = 0.0
    private var lonObject : Double = 0.0
    private var  radius  = 14.0
    private var zoomMap = 0
    private lateinit var viewMap : MapView
    private lateinit var map: GoogleMap
    private lateinit var seekBarRadius : SeekBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_lieu_animal, container, false)

        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 100

        val textSpecie = requireArguments().getString("specie")
        val textRace = requireArguments().getString("race")
        val textDescription = requireArguments().getString("description")
        val cropImg : Bitmap? = requireArguments().getParcelable("image")
        val textDate = requireArguments().getInt("date")

        val args = Bundle()

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevLieuAnimal)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentDateAnimal()
            args.putString("specie", textSpecie.toString())
            args.putString("race", textRace.toString())
            args.putString("description", textDescription.toString())
            args.putParcelable("image", cropImg)
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        seekBarRadius = view.findViewById(R.id.seekBar)
        val textSeekBarRadius : TextView = view.findViewById(R.id.var_progress)

        seekBarRadius!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                zoomMap = i
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latObject, lonObject),(((1-(zoomMap.toFloat()/100))*5)+7)))
                textSeekBarRadius!!.text = " $zoomMap"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY, Locale.CANADA);
        }

        val fm = fragmentManager?.beginTransaction()
        mapFragment = SupportMapFragment.newInstance()
        autocompleteFragment = AutocompleteSupportFragment.newInstance()
        fm?.add(R.id.mapView, mapFragment)
        fm?.add(R.id.autocomplete_fragment, autocompleteFragment)
        fm?.commit()

        //Lire les coordonnées sur le fichier de stockage interne de l'application
        readCoordinate()
        mapFragment.getMapAsync(this)
        viewMap = view.findViewById(R.id.mapView)

        val db = Firebase.firestore

        val buttonNext : Button = view.findViewById(R.id.buttonPublier)
        buttonNext.setOnClickListener(){


// Compute the GeoHash for a lat/lng point
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latObject, lonObject))

            //ici, on récupère l'id de la dernière image, requete, on compte le nombre d'éléments
            var champ_image = "image_"
            var id_recup : Int = 0

            val list_articles = db.collection("Articles")
            list_articles.whereNotEqualTo("image", "null")
                .get().addOnSuccessListener { documents ->
                    for (document in documents){
                        id_recup++
                    }
                }.addOnCompleteListener{

                    champ_image += id_recup.toString()

                    if (cropImg != null) {
                        updatePic(cropImg, champ_image)
                    }else{
                        champ_image = null.toString()
                    }


                    val article = Firebase.auth.currentUser?.email?.let { it1 ->
                        Firebase.auth.currentUser?.displayName?.let { it2 ->
                            Article("$textSpecie", "$textRace", textDate,
                                "$textDescription", champ_image,
                                it2,hash,latObject, lonObject,
                                it1, false
                            )
                        }
                    }


                    if (article != null) {
                        db.collection("Articles")
                            .add(article)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Objet posté !", Toast.LENGTH_SHORT).show()
                                val fragment = PostFragmentNature()
                                val transaction = fragmentManager?.beginTransaction()
                                transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()

                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Objet non Posté !", Toast.LENGTH_SHORT).show()
                                Log.e("HA", "Error saving : Err :" + it.message)
                            }
                    }
                }


        }

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                //Log.i(ChatFragment.TAG, "An error occurred: $status")
                Toast.makeText(context,"Utilisez une API KEY valide!", Toast.LENGTH_LONG).show()

            }

            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(ChatFragment.TAG, "Place: ${place.name}, ${place.id}")
                latObject = place.latLng.latitude
                lonObject = place.latLng.longitude
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng,(((1-(zoomMap.toFloat()/100))*5)+7)))

            }
        })

        return view
    }

    private fun readCoordinate() {

        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(activity,"Vous devriez activer ou autoriser la localsation pour un meilleurs service...",Toast.LENGTH_LONG).show()
        } else {

            val filename = "Coordinates"
            if (filename != null && filename.trim() != "") {
                var fileInputStream: FileInputStream? =
                    (activity as MainActivity).openFileInput(filename)
                var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder: StringBuilder = StringBuilder()
                var text: String? = null
                while (run {
                        text = bufferedReader.readLine()
                        text
                    } != null) {
                    stringBuilder.append(text)
                }
                //Displaying data on EditText
                val coordinates = stringBuilder.split("=")
                this.lat = coordinates[0].toDouble()
                this.lon = coordinates[1].toDouble()
            } else {
                Toast.makeText(activity, "file name cannot be blank", Toast.LENGTH_LONG).show()
            }

        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(googleMap: GoogleMap) {
        val lat = this.lat
        val lng = this.lon
        var positions = LatLng(lat, lng)
        val zoomLevel = radius.toFloat()
        map = googleMap
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraIdleListener(this);
        map.addMarker(
            MarkerOptions()
                .position(positions)
                .title("Marker")
        )
        /*googleMap.addCircle( CircleOptions()
           .center(googleMap.cameraPosition.target)
           .radius(googleMap.maxZoomLevel.toDouble()*25)
           .strokeWidth(10f)
           .fillColor(0x550000FF));
           */
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positions, zoomLevel))
    }

    override fun onCameraMoveStarted(reason: Int) {

        var reasonText = "UNKNOWN_REASON"
        when (reason) {
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                reasonText = "GESTURE"

            }
            GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION -> {
                reasonText = "API_ANIMATION"

            }
            GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> {
                reasonText = "DEVELOPER_ANIMATION"
            }
        }
        Log.d(TAG, "onCameraMoveStarted($reasonText)")
        //uptdateCoordinates()
    }


    fun uptdateCoordinates() {
        latObject = map.cameraPosition.target.latitude
        lonObject = map.cameraPosition.target.longitude
        Log.v(map.cameraPosition.target.toString(), "CAMERAAAAAAAA")

    }

    override fun onCameraIdle() {
        uptdateCoordinates()
    }

    fun updatePic(img : Bitmap, id: String){
        reloaded = true
        // Update profil pic in db - New style
        val storageReferenceu = FirebaseStorage.getInstance().getReference("articles_pics/$id")
        var myUri = context?.let { getImageUri(it, img, "article_pic.$id") }!!
        Log.d(ContentValues.TAG, myUri.toString())
        storageReferenceu.putFile(myUri).addOnSuccessListener {
            storageReferenceu.downloadUrl.addOnSuccessListener {}
        }
    }
    fun getImageUri(inContext: Context, inImage: Bitmap, name : String): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            name,
            null
        )
        return Uri.parse(path)
    }

}

