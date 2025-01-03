package com.reviling.filamentandroid.ui.seeallgamelan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.databinding.ActivityDetailGamelanBinding
import com.reviling.filamentandroid.databinding.ActivitySeeAllGamelanBaliBinding
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.seeallinstrument.SeeAllInstrumentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.ui.CustomItemDecorationVertical
import com.reviling.filamentandroid.ui.adapter.GamelanAdapter
import com.reviling.filamentandroid.ui.inputgamelan.InputGamelanActivity

class SeeAllGamelanBaliActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeAllGamelanBaliBinding
    private lateinit var seeAllGamelanViewModel: SeeAllGamelanViewModel
    private lateinit var adapter: GamelanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllGamelanBaliBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            seeAllGamelanViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@SeeAllGamelanBaliActivity)
                    .create(SeeAllGamelanViewModel::class.java)
            }

            seeAllGamelanViewModel.seeAllGamelanBali().observe(this@SeeAllGamelanBaliActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                            binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                            binding.rvGamelanBaliHome.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                            binding.rvGamelanBaliHome.setHasFixedSize(true)
                            binding.rvGamelanBaliHome.adapter = adapter

                            adapter.setListGamelan(result.data)
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            isLoading(false)
                        }
                    }
                }
            }

            binding.addDataGamelanBtn.setOnClickListener {
                val intent = Intent(this@SeeAllGamelanBaliActivity, InputGamelanActivity::class.java)
                startActivity(intent)
            }

            binding.seeallgolongan.setOnClickListener {
                loadAllGamelan()
                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
            }

            binding.golonganTua.setOnClickListener {
                loadGamelanByGolongan("6761a2ddcc4fa7bc6c0bdbe6")
                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
            }

            binding.golonganMadya.setOnClickListener {
                loadGamelanByGolongan("6761a2e8cc4fa7bc6c0bdbe8")
                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
            }

            binding.golonganBaru.setOnClickListener {
                loadGamelanByGolongan("6761a2f1cc4fa7bc6c0bdbea")
                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
            }
        }

        binding.scrolling.setOnRefreshListener {
            loadAllGamelan()
            binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
            binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
            binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
            binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))

            binding.scrolling.isRefreshing = false
        }

        binding.searchViewGamelan.setupWithSearchBar(binding.searchBarGamelan)

        binding.searchViewGamelan
            .editText
            .setOnEditorActionListener{ textView, actionId, event ->
                binding.searchViewGamelan.hide()
                val value = binding.searchViewGamelan.text.toString()
                loadGamelanByName(value)
                false
            }

        supportActionBar?.hide()
    }

    private fun loadGamelanByName(gamelan: String) {
        seeAllGamelanViewModel.getSearchAllGamelanInstrument(gamelan).observe(this@SeeAllGamelanBaliActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                        binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                        binding.rvGamelanBaliHome.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                        binding.rvGamelanBaliHome.setHasFixedSize(true)
                        binding.rvGamelanBaliHome.adapter = adapter

                        adapter.setListGamelan(result.data.gamelanData)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun loadAllGamelan() {
        seeAllGamelanViewModel.seeAllGamelanBali().observe(this@SeeAllGamelanBaliActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                        binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                        binding.rvGamelanBaliHome.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                        binding.rvGamelanBaliHome.setHasFixedSize(true)
                        binding.rvGamelanBaliHome.adapter = adapter

                        adapter.setListGamelan(result.data)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun loadGamelanByGolongan(golongan: String) {
        if (golongan != "lihatsemua") {
            seeAllGamelanViewModel.getGamelanByGolongan(golongan).observe(this@SeeAllGamelanBaliActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                            binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                            binding.rvGamelanBaliHome.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                            binding.rvGamelanBaliHome.setHasFixedSize(true)
                            binding.rvGamelanBaliHome.adapter = adapter

                            adapter.setListGamelan(result.data)
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            isLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

}