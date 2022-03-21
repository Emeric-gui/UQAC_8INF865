package com.example.project_uqac.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentChatBinding
import com.example.project_uqac.ui.discussions.DiscussionsFragment
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment(){
    private lateinit var dashboardViewModel: ChatViewModel
    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_Retour.setOnClickListener {
            val fr = parentFragmentManager.beginTransaction()
            fr.replace(R.id.nav_host_fragment_activity_main, DiscussionsFragment())
            fr.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}