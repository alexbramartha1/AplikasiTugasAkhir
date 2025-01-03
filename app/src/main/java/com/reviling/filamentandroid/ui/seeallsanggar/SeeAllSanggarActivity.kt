package com.reviling.filamentandroid.ui.seeallsanggar

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.databinding.ActivitySeeAllSanggarBinding
import com.reviling.filamentandroid.ui.adapter.SanggarViewPagerAdapter

class SeeAllSanggarActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeAllSanggarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllSanggarBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pagerAdapter = SanggarViewPagerAdapter(this@SeeAllSanggarActivity)
        val viewAdapter: ViewPager2 = binding.viewPager
        viewAdapter.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, viewAdapter) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.hide()
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.semua_sanggar,
            R.string.sanggar_anda
        )
    }
}