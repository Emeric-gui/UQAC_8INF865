package com.example.project_uqac.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentHomeBinding
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.article.ArticlesAdapter
import com.example.project_uqac.ui.home.popupDiscussion.DialogFragmentDiscussion

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

        // Lookup the recyclerview in activity layout
        val rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        // Initialize contacts
        var articles = Article.createContactsList(19)
        // Create adapter passing in the sample user data
        val adapter = ArticlesAdapter(articles)
        // Attach the adapter to the recyclerview to populate items
        rvArticles.adapter = adapter


        //addOnClick
        adapter.setOnItemClickListener(object :ArticlesAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                //recuperer items
                val objet = adapter.getObjet(position)
                val lieu = adapter.getLieu(position)
                val date = adapter.getDate(position)

                //creation du fragment de dialogue
                val dialogPage = DialogFragmentDiscussion()

                //ajout des infos dans le dialog
                dialogPage.show(childFragmentManager, "Custom Dialog")
                dialogPage.addInfos(lieu, objet, date)
            }
        })

        // Set layout manager to position the items
        rvArticles.layoutManager = LinearLayoutManager(view?.context)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}