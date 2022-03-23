package com.example.project_uqac.ui.search.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.project_uqac.R

class FilterTabPosition : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)
        return inflater.inflate(R.layout.fragment_filter_map, container, false)
    }
}