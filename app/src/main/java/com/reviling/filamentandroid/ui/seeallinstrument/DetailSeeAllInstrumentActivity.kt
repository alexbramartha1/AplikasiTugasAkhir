package com.reviling.filamentandroid.ui.seeallinstrument

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.databinding.ActivityDetailSeeAllInstrumentBinding
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.ui.CustomItemDecorationAbove
import com.reviling.filamentandroid.ui.CustomItemDecorationVertical
import com.reviling.filamentandroid.ui.adapter.HomeAdapter
import com.reviling.filamentandroid.ui.home.HomeActivity
import com.reviling.filamentandroid.ui.inputinstrument.InputInstrumentActivity

class DetailSeeAllInstrumentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSeeAllInstrumentBinding
    private lateinit var seeAllInstrumentViewModel: SeeAllInstrumentViewModel
    private lateinit var adapterInstrument: InstrumentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSeeAllInstrumentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            seeAllInstrumentViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailSeeAllInstrumentActivity)
                    .create(SeeAllInstrumentViewModel::class.java)
            }

            binding.addDataInstrumentBtn.setOnClickListener {
                val intent = Intent(this@DetailSeeAllInstrumentActivity, InputInstrumentActivity::class.java)
                startActivity(intent)
            }

            seeAllInstrumentViewModel.getAllInstrumentData().observe(this@DetailSeeAllInstrumentActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            adapterInstrument = InstrumentAdapter(this@DetailSeeAllInstrumentActivity)
                            binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailSeeAllInstrumentActivity, 2)
                            val spacingInPixelsAbove = resources.getDimensionPixelSize(R.dimen.spacing_10dp)
                            binding.rvInstrumenHome.addItemDecoration(CustomItemDecorationAbove(spacingInPixelsAbove, false))
                            binding.rvInstrumenHome.setHasFixedSize(true)
                            binding.rvInstrumenHome.adapter = adapterInstrument
                            adapterInstrument.setListInstrument(result.data)
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            isLoading(false)
                        }
                    }
                }
            }

            binding.scrolling.setOnRefreshListener {
                seeAllInstrumentViewModel.getAllInstrumentData().observe(this@DetailSeeAllInstrumentActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast("Data Loaded")
                                isLoading(false)

                                adapterInstrument = InstrumentAdapter(this@DetailSeeAllInstrumentActivity)
                                binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailSeeAllInstrumentActivity, 2)
                                binding.rvInstrumenHome.setHasFixedSize(true)
                                binding.rvInstrumenHome.adapter = adapterInstrument
                                adapterInstrument.setListInstrument(result.data)
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                isLoading(false)
                            }
                        }
                    }
                }

                binding.scrolling.isRefreshing = false
            }

            binding.searchViewGamelan.setupWithSearchBar(binding.searchBarGamelan)

            binding.searchViewGamelan
                .editText
                .setOnEditorActionListener{ textView, actionId, event ->
                    binding.searchViewGamelan.hide()
                    val value = binding.searchViewGamelan.text.toString()
                    seeAllInstrumentViewModel.getInstrumentByName(value).observe(this@DetailSeeAllInstrumentActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)
                                    adapterInstrument = InstrumentAdapter(this@DetailSeeAllInstrumentActivity)
                                    binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailSeeAllInstrumentActivity, 2)
                                    binding.rvInstrumenHome.setHasFixedSize(true)
                                    binding.rvInstrumenHome.adapter = adapterInstrument
                                    adapterInstrument.setListInstrument(result.data)
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    isLoading(false)
                                }
                            }
                        }
                    }
                    Log.d("IsiSearchView", binding.searchViewGamelan.text.toString())
                    false
                }
        }
        
        supportActionBar?.hide()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}