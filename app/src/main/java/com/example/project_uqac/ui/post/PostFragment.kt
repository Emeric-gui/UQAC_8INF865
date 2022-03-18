package com.example.project_uqac.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private lateinit var dashboardViewModel: PostViewModel
    private var _binding: FragmentPostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(PostViewModel::class.java)

        _binding = FragmentPostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*
        val textView: TextView = binding.textPost
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
         */

        childFragmentManager.beginTransaction().replace(R.id.post_fragment_navigation,PostFragmentNature()).commit()



        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}