package com.example.project_uqac.ui.my_account

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_uqac.R
import com.example.project_uqac.ui.my_account.tabs.MyAccountTabInformations
import com.example.project_uqac.ui.my_account.tabs.MyAccountTabMyPosts
import com.example.project_uqac.ui.my_account.tabs.MyAccountTabHome

class MyAccountViewPager2FragmentAdapter(fa: Fragment): FragmentStateAdapter(fa) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyAccountTabHome()
            1 -> MyAccountTabInformations()
            2 -> MyAccountTabMyPosts()
            else -> MyAccountTabHome()
        }

    }

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

    fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position + 1)
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.tab_my_acc_0, R.string.tab_my_acc_1, R.string.tab_my_acc_2)

    }

}