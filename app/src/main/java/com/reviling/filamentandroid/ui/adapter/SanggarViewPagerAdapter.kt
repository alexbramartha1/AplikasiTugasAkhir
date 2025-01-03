package com.reviling.filamentandroid.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.reviling.filamentandroid.ui.seeallsanggar.AllAndYourSanggarFragment
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity

class SanggarViewPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity)  {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = AllAndYourSanggarFragment()

        fragment.arguments = Bundle().apply {
            putInt(AllAndYourSanggarFragment.ARG_POSITION, position + 1)
        }

        return fragment
    }
}