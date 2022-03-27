package com.example.project_uqac.ui.my_account.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.example.project_uqac.ui.my_account.myPosts.Post
import com.example.project_uqac.ui.my_account.myPosts.PostsAdapter
import com.example.project_uqac.ui.home.popupDiscussion.DialogFragmentDiscussion
import com.example.project_uqac.ui.my_account.popupDelete.DialogFragmentDelete


class MyAccountTabMyPosts : Fragment() {
//
//    private var _binding: FragmentMyAccountMyPostsBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var rvArticles : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflated = inflater.inflate(R.layout.fragment_my_account_my_posts, container, false)
        // Lookup the recyclerview in activity layout
        val rvArticles = inflated.findViewById<View>(R.id.myAccountRecycler) as RecyclerView
        // Initialize contacts
        var articles = Post.createContactsList(20)
        // Create adapter passing in the sample user data
        val adapter = PostsAdapter(articles)
        // Attach the adapter to the recyclerview to populate items
        rvArticles.adapter = adapter

        //addOnClick
        adapter.setOnItemClickListener(object :PostsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(activity, "You click on item no. $position", Toast.LENGTH_SHORT).show()

                //recuperer items
                var dialogPage = DialogFragmentDelete()
                dialogPage.show(childFragmentManager, "Custom Dialog")
            }
        })

        rvArticles.layoutManager = LinearLayoutManager(activity)

        return inflated
    }
}