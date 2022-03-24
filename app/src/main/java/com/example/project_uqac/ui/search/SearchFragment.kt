package com.example.project_uqac.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentSearchBinding
import com.example.project_uqac.ui.article.Article
import com.example.project_uqac.ui.article.ArticlesAdapter
import com.example.project_uqac.ui.home.popupDiscussion.DialogFragmentDiscussion
import com.example.project_uqac.ui.search.filter.DialogueFragmentFilter

class SearchFragment  : Fragment()  {

    private lateinit var dashboardViewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null

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

        // Lookup the recyclerview in activity layout
        val rvArticles = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        // Initialize contacts
        var articles = Article.createContactsList(19)
        // Create adapter passing in the sample user data
        val adapter = ArticlesAdapter(articles)
        // Attach the adapter to the recyclerview to populate items
        rvArticles.adapter = adapter


        adapter.setOnItemClickListener(object :ArticlesAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                //recuperer items
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


        // Set layout manager to position the items
        rvArticles.layoutManager = LinearLayoutManager(view?.context)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}