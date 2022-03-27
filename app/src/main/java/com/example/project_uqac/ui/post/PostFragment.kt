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
import com.example.project_uqac.MainActivity
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentPostBinding
import com.example.project_uqac.ui.search.SearchFragment
import com.example.project_uqac.ui.service.LocationGPS
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PostFragment : Fragment() {

    private lateinit var dashboardViewModel: PostViewModel
    private var _binding: FragmentPostBinding? = null


    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())


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

        val position =  LocationGPS(context as MainActivity)
        //position.getLocationSearch(this)
        getPositionBackground(position, this)

        return root
    }

    fun getPositionBackground(
        position: LocationGPS,
        postFragment: PostFragment
    ) {
        executorService.execute {
            try {

                mainThreadHandler.post {  position.getLocationPost(postFragment) }
            } catch (e: Exception) {

            }
        }
    }

    fun getCoordinate(lat : Double,lon : Double) {
        this.lat = lat
        this.lon = lon
        Toast.makeText(
            activity,
            "Post Latitude: $lat , Longitude: $lon",
            Toast.LENGTH_SHORT
        ).show()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}