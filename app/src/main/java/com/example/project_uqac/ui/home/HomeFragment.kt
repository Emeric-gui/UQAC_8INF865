package com.example.project_uqac.ui.home

import android.annotation.SuppressLint
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


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    private var db = Firebase.firestore
    private var articles = ArrayList<Article>()
    // Create adapter passing in the sample user data

    // Lookup the recyclerview in activity layout
    private lateinit var rvArticles : RecyclerView

    private lateinit var textNoArticle : TextView

    private lateinit var button1 : Button
    private lateinit var button3 : Button
    private lateinit var button7 : Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = Firebase.firestore
        articles = ArrayList<Article>()
        // Create adapter passing in the sample user data

        // Lookup the recyclerview in activity layout
        rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView

        textNoArticle = root.findViewById<TextView>(R.id.textNoArticles)

        button1 = root.findViewById<Button>(R.id.button1J)
        button3 = root.findViewById<Button>(R.id.button3J)
        button7 = root.findViewById<Button>(R.id.button7j)
        button1.isSelected = true
        button1.setBackgroundColor(Color.parseColor("#FF919090"))
        button3.isSelected = false
        button7.isSelected = false

        button1.setOnClickListener {
            button1.isSelected = true
            button1.setBackgroundColor(Color.parseColor("#FF919090"))
            button3.isSelected = false
            button3.setBackgroundColor(Color.parseColor("#E0E0E0"))
            button7.isSelected = false
            button7.setBackgroundColor(Color.parseColor("#E0E0E0"))

            loadData()
        }
        button3.setOnClickListener { // Perform action on click
            button3.isSelected = true
            button3.setBackgroundColor(Color.parseColor("#FF919090"))
            button1.isSelected = false
            button1.setBackgroundColor(Color.parseColor("#E0E0E0"))
            button7.isSelected = false
            button7.setBackgroundColor(Color.parseColor("#E0E0E0"))

            loadData()
        }
        button7.setOnClickListener {
            button7.isSelected = true
            button7.setBackgroundColor(Color.parseColor("#FF919090"))
            button3.isSelected = false
            button3.setBackgroundColor(Color.parseColor("#E0E0E0"))
            button1.isSelected = false
            button1.setBackgroundColor(Color.parseColor("#E0E0E0"))

            loadData()
        }
        // Initialize contacts
        //var articles = Article.createContactsList(19)

        val position =  LocationGPS(context as MainActivity)
        //position.getLocationHome(this)
        getPositionBackground(position, this)
        Toast.makeText(
            context,
            "Home Fragment Ecriture data",
            Toast.LENGTH_SHORT
        ).show()

        loadData()

        return root
    }

    fun getPositionBackground(
        position: LocationGPS,
        homeFragment: HomeFragment
    ) {

        executorService.execute {
            try {

                mainThreadHandler.post {  position.getLocation() }
            } catch (e: Exception) {

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

    //for loading all articles from server
    fun loadData()
    {
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

        val daysBeforeDate : java.util.Date = cal.time
        val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val formattedDateBefore: String = df.format(daysBeforeDate)
        Log.v(formattedDateBefore.toInt().toString(),"7avant?")

/*
 var ref = db.collection("Articles")

        ref.whereGreaterThanOrEqualTo("date", formattedDateBefore.toInt() /*formattedDateBefore.toInt()*/)
        //ref.whereIn()
        //db.collection("Articles")
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    textNoArticle.text ="Aucun objet trouvé"

                    Toast.makeText(context, "No article Found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for (doc in it) {
                    val article = doc.toObject(Article::class.java)
                    Log.v(article.date.toString(), "article")
                    articles.add(article)
                }

                textNoArticle.text =""


                // Attach the adapter to the recyclerview to populate items
                rvArticles.adapter = adapter

                setAdapter(adapter)


                // Set layout manager to position the items
                rvArticles.layoutManager = LinearLayoutManager(view?.context)


            }
 */

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
            tasks.add(q.get())
        }
        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val adapter = ArticlesAdapter(articles)
                rvArticles.adapter = adapter
                setAdapter(adapter)
                //val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
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
                                if (article != null) {
                                    Log.v(article.title,"articleeeeee22222222" +
                                            "eeeeeeeee")
                                    articles.add(article)
                                }
                            }
                        }
                    }
                }

                // Set layout manager to position the items
                rvArticles.layoutManager = LinearLayoutManager(view?.context)

               // matchingDocs contains the results
                // ...
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