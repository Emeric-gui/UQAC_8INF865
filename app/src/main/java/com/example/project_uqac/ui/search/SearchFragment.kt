package com.example.project_uqac.ui.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentSearchBinding
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.article.ArticlesAdapter
import com.example.project_uqac.ui.home.HomeFragment
import com.example.project_uqac.ui.home.popupDiscussion.DialogFragmentDiscussion
import com.example.project_uqac.ui.search.filter.DialogueFragmentFilter
import com.example.project_uqac.ui.service.LocationGPS
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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
import kotlin.collections.ArrayList

class SearchFragment  : Fragment()  {

    private lateinit var dashboardViewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null
    private var lat : Double = 37.406474
    private var lon : Double = -122.078184
    private var date :Int = 0
    private var radius : Int = 50
    private var db = Firebase.firestore
    private var articles = java.util.ArrayList<Article>()
    // Lookup the recyclerview in activity layout
    private lateinit var rvArticles : RecyclerView
    private lateinit var textNoArticle : TextView
    private lateinit var inputSearch : EditText
    private lateinit var loading : ProgressBar
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dashboardViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = Firebase.firestore
        rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        textNoArticle = root.findViewById<TextView>(R.id.textNoArticles2)
        inputSearch = root.findViewById<EditText>(R.id.Search)
        loading = root.findViewById(R.id.progressBar4)
        inputSearch.setText("")

        val buttonFilter = root.findViewById<View>(R.id.button_filter)
        buttonFilter.setOnClickListener(){
            var dialogFragFilter = DialogueFragmentFilter(this)
            dialogFragFilter.show(childFragmentManager, "customDialog")
        }

        loading.visibility = VISIBLE
        loadData()

        val buttonSearch = root.findViewById<ImageButton>(R.id.button_search)
        buttonSearch.setOnClickListener {
           loadData()

        }
        return root
    }

    override fun onStart() {
        super.onStart()
        getPositionBackground()
        loadData()
    }

    fun getPositionBackground() : Boolean {
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
            return false
        } else {
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
                dialogPage.show(childFragmentManager, "Custom Dialog")
            }
        })
    }

    fun setData(date : Int , lat : Double, lon : Double, radius : Int) {
        this.date = date
        this.lat = lat
        this.lon = lon
        this.radius = radius
        loadData()
    }

    //for loading all articles from server
    fun loadData()
    {
        textNoArticle.text = ""
        loading.visibility = VISIBLE
        //Reset liste
        articles.clear()
        //Find day of choose periode
        if (date == 0) {
            val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            date = df.format(Calendar.getInstance().time).toInt()
            Log.v(date.toString(), "7avant?")
        }
        readCoordinate()
        // Find cities within 50km of the user position
        val center = GeoLocation(lat, lon)
        val radiusInM = (radius * 1000).toDouble()

        if ( inputSearch.text.toString() == "") {
            // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
            // a separate query for each pair. There can be up to 9 pairs of bounds
            // depending on overlap, but in most cases there are 4.
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
            val tasks: MutableList<Task<QuerySnapshot>> = java.util.ArrayList()
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
                .addOnCompleteListener addOnSuccessListener@{
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
                                if (distanceInM <= radiusInM && date <= article.date) {
                                    if (article != null && article.author != Firebase.auth.currentUser?.email) {
                                        articles.add(article)
                                    }
                                }
                            }
                        }
                    }
                    loading.visibility = GONE
                    if (articles.isEmpty()) {
                        textNoArticle.text = "Aucun objet trouvé"
                    }
                    // Set layout manager to position the items
                    rvArticles.layoutManager = LinearLayoutManager(view?.context)
                }

        } else {
            var ref = db.collection("Articles")
            val adapter = ArticlesAdapter(articles)
            ref.whereEqualTo("title", inputSearch.text.toString() /*formattedDateBefore.toInt()*/)
                //ref.whereIn()
                //db.collection("Articles")
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        loading.visibility = GONE
                        textNoArticle.text = "Aucun objet trouvé"
                        // Set layout manager to position the items
                        rvArticles.layoutManager = LinearLayoutManager(view?.context)
                        return@addOnSuccessListener
                    }
                    for (doc in it) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lon")!!
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        val article = doc.toObject(Article::class.java)
                        Log.v(article.date.toString(), "article")
                        if( article.author != Firebase.auth.currentUser?.email){
                            if (distanceInM <= radiusInM && date <= article.date) {
                                articles.add(article)
                            }
                        }

                    }
                    loading.visibility = GONE
                    if (articles.isEmpty()) {
                        textNoArticle.text = "Aucun objet trouvé"
                    }
                    // Attach the adapter to the recyclerview to populate items
                    rvArticles.adapter = adapter
                    setAdapter(adapter)
                    // Set layout manager to position the items
                    rvArticles.layoutManager = LinearLayoutManager(view?.context)
                }

        }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}