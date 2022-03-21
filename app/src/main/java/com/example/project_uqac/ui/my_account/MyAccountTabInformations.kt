package com.example.project_uqac.ui.my_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.google.android.material.tabs.TabLayout

class MyAccountTabInformations : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        view?.findViewById<TabLayout?>(R.id.my_account_logged_tabs)?.setupWithViewPager(viewPager)
        return inflater.inflate(R.layout.fragment_my_account_informations, container, false)
    }
}