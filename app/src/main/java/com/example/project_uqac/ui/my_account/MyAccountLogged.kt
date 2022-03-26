package com.example.project_uqac.ui.my_account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.project_uqac.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MyAccountLogged : Fragment() {

    private lateinit var viewPager2 : ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_account_logged, container, false)
        val img : ImageView = view.findViewById(R.id.imageViewMyAccountLogged)
        img.setImageResource(R.drawable.ic_action_arrows_left)
        Picasso.get().load("https://picsum.photos/300/300?random").into(img)

//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
//            // Handle the back button event
//        }

        // Print username
        val user = Firebase.auth.currentUser
        val username : TextView = view.findViewById(R.id.username)
        if (user != null) {
            username.setText(user.displayName.toString())
        } else {
            username.setText("Error")
        }

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager2 = view.findViewById(R.id.my_account_logged_viewer)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = MyAccountViewPager2FragmentAdapter(this)
        viewPager2.adapter = pagerAdapter

        val TAB_TITLES = arrayOf("Informations", "My Posts")

        val tabLayout : TabLayout = view.findViewById(R.id.my_account_logged_tabs)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()


//        requireActivity()
//            .onBackPressedDispatcher
//            .addCallback(this, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    Log.d(TAG, "Fragment back pressed invoked")
//                    // Do custom work here
//
//                    // if you want onBackPressed() to be called as normal afterwards
//                    if (isEnabled) {
//                        isEnabled = false
//                        requireActivity().onBackPressed()
//                    }
//                }
//            }
//            )

//        val viewPager : ViewPager = view.findViewById(R.id.my_account_logged_viewer) // Good
//        viewPager.adapter = activity?.let { MyAccountViewPagerFragmentAdapter(it.supportFragmentManager) }

//        val tabLayout : TableLayout = view.findViewById(R.id.my_account_logged_tabs)
//        tabLayout.setupWithViewPager(viewPager)

//        TabLayoutMediator(view.findViewById(R.id.my_account_logged_tabs), view.findViewById(R.id.my_account_logged_viewer)) {
//                tab, position -> tab.text = TAB_TITLES[position]
//        }.attach()
//        val tabLayout : TabLayout = view.findViewById(R.id.my_account_logged_tabs)
//        tabLayout.addTab(tabLayout.newTab().setText("Bar"));
//        tabLayout.addTab(tabLayout.newTab().setText("Foo"));
//        val myAccountViewPagerFragmentAdapter : MyAccountViewPagerFragmentAdapter = MyAccountViewPagerFragmentAdapter()
//        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//        val viewPager : ViewPager = view.findViewById(R.id.my_account_logged_viewer)
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs : TabLayout = view.findViewById(R.id.my_account_logged_tabs)
//        tabs.setupWithViewPager(viewPager)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {

                if(viewPager2.currentItem > 0)
                    viewPager2.currentItem = viewPager2.currentItem - 1
                else{
//                    requireActivity().onBackPressed()
                    Log.d("TAG", "message")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

}