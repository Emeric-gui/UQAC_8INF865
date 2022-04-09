package com.example.project_uqac.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var date :Int = 0
    private var radius : Int = 50
    private var db = Firebase.firestore
    private var articles = java.util.ArrayList<Article>()
    // Lookup the recyclerview in activity layout
    private lateinit var rvArticles : RecyclerView
    private lateinit var textNoArticle : TextView
    private lateinit var inputSearch : TextInputEditText



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

        //val textView: TextView = binding.textSearch
        //dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        // })

        val buttonFilter = root.findViewById<View>(R.id.button_filter)
        buttonFilter.setOnClickListener(){
            var dialogFragFilter = DialogueFragmentFilter(this)
            dialogFragFilter.show(childFragmentManager, "customDialog")
        }
        db = Firebase.firestore
        // Lookup the recyclerview in activity layout
        rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        textNoArticle = root.findViewById<TextView>(R.id.textNoArticles2)
        inputSearch = root.findViewById<TextInputEditText>(R.id.Search)
        inputSearch.setText("")
        val buttonSearch = root.findViewById<ImageButton>(R.id.button_search)
        loadData()
        buttonSearch.setOnClickListener {
            loadData()
        }

        val position =  LocationGPS(context as MainActivity)
        //position.getLocationSearch(this)
        getPositionBackground(position, this)
        /*Toast.makeText(
            context,
            "SearchFragment Ecriture data",
            Toast.LENGTH_SHORT
        ).show()
         */
        return root
    }

    fun getPositionBackground(
        position: LocationGPS,
        searchFragment: SearchFragment
    ) {
        executorService.execute {
            try {
                mainThreadHandler.post {  position.getLocation()}
            } catch (e: Exception) {

            }
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

    /*
    //for loading all articles from server
    fun loadData(
        db: FirebaseFirestore,
        textNoArticle: TextView,
        articles: java.util.ArrayList<Article>,
        rvArticles: RecyclerView,
        text: String?
    ) {
        //Reset liste
        articles.clear()
        val adapter = ArticlesAdapter(articles)
        if (text != "") {
            var ref = db.collection("Articles")
            ref.whereEqualTo("title", text /*formattedDateBefore.toInt()*/)
            ref.whereEqualTo("marque", text/*formattedDateBefore.toInt()*/)
                //ref.whereIn()
                //db.collection("Articles")
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        textNoArticle.text = "Aucun objet trouvé"
                        // Set layout manager to position the items
                        rvArticles.layoutManager = LinearLayoutManager(view?.context)
                        Toast.makeText(context, "No article Found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    for (doc in it) {
                        val article = doc.toObject(Article::class.java)
                        Log.v(article.date.toString(), "article")
                        if( article.author != Firebase.auth.currentUser?.email){
                            articles.add(article)
                        }
                    }
                    textNoArticle.text = ""
                    // Attach the adapter to the recyclerview to populate items
                    rvArticles.adapter = adapter
                    setAdapter(adapter)
                    // Set layout manager to position the items
                    rvArticles.layoutManager = LinearLayoutManager(view?.context)
                }
        } else {
            db.collection("Articles")
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        textNoArticle.text = "Aucun objet trouvé"

                        Toast.makeText(context, "No article Found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    for (doc in it) {
                        val article = doc.toObject(Article::class.java)
                        Log.v(article.date.toString(), "article")
                        if( article.author != Firebase.auth.currentUser?.email){
                            articles.add(article)
                        }
                    }
                    textNoArticle.text = ""
                    // Attach the adapter to the recyclerview to populate items
                    rvArticles.adapter = adapter
                    setAdapter(adapter)
                    // Set layout manager to position the items
                    rvArticles.layoutManager = LinearLayoutManager(view?.context)
                }
        }
    }
     */

    //for loading all articles from server
    fun loadData()
    {
        textNoArticle.text = ""
        (activity as MainActivity).startLoading()
        //Reset liste
        articles.clear()
        //Find day of choose periode
        if (date == 0) {
            val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            date = df.format(Calendar.getInstance().time).toInt()
            Log.v(date.toString(), "7avant?")
        }
        if (lat == 0.0 && lon == 0.0) {
            readCoordinate()
        }
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
                        .addOnSuccessListener {
                            if (tasks.isEmpty()) {
                                (activity as MainActivity).stopLoading()
                                textNoArticle.text = "Aucun objet trouvé"

                                Toast.makeText(context, "No article Found", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }
                        }
                )
            }

            // Collect all the query results together into a single list
            Tasks.whenAllComplete(tasks)
                .addOnCompleteListener addOnSuccessListener@{
                    val adapter = ArticlesAdapter(articles)
                    rvArticles.adapter = adapter
                    setAdapter(adapter)
                    //val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                    (activity as MainActivity).stopLoading()
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
                                        Log.v(
                                            article.title, "articleeeeee22222222" +
                                                    "eeeeeeeee"
                                        )
                                        articles.add(article)
                                    }
                                }
                            }
                        }
                    }
                    if (articles.isEmpty()) {
                        textNoArticle.text = "Aucun objet trouvé"
                        // Set layout manager to position the items
                        rvArticles.layoutManager = LinearLayoutManager(view?.context)
                        Toast.makeText(context, "No article Found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    (activity as MainActivity).stopLoading()
                    // Set layout manager to position the items
                    rvArticles.layoutManager = LinearLayoutManager(view?.context)
                }

        } else {
            var ref = db.collection("Articles")
            val adapter = ArticlesAdapter(articles)
            Log.v(inputSearch.text.toString(), "IIIINNNNNPPPUUUTT?")
            ref.whereEqualTo("title", inputSearch.text.toString() /*formattedDateBefore.toInt()*/)
                //ref.whereIn()
                //db.collection("Articles")
                .get()
                .addOnSuccessListener {
                    (activity as MainActivity).stopLoading()
                    if (it.isEmpty) {
                        textNoArticle.text = "Aucun objet trouvé"
                        // Set layout manager to position the items
                        rvArticles.layoutManager = LinearLayoutManager(view?.context)
                        Toast.makeText(context, "No article Found", Toast.LENGTH_SHORT).show()
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
                    if (articles.isEmpty()) {
                        textNoArticle.text = "Aucun objet trouvé"
                        // Set layout manager to position the items
                        rvArticles.layoutManager = LinearLayoutManager(view?.context)
                        Toast.makeText(context, "No article Found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    textNoArticle.text = ""
                    // Attach the adapter to the recyclerview to populate items
                    rvArticles.adapter = adapter
                    setAdapter(adapter)
                    // Set layout manager to position the items
                    rvArticles.layoutManager = LinearLayoutManager(view?.context)
                }

        }
    }

    private fun readCoordinate() {

        val filename = "Coordinates"
        if(filename!=null && filename.trim()!=""){
            var fileInputStream: FileInputStream? =
                activity?.applicationContext?.openFileInput(filename)//activity?.applicationContext?.openFileInput(filename)
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
            //Toast.makeText(activity,"STRING"+stringBuilder,Toast.LENGTH_LONG).show()
            //Toast.makeText(activity,"LAAAAAAAA"+ coordinates[0] + " / " + coordinates[1] + "FINI",Toast.LENGTH_LONG).show()
            //fileData.setText(stringBuilder.toString()).toString()
        }else{
            Toast.makeText(activity,"file name cannot be blank",Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}