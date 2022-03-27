package com.example.project_uqac.ui.my_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentLoginBinding
import com.example.project_uqac.databinding.FragmentMyAccountBinding
import com.example.project_uqac.ui.post.PostFragmentNature

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

        childFragmentManager.beginTransaction().replace(
            R.id.my_account_fragment_navigation,
            MyAccountLogin()
        ).commit()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
