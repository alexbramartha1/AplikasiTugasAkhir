package com.reviling.filamentandroid.ui.detailgamelan

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.StatePlayer
import com.reviling.filamentandroid.databinding.ActivityDetailGamelanBinding
import com.reviling.filamentandroid.ui.CustomItemDecoration
import com.reviling.filamentandroid.ui.adapter.AudioAdapter
import com.reviling.filamentandroid.ui.adapter.HomeAdapter
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.adapter.UpacaraAdapter
import com.reviling.filamentandroid.ui.home.HomeViewModel
import com.reviling.filamentandroid.ui.inputgamelan.InputGamelanActivity
import com.reviling.filamentandroid.ui.inputinstrument.InputInstrumentActivity
import com.reviling.filamentandroid.ui.login.LoginActivity
import com.reviling.filamentandroid.ui.seeallgamelan.SeeAllGamelanBaliActivity
import com.reviling.filamentandroid.ui.seeallinstrument.DetailSeeAllInstrumentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class DetailGamelanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailGamelanBinding
    private lateinit var detailGamelanViewModel: DetailGamelanViewModel
    private lateinit var idGamelan: String
    private lateinit var upacaraAdapter: UpacaraAdapter
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var instrumentAdapter: InstrumentAdapter
    private var flagsBtnShow: Int = 0
    private lateinit var isLoadingBar: ProgressBar

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGamelanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idGamelan = intent.getStringExtra(ID).toString()

        lifecycleScope.launch {
            detailGamelanViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailGamelanActivity).create(DetailGamelanViewModel::class.java)
            }

            binding.showEverything.setOnClickListener {
                if (flagsBtnShow == 0) {
                    flagsBtnShow = 1
                    binding.deletebtn.visibility = View.VISIBLE
                    binding.editdatacard.visibility = View.VISIBLE
                } else {
                    flagsBtnShow = 0
                    binding.deletebtn.visibility = View.GONE
                    binding.editdatacard.visibility = View.GONE
                }
            }

            detailGamelanViewModel.geDetailGamelanInstrument(idGamelan).observe(this@DetailGamelanActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)
                            Log.d("ISIDATA", result.data.toString())
                            binding.gamelanName.text = result.data.gamelanData[0].namaGamelan
                            binding.gamelanDesc.text = result.data.gamelanData[0].description
                            binding.gamelanDescFull.text = result.data.gamelanData[0].description
                            binding.gamelanGolongan.text = getString(R.string.gol_value_full, result.data.gamelanData[0].golongan)

                            binding.buttonClickAll.setOnClickListener {
                                binding.gamelanDesc.visibility = View.GONE
                                binding.buttonClickAll.visibility = View.GONE
                                binding.buttonClickDown.visibility = View.VISIBLE
                                binding.gamelanDescFull.visibility = View.VISIBLE
                            }

                            binding.buttonClickDown.setOnClickListener {
                                binding.gamelanDesc.visibility = View.VISIBLE
                                binding.buttonClickAll.visibility = View.VISIBLE
                                binding.buttonClickDown.visibility = View.GONE
                                binding.gamelanDescFull.visibility = View.GONE
                            }

                            binding.deletebtn.setOnClickListener {
                                val builder = AlertDialog.Builder(this@DetailGamelanActivity)
                                val inflater = layoutInflater
                                val dialogView = inflater.inflate(R.layout.fragment_choose, null)

                                val buttonDelete = dialogView.findViewById<Button>(R.id.deletebtnsanggar)
                                val buttonBatal = dialogView.findViewById<Button>(R.id.batalbtn)
                                isLoadingBar = dialogView.findViewById(R.id.progress_bar_dialog_ask)

                                builder.setView(dialogView)
                                val dialog = builder.create()
                                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.show()

                                buttonDelete.setOnClickListener {
                                    detailGamelanViewModel.deleteGamelanDataByItsId(idGamelan).observe(this@DetailGamelanActivity) { result ->
                                        if (result != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    isLoadingDialog(true)
                                                }

                                                is Result.Success -> {
                                                    showToast(result.data.message)
                                                    detailGamelanViewModel.deleteManyAudioByItsGamelanId(idGamelan).observe(this@DetailGamelanActivity) { result ->
                                                        if (result != null) {
                                                            when (result) {
                                                                is Result.Loading -> {
                                                                    isLoadingDialog(true)
                                                                }

                                                                is Result.Success -> {
                                                                    showToast(result.data)
                                                                    isLoadingDialog(false)
                                                                    val intent = Intent(this@DetailGamelanActivity, SeeAllGamelanBaliActivity::class.java)
                                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                                    startActivity(intent)
                                                                }

                                                                is Result.Error -> {
                                                                    showToast(result.error)
                                                                    isLoadingDialog(false)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                is Result.Error -> {
                                                    showToast(result.error)
                                                    isLoadingDialog(false)
                                                }
                                            }
                                        }
                                    }
                                }
                                buttonBatal.setOnClickListener { dialog.dismiss() }

                            }

                            binding.editdatacard.setOnClickListener {
                                val intent = Intent(this@DetailGamelanActivity, InputGamelanActivity::class.java)
                                intent.putExtra(InputGamelanActivity.IDGAMELAN, idGamelan)
                                startActivity(intent)
                            }

                            upacaraAdapter = UpacaraAdapter(this@DetailGamelanActivity)
                            binding.rvUpacara.layoutManager = LinearLayoutManager(this@DetailGamelanActivity,  LinearLayoutManager.HORIZONTAL, false)
                            binding.rvUpacara.setHasFixedSize(true)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                            binding.rvUpacara.addItemDecoration(CustomItemDecoration(spacingInPixels, false))
                            binding.rvUpacara.adapter = upacaraAdapter
                            upacaraAdapter.setListUpacara(result.data.gamelanData[0].upacara)

                            audioAdapter = AudioAdapter(this@DetailGamelanActivity)
                            binding.rvAudio.layoutManager = LinearLayoutManager(this@DetailGamelanActivity,  LinearLayoutManager.VERTICAL, false)
                            binding.rvAudio.setHasFixedSize(false)
                            binding.rvAudio.adapter = audioAdapter
                            audioAdapter.setListAudio(result.data.gamelanData[0].audioGamelan)

                            instrumentAdapter = InstrumentAdapter(this@DetailGamelanActivity)
                            binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailGamelanActivity, 2)
                            binding.rvInstrumenHome.setHasFixedSize(true)
                            binding.rvInstrumenHome.adapter = instrumentAdapter
                            instrumentAdapter.setListInstrument(result.data.instrumentData)
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            isLoading(false)
                        }
                    }
                }
            }
        }

        binding.scrolling.setOnRefreshListener {
            audioAdapter.stopAudioPlayer()

            detailGamelanViewModel.geDetailGamelanInstrument(idGamelan).observe(this@DetailGamelanActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)
                            Log.d("ISIDATA", result.data.toString())
                            binding.gamelanName.text = result.data.gamelanData[0].namaGamelan
                            binding.gamelanDesc.text = result.data.gamelanData[0].description
                            binding.gamelanDescFull.text = result.data.gamelanData[0].description
                            binding.gamelanGolongan.text = getString(R.string.gol_value_full, result.data.gamelanData[0].golongan)

                            binding.buttonClickAll.setOnClickListener {
                                binding.gamelanDesc.visibility = View.GONE
                                binding.buttonClickAll.visibility = View.GONE
                                binding.buttonClickDown.visibility = View.VISIBLE
                                binding.gamelanDescFull.visibility = View.VISIBLE
                            }

                            binding.buttonClickDown.setOnClickListener {
                                binding.gamelanDesc.visibility = View.VISIBLE
                                binding.buttonClickAll.visibility = View.VISIBLE
                                binding.buttonClickDown.visibility = View.GONE
                                binding.gamelanDescFull.visibility = View.GONE
                            }

                            upacaraAdapter = UpacaraAdapter(this@DetailGamelanActivity)
                            binding.rvUpacara.layoutManager = LinearLayoutManager(this@DetailGamelanActivity,  LinearLayoutManager.HORIZONTAL, false)
                            binding.rvUpacara.setHasFixedSize(true)
                            binding.rvUpacara.adapter = upacaraAdapter
                            upacaraAdapter.setListUpacara(result.data.gamelanData[0].upacara)

                            audioAdapter = AudioAdapter(this@DetailGamelanActivity)
                            binding.rvAudio.layoutManager = LinearLayoutManager(this@DetailGamelanActivity,  LinearLayoutManager.VERTICAL, false)
                            binding.rvAudio.setHasFixedSize(false)
                            binding.rvAudio.adapter = audioAdapter
                            audioAdapter.setListAudio(result.data.gamelanData[0].audioGamelan)

                            instrumentAdapter = InstrumentAdapter(this@DetailGamelanActivity)
                            binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailGamelanActivity, 2)
                            binding.rvInstrumenHome.setHasFixedSize(true)
                            binding.rvInstrumenHome.adapter = instrumentAdapter
                            instrumentAdapter.setListInstrument(result.data.instrumentData)
                        }

                        is Result.Error -> {
                            showToast("There is no Gamelan Data using this Instrument")
                            isLoading(false)
                        }
                    }
                }
            }

            binding.scrolling.isRefreshing = false
        }


        supportActionBar?.hide()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun isLoadingDialog(loading: Boolean) {
        isLoadingBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ID = "gamelanId"
    }
}