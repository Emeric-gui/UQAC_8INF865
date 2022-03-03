package com.example.project_uqac.ui.my_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.project_uqac.databinding.FragmentMyAccountBinding
import com.example.project_uqac.ui.my_account.MyAccountViewModel

class MyAccountFragment : Fragment() {

    private lateinit var dashboardViewModel: MyAccountViewModel
    private var _binding: FragmentMyAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(MyAccountViewModel::class.java)

        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMyAccount
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