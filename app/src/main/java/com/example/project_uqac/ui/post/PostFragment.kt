package com.example.project_uqac.ui.post

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentPostBinding
import com.example.project_uqac.ui.my_account.MyAccountLogged
import com.example.project_uqac.ui.my_account.MyAccountLogin
import com.example.project_uqac.ui.my_account.MyAccountRegister
import com.example.project_uqac.ui.search.SearchFragment
import com.example.project_uqac.ui.service.LocationGPS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PostFragment : Fragment() {

    private lateinit var dashboardViewModel: PostViewModel
    private var _binding: FragmentPostBinding? = null


    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    private lateinit var auth: FirebaseAuth


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

        // Initialize Firebase Auth
        auth = Firebase.auth

        if (Firebase.auth.currentUser != null){
            childFragmentManager.beginTransaction().replace(R.id.post_fragment_navigation,PostFragmentNature()).commit()

        } else {
            Toast.makeText(
                context,
                "Vous devez vous connecter ...",
                Toast.LENGTH_SHORT
            ).show()
           this.findNavController().navigate(R.id.navigation_my_account)

        }

       // childFragmentManager.beginTransaction().replace(R.id.post_fragment_navigation,PostFragmentNature()).commit()

        val position =  LocationGPS(context as MainActivity)
        getPositionBackground(position, this)
        Toast.makeText(
            context,
            "PostFragment Ecriture data",
            Toast.LENGTH_SHORT
        ).show()

        return root
    }

    private fun getPositionBackground(
        position: LocationGPS,
        postFragment: PostFragment
    ) {
        executorService.execute {
            try {

                mainThreadHandler.post {  position.getLocation() }
            } catch (e: Exception) {

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}