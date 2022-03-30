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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class SearchFragment  : Fragment()  {

    private lateinit var dashboardViewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null
    private var lat : Double = 0.0
    private var lon : Double = 0.0
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


        dashboardViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textSearch
        //dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        // })

        val buttonFilter = root.findViewById<View>(R.id.button_filter)
        buttonFilter.setOnClickListener(){
            var dialogFragFilter = DialogueFragmentFilter()
            dialogFragFilter.show(childFragmentManager, "customDialog")

        }

        val db = Firebase.firestore
        val articles = ArrayList<Article>()
        // Lookup the recyclerview in activity layout
        val rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView

        val textNoArticle = root.findViewById<TextView>(R.id.textNoArticles2)

        val inputSearch = root.findViewById<TextInputEditText>(R.id.Search)
        val buttonSearch = root.findViewById<ImageButton>(R.id.button_search)
        loadData(db, textNoArticle, articles, rvArticles, inputSearch.text.toString())
        buttonSearch.setOnClickListener {
            loadData(db, textNoArticle, articles, rvArticles, inputSearch.text.toString())
        }

        val position =  LocationGPS(context as MainActivity)
        //position.getLocationSearch(this)
        getPositionBackground(position, this)

        return root
    }

    fun getPositionBackground(
        position: LocationGPS,
        searchFragment: SearchFragment
    ) {
        executorService.execute {
            try {

                mainThreadHandler.post {  position.getLocationSearch(searchFragment) }
            } catch (e: Exception) {

            }
        }
    }

    fun getCoordinate(lat : Double,lon : Double) {
        this.lat = lat
        this.lon = lon


    }


    private fun setAdapter(adapter: ArticlesAdapter) {

        adapter.setOnItemClickListener(object :ArticlesAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                //recuperer items
                Log.v(position.toString(), "positio")
                val objet = adapter.getObjet(position)
                val lieu = adapter.getLieu(position)
                val date = adapter.getDate(position)
                val nom = adapter.getNom(position)

                var args : Bundle = Bundle()
                args.putString("objet", objet)
                args.putString("lieu", lieu)
                args.putString("date", date)
                args.putString("nom", nom)

                //creation du fragment de dialogue
                val dialogPage = DialogFragmentDiscussion()

                //ajout des infos dans le dialog
                dialogPage.arguments(args)

                dialogPage.show(childFragmentManager, "Custom Dialog")

            }
        })
    }


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
                        articles.add(article)
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
                        articles.add(article)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}