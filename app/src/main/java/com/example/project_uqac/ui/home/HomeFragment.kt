package com.example.project_uqac.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentHomeBinding
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.article.ArticlesAdapter

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })


        //val posts: ArrayList<Post> = ArrayList()
        //for (i in 1..100) {
        //    posts.add(Post("Mon Giga Titre_" + i, "Ma giga description qui nique des grandes daronnes tellement elle est longue.", "https://picsum.photos/600/300?random&" + i))
        //}
//
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.adapter = PostsAdapter(posts, this)

        // Add the following lines to create RecyclerView
        //val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(LinearLayoutManager(view?.getContext()));
        //recyclerView.setAdapter(PostAdapter(posts, 1234));

        // Lookup the recyclerview in activity layout
        val rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        // Initialize contacts
        var articles = Article.createContactsList(19)
        // Create adapter passing in the sample user data
        val adapter = ArticlesAdapter(articles)
        // Attach the adapter to the recyclerview to populate items
        rvArticles.adapter = adapter
        // Set layout manager to position the items
        rvArticles.layoutManager = LinearLayoutManager(view?.context)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}