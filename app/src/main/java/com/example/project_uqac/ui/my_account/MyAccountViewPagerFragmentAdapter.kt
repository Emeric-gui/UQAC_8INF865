package com.example.project_uqac.ui.my_account

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_uqac.R

class MyAccountViewPagerFragmentAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> MyAccountTabInformations()
            1 -> MyAccountTabMyPosts()
            else -> MyAccountTabInformations()
        }

    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position + 1)
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.tab_my_acc_1, R.string.tab_my_acc_2)

    }

}