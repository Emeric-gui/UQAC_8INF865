package com.example.project_uqac.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentHomeBinding
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.article.ArticlesAdapter
import com.example.project_uqac.ui.home.popupDiscussion.DialogFragmentDiscussion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

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

        val db = Firebase.firestore
        val articles = ArrayList<Article>()
        // Create adapter passing in the sample user data

        // Lookup the recyclerview in activity layout
        val rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView

        val textNoArticle = root.findViewById<TextView>(R.id.textNoArticles)

        val button1 = root.findViewById<Button>(R.id.button1J)
        val button3 = root.findViewById<Button>(R.id.button3J)
        val button7 = root.findViewById<Button>(R.id.button7j)
        button1.isSelected = true
        button3.isSelected = false
        button7.isSelected = false
        loadData(db, textNoArticle, articles, rvArticles, button1, button3, button7)


        button1.setOnClickListener {
            button1.isSelected = true
            button1.setBackgroundColor(Color.parseColor("#FF919090"))
            button3.isSelected = false
            button3.setBackgroundColor(Color.parseColor("#E0E0E0"))
            button7.isSelected = false
            button7.setBackgroundColor(Color.parseColor("#E0E0E0"))

            loadData(db, textNoArticle, articles, rvArticles, button1, button3, button7)
        }
        button3.setOnClickListener { // Perform action on click
            button3.isSelected = true
            button3.setBackgroundColor(Color.parseColor("#FF919090"))
            button1.isSelected = false
            button1.setBackgroundColor(Color.parseColor("#E0E0E0"))
            button7.isSelected = false
            button7.setBackgroundColor(Color.parseColor("#E0E0E0"))

            loadData(db, textNoArticle, articles, rvArticles, button1, button3, button7)
        }
        button7.setOnClickListener {
            button7.isSelected = true
            button7.setBackgroundColor(Color.parseColor("#FF919090"))
            button3.isSelected = false
            button3.setBackgroundColor(Color.parseColor("#E0E0E0"))
            button1.isSelected = false
            button1.setBackgroundColor(Color.parseColor("#E0E0E0"))

            loadData(db, textNoArticle, articles, rvArticles, button1, button3, button7)
        }

        // Initialize contacts
        //var articles = Article.createContactsList(19)

        return root
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

                dialogPage.show(childFragmentManager,  dialogPage.tag)
            }
        })
    }


    //for loading all articles from server
    fun loadData(
        db: FirebaseFirestore,
        textNoArticle: TextView,
        articles: ArrayList<Article>,
        rvArticles: RecyclerView,
        button1: Button,
        button3: Button,
        button7: Button
    ) {

        //Reset liste
        articles.clear()
        val adapter = ArticlesAdapter(articles)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}