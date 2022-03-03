package com.example.project_uqac.ui.discussions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.project_uqac.databinding.FragmentDiscussionsBinding
import com.example.project_uqac.ui.search.DiscussionsViewModel

class DiscussionsFragment : Fragment() {

    private lateinit var dashboardViewModel: DiscussionsViewModel
    private var _binding: FragmentDiscussionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DiscussionsViewModel::class.java)

        _binding = FragmentDiscussionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDiscussions
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}