package com.example.project_uqac.ui.search.filter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.project_uqac.R
import com.example.project_uqac.ui.my_account.MyAccountViewPager2FragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_filter.view.*

class DialogueFragmentFilter:DialogFragment()  {

    private lateinit var viewFilter : ViewPager2

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) :
   View? {
      var rootView : View = inflater.inflate(R.layout.fragment_filter, container, false)

         rootView.button_valider.setOnClickListener(){
             dismiss()
         }
         // Instantiate a ViewPager2 and a PagerAdapter.
         viewFilter = rootView.findViewById(R.id.filter_viewer)

         // The pager adapter, which provides the pages to the view pager widget.
         val pagerAdapter = FiltreViewPager2FragmentAdapter(this)
         viewFilter.adapter = pagerAdapter

         val TAB_TITLES = arrayOf("Où ?", "Quand ?")

         val tabLayout : TabLayout = rootView.findViewById(R.id.filter_tabs)
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

}
