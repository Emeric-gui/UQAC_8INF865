package com.example.project_uqac.ui.search.filter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils.getMonthString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.project_uqac.R
import com.example.project_uqac.ui.search.SearchFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_filter.view.*


class DialogueFragmentFilter(searchFragment: SearchFragment) :DialogFragment(){
    private lateinit var viewFilter : ViewPager2
    private var lat : Double = 37.406474
    private var lon : Double = -122.078184
    private var date :Int = 0
    private var radius : Int = 1
    private var searchFragment : SearchFragment = searchFragment

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :
   View? {
      var rootView : View = inflater.inflate(R.layout.fragment_filter, container, false)

         rootView.button_valider.setOnClickListener(){
             if (date != 0 && lon != 0.0 && lat != 0.0) {
                 searchFragment.setData(date, lat, lon, radius)
                 dismiss()
             }
             if (date == 0) {
                 Toast.makeText(context, "Veuillez configurer la date ...", Toast.LENGTH_SHORT).show()
             }
         }
         // Instantiate a ViewPager2 and a PagerAdapter.
         viewFilter = rootView.findViewById(R.id.filter_viewer)
         viewFilter.isUserInputEnabled = false;

         // The pager adapter, which provides the pages to the view pager widget.
         val pagerAdapter = FiltreViewPager2FragmentAdapter(this)
         pagerAdapter.setData(this)
         viewFilter.adapter = pagerAdapter

         val TAB_TITLES = arrayOf("Où ?", "Quand ?")

         val tabLayout : TabLayout = rootView.findViewById(R.id.filter_tabs)
         tabLayout.setTabTextColors(Color.parseColor("#888888"), Color.parseColor("#3F51B5"))
         TabLayoutMediator(tabLayout, viewFilter) { tab, position ->
             tab.text = TAB_TITLES[position]
         }.attach()

      return rootView
   }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                Log.d("TAG", "message")
                viewFilter.currentItem = viewFilter.currentItem - 1
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    fun setLat (lat : Double) {
        this.lat = lat
    }

    fun setLon( lon : Double) {
        this.lon = lon
    }

    fun setDate( date : Int) {
        this.date = date
    }

    fun setRadius(radius: Int) {
        this.radius = radius
    }


}
