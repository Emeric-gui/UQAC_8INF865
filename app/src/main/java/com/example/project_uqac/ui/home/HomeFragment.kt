package com.example.project_uqac.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentHomeBinding
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.article.ArticlesAdapter
import com.example.project_uqac.ui.home.popupDiscussion.DialogFragmentDiscussion
import com.example.project_uqac.ui.service.LocationGPS
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.project_uqac.ui.home.information.DialogueFragmentHome
import com.example.project_uqac.ui.search.filter.DialogueFragmentFilter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.messaging.FirebaseMessaging


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private val locationPermissionCode = 2
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    private var db = Firebase.firestore
    private var articles = ArrayList<Article>()

    private lateinit var rvArticles : RecyclerView

    private lateinit var textNoArticle : TextView

    private lateinit var button1 : TextView
    private lateinit var button3 : TextView
    private lateinit var button7 : TextView
    private lateinit var loading : ProgressBar

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = Firebase.firestore
        articles = ArrayList<Article>()
        // Lookup the recyclerview in activity layout
        rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        textNoArticle = root.findViewById<TextView>(R.id.textNoArticles)

        button1 = root.findViewById<TextView>(R.id.button1J)
        button3 = root.findViewById<Button>(R.id.button3J)
        button7 = root.findViewById<Button>(R.id.button7J)
        loading = root.findViewById(R.id.progressBar3)
        button1.isSelected = true
        button1.setBackgroundResource(R.drawable.barckground_button_home)
        button3.isSelected = false
        button7.isSelected = false

        button1.setOnClickListener {
            if (checkPermissions()) {
                button1.isSelected = true
                button1.setBackgroundResource(R.drawable.barckground_button_home)
                button3.isSelected = false
                button3.setBackgroundColor(Color.parseColor("#3F51B5"))
                button7.isSelected = false
                button7.setBackgroundColor(Color.parseColor("#3F51B5"))
                getDataArticlesServer()
            }
        }
        button3.setOnClickListener { // Perform action on click
            if (checkPermissions()) {
                button3.isSelected = true
                button3.setBackgroundResource(R.drawable.barckground_button_home)
                button1.isSelected = false
                button1.setBackgroundColor(Color.parseColor("#3F51B5"))
                button7.isSelected = false
                button7.setBackgroundColor(Color.parseColor("#3F51B5"))
                getDataArticlesServer()
            }
        }
        button7.setOnClickListener {
            if (checkPermissions()) {
                button7.isSelected = true
                button7.setBackgroundResource(R.drawable.barckground_button_home)
                button3.isSelected = false
                button3.setBackgroundColor(Color.parseColor("#3F51B5"))
                button1.isSelected = false
                button1.setBackgroundColor(Color.parseColor("#3F51B5"))
                getDataArticlesServer()
            }
        }

        val buttonInformation = root.findViewById<View>(R.id.imageButtonInformation)
        buttonInformation.setOnClickListener(){
            var dialogFragInformation = DialogueFragmentHome()
            dialogFragInformation.show(childFragmentManager, "customDialog")
        }

        loading.visibility = VISIBLE
        if (!getPositionBackground()){
            loading.visibility = GONE
            textNoArticle.text = "Veuillez Activer la localisation..."
        } else {
            textNoArticle.text = ""
        }
        if(Firebase.auth.currentUser != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
                println(
                    "Token : " + task.result
                )
                val db = Firebase.database
                val auth = Firebase.auth
                var userID: String? = null
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
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        getPositionBackground()
        getDataArticlesServer()
    }

    fun checkPermissions () : Boolean {
        return !(context?.let {
            ActivityCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } != PackageManager.PERMISSION_GRANTED && context?.let {
            ActivityCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } != PackageManager.PERMISSION_GRANTED)
    }

    fun getDataArticlesServer  () : Boolean {
        return if (checkPermissions ()) {
            loadData()
            true
        } else {
            Toast.makeText(activity,"Vous devez activez ou autoriser la localisation",Toast.LENGTH_LONG).show()
            false
        }
    }

    fun getPositionBackground() : Boolean {
        if (checkPermissions ()) {
            val position =  LocationGPS(context as MainActivity)
            executorService.execute {
                try {

                    mainThreadHandler.post {
                        position.getLocation()
                    }

                } catch (e: Exception) {
                }
            }
            return true
        } else {
            return false
        }
    }

    private fun readCoordinate() {

        val filename = "Coordinates"
        if(filename!=null && filename.trim()!=""){
            var fileInputStream: FileInputStream? =
                activity?.applicationContext?.openFileInput(filename)
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
        }else{
            Toast.makeText(activity,"file name cannot be blank",Toast.LENGTH_LONG).show()
        }
    }

    //for loading all articles from server
    fun loadData()
    {
        textNoArticle.text = ""
        loading.visibility = VISIBLE
        readCoordinate()
        //Reset liste
        articles.clear()
        //Find day of choose periode
        val  cal : Calendar = GregorianCalendar . getInstance ()
        cal.time = Calendar.getInstance().time
        if (button1.isSelected) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else if (button3.isSelected){
            cal.add(Calendar.DAY_OF_YEAR, -3)
        } else if (button7.isSelected) {
            cal.add(Calendar.DAY_OF_YEAR, -7)
        }
        val daysBeforeDate : Date = cal.time
        val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val formattedDateBefore: String = df.format(daysBeforeDate)
        Log.v(formattedDateBefore.toInt().toString(),"7avant?")

        // Find cities within 50km of the user position
        val center = GeoLocation(lat, lon)
        val radiusInM = (50 * 10000).toDouble()
        // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
        // a separate query for each pair. There can be up to 9 pairs of bounds
        // depending on overlap, but in most cases there are 4.
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q: Query = db.collection("Articles")
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
            tasks.add(
                q.get()
            )
        }
        if (tasks.isEmpty()) {
            loading.visibility = GONE
            textNoArticle.text = "Aucun objet trouvé"
        }
        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val adapter = ArticlesAdapter(articles)
                rvArticles.adapter = adapter
                setAdapter(adapter)
                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lon")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        val article = doc.toObject(Article::class.java)

                        if (article != null) {
                            if (distanceInM <= radiusInM && formattedDateBefore.toInt() <= article.date) {
                                if (article != null && article.author != Firebase.auth.currentUser?.email) {
                                    articles.add(article)
                                }
                            }
                        }
                    }
                    loading.visibility = GONE
                    if (articles.isEmpty()) {
                        textNoArticle.text = "Aucun objet trouvé"
                    }
                }
                // Set layout manager to position the items
                rvArticles.layoutManager = LinearLayoutManager(view?.context)

            }
    }

    private fun setAdapter(adapter: ArticlesAdapter) {

        adapter.setOnItemClickListener(object :ArticlesAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                //recuperer items
                val objet = adapter.getObjet(position)
                val lieu = adapter.getLieu(position)
                val date = adapter.getDate(position)
                val nom = adapter.getNom(position)
                val mail = adapter.getMail(position)

                var args : Bundle = Bundle()
                args.putString("objet", objet)
                args.putString("lieu", lieu)
                args.putString("date", date)
                args.putString("nom", nom)
                args.putString("mail", mail)

                //creation du fragment de dialogue
                val dialogPage = DialogFragmentDiscussion()
                //ajout des infos dans le dialog
                dialogPage.arguments(args)

                dialogPage.show(childFragmentManager,  dialogPage.tag)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}