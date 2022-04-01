package com.example.project_uqac.ui.discussions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentDiscussionsBinding
import com.example.project_uqac.ui.conversation.Conversation
import com.example.project_uqac.ui.conversation.ConversationsAdapter
import com.example.project_uqac.ui.chat.ChatFragment
import com.example.project_uqac.ui.post.PostFragmentNature
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chat.*

class DiscussionsFragment : Fragment() {

    private lateinit var dashboardViewModel: DiscussionsViewModel
    private var _binding: FragmentDiscussionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var _context: Context
    private lateinit var listView: ListView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DiscussionsViewModel::class.java)

        _binding = FragmentDiscussionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase Auth
        auth = Firebase.auth

        if (Firebase.auth.currentUser != null){

            listView = root.findViewById<ListView>(R.id.recipe_list_view)


            var conversations = Conversation.createConversationList(19)
            val adapter = ConversationsAdapter(_context,conversations)

            listView.adapter = adapter

            listView.setOnItemClickListener { parent, view, position, id ->
                val fr = parentFragmentManager.beginTransaction()
                fr.replace(R.id.nav_host_fragment_activity_main, ChatFragment())
                fr.commit()
                Log.d("debug", "test 3")
            }
            //val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,listOf("car", "plane"))
            //list.setOnClickListener(AdapterView.OnItemClickListener())


        } else {
            Toast.makeText(
                context,
                "Vous devez vous connecter ...",
                Toast.LENGTH_SHORT
            ).show()
            this.findNavController().navigate(R.id.navigation_my_account)

        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context=context
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}