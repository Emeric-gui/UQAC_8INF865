package com.example.project_uqac.ui.my_account.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentSearchBinding
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.article.ArticlesAdapter
import com.example.project_uqac.ui.my_account.myPosts.Post
import com.example.project_uqac.ui.my_account.myPosts.PostsAdapter
import com.example.project_uqac.ui.home.popupDiscussion.DialogFragmentDiscussion
import com.example.project_uqac.ui.my_account.MyAccountLogged
import com.example.project_uqac.ui.my_account.dialogue.DialogueDeleteAccount
import com.example.project_uqac.ui.my_account.dialogue.DialogueDeletePost
import com.example.project_uqac.ui.my_account.popupDelete.DialogFragmentDelete
import com.example.project_uqac.ui.search.SearchViewModel
import com.example.project_uqac.ui.search.filter.DialogueFragmentFilter
import com.example.project_uqac.ui.service.LocationGPS
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.model.DocumentKey
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_account_my_posts.*


class MyAccountTabMyPosts : Fragment() {
//
//    private var _binding: FragmentMyAccountMyPostsBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var rvArticles : RecyclerView
    var articles = ArrayList<Article>()
    var articlesSnapshots = ArrayList<String>()
    lateinit var rvArticles: RecyclerView
    private lateinit var my_account_no_article : TextView
    private lateinit var my_account_no_article_descr : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inflated = inflater.inflate(R.layout.fragment_my_account_my_posts, container, false)
        // Lookup the recyclerview in activity layout
        rvArticles = inflated.findViewById<View>(R.id.myAccountRecycler) as RecyclerView
        // Initialize contacts


        my_account_no_article = inflated.findViewById(R.id.my_account_no_article)
        my_account_no_article_descr = inflated.findViewById(R.id.my_account_no_article_descr)
        // Create adapter passing in the sample user data
        loadData()

//        // Attach the adapter to the recyclerview to populate items
//        rvArticles.adapter = adapter
//        //addOnClick
//        adapter.setOnItemClickListener(object :PostsAdapter.onItemClickListener{
//            override fun onItemClick(position: Int) {
//                Toast.makeText(activity, "You click on item no. $position", Toast.LENGTH_SHORT).show()
//
//                //recuperer items
////                var dialogPage = DialogFragmentDelete()
////                dialogPage.show(childFragmentManager, "Custom Dialog")
//
//                //creation du fragment de dialogue
//                val dialogPage = DialogueDeletePost()
//
//                dialogPage.show(childFragmentManager, "Custom Dialog")
//            }
//        })
//
//        rvArticles.layoutManager = LinearLayoutManager(activity)

        return inflated
    }


//        val textNoArticle = root.findViewById<TextView>(R.id.textNoArticles2)
    fun gonnaLoad(){
        Toast.makeText(context, "Publication supprimée", Toast.LENGTH_SHORT).show()
        loadData()
    }
    //for loading all articles from server
    fun loadData() {
        articlesSnapshots = ArrayList<String>()
        val db = Firebase.firestore
        articles.clear()
        my_account_no_article.text = ""
        my_account_no_article_descr.text = ""
        val adapter = ArticlesAdapter(articles)
        db.collection("Articles")
            .whereEqualTo("author", Firebase.auth.currentUser?.email)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    // Attach the adapter to the recyclerview to populate items
                    rvArticles.adapter = adapter

                    setAdapter(adapter, this)

                    // Set layout manager to position the items
                    rvArticles.layoutManager = LinearLayoutManager(view?.context)

                    my_account_no_article.text = getString(R.string.my_account_no_post)
                    my_account_no_article_descr.text = getString(R.string.my_account_no_post_descr)
//                    Toast.makeText(context, "No article Found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
//                val emailCurrentAuthor : String? = Firebase.auth.currentUser?.email
                for (doc in it) {
                    val article = doc.toObject(Article::class.java)
                    Log.v(article.date.toString(), "article")
                    articles.add(article)
                    articlesSnapshots.add(doc.toString().split("/")[1].split(",")[0])
//                    val temp = doc.getField<DocumentKey>("key")
//                    var tem = doc.toString().split("/")[1].split(",")[0]
//                    val zerfgh = doc.toObject(DocumentSnapshot::class.java)
//                    val myKey : DocumentKey = doc.get("key") as DocumentKey
//                    Log.d("TAG", "str")
                }

                // Attach the adapter to the recyclerview to populate items
                rvArticles.adapter = adapter

                setAdapter(adapter, this)

                // Set layout manager to position the items
                rvArticles.layoutManager = LinearLayoutManager(view?.context)

            }
    }

    private fun setAdapter(adapter: ArticlesAdapter, thisClass : MyAccountTabMyPosts) {

        adapter.setOnItemClickListener(object :ArticlesAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                //creation du fragment de dialogue
                val dialogPage = DialogueDeletePost()

                //ajout des infos dans le dialog
                dialogPage.arguments(articlesSnapshots[position], thisClass)
//                articles = articles.drop(position) as ArrayList<Article>
//                articlesSnapshots = articlesSnapshots.drop(position) as ArrayList<String>

                dialogPage.show(childFragmentManager, "Custom Dialog")
            }
        })
    }

}