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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.StatePlayer
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.databinding.ActivityDetailGamelanBinding
import com.reviling.filamentandroid.ui.BaseActivity
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

class DetailGamelanActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailGamelanBinding
    private lateinit var detailGamelanViewModel: DetailGamelanViewModel
    private lateinit var idGamelan: String
    private lateinit var upacaraAdapter: UpacaraAdapter
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var instrumentAdapter: InstrumentAdapter
    private var flagsBtnShow: Int = 0
    private lateinit var isLoadingBar: ProgressBar
    private var statusDetail: String? = null

    @SuppressLint("NotifyDataSetChanged", "InflateParams")
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

            detailGamelanViewModel.getSessionUser().observe(this@DetailGamelanActivity) { user ->
                if (user.isLogin) {
                    if (user.role == "67619109cc4fa7bc6c0bdbc8" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                        binding.showEverythingCard.visibility = View.VISIBLE
                        binding.statusBtn.visibility = View.VISIBLE
                    } else {
                        binding.showEverythingCard.visibility = View.GONE
                        binding.statusBtn.visibility = View.GONE
                    }
                }
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

            binding.statusBtn.setOnClickListener {
                val dialog = layoutInflater.inflate(R.layout.status_fragment, null)
                val builder = BottomSheetDialog(this@DetailGamelanActivity)
                val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                builder.setContentView(dialog)
                builder.show()

                detailGamelanViewModel.getNoteData(idGamelan).observe(this@DetailGamelanActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoadingBar.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                descriptionStatus.visibility = View.VISIBLE
                                descriptionStatus.text = result.data.note
                                statusApproval.text = statusDetail

                                if (statusDetail == "Pending") {
                                    statusApproval.setTextColor(getColor(R.color.white))
                                    statusApproval.setBackgroundColor(getColor(R.color.pendingColor))
                                } else if (statusDetail == "Unapproved") {
                                    statusApproval.setTextColor(getColor(R.color.white))
                                    statusApproval.setBackgroundColor(getColor(R.color.unapprovedColor))
                                } else if (statusDetail == "Approved") {
                                    statusApproval.setTextColor(getColor(R.color.white))
                                    statusApproval.setBackgroundColor(getColor(R.color.approvedColor))
                                }
                                isLoadingBar.visibility = View.GONE
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                isLoadingBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }

            detailGamelanViewModel.geDetailGamelanInstrument(idGamelan).observe(this@DetailGamelanActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            detailGamelanViewModel.getListStatus().observe(this@DetailGamelanActivity) { status ->
                                if (status != null) {
                                    when (status) {
                                        is Result.Loading -> {
                                            isLoading(true)
                                        }

                                        is Result.Success -> {
                                            val statusGamelan = result.data.gamelanData[0].status

                                            status.data.forEach {
                                                if (it.id == statusGamelan) {
                                                    binding.statusBtn.text = it.status
                                                    statusDetail = it.status

                                                    if (it.status == "Pending") {
                                                        binding.statusBtn.setTextColor(getColor(R.color.white))
                                                        binding.statusBtn.setBackgroundColor(getColor(R.color.pendingColor))
                                                    } else if (it.status == "Unapproved") {
                                                        binding.statusBtn.setTextColor(getColor(R.color.white))
                                                        binding.statusBtn.setBackgroundColor(getColor(R.color.unapprovedColor))
                                                    } else if (it.status == "Approved") {
                                                        binding.statusBtn.setTextColor(getColor(R.color.white))
                                                        binding.statusBtn.setBackgroundColor(getColor(R.color.approvedColor))
                                                    }
                                                }
                                            }

                                            showToast("Data Loaded")
                                            isLoading(false)
                                        }

                                        is Result.Error -> {
                                            showToast(status.error)
                                            isLoading(false)
                                        }
                                    }
                                }
                            }

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
                            showToast("Data Loaded")
                            isLoading(false)
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
                            detailGamelanViewModel.getListStatus().observe(this@DetailGamelanActivity) { status ->
                                if (status != null) {
                                    when (status) {
                                        is Result.Loading -> {
                                            isLoading(true)
                                        }

                                        is Result.Success -> {
                                            val statusSanggar = result.data.gamelanData[0].status

                                            status.data?.forEach {
                                                if (it?.id == statusSanggar) {
                                                    binding.statusBtn.text = it.status

                                                    if (it.status == "Pending") {
                                                        binding.statusBtn.setTextColor(getColor(R.color.white))
                                                        binding.statusBtn.setBackgroundColor(getColor(R.color.pendingColor))
                                                    } else if (it.status == "Unapproved") {
                                                        binding.statusBtn.setTextColor(getColor(R.color.white))
                                                        binding.statusBtn.setBackgroundColor(getColor(R.color.unapprovedColor))
                                                    } else if (it.status == "Approved") {
                                                        binding.statusBtn.setTextColor(getColor(R.color.white))
                                                        binding.statusBtn.setBackgroundColor(getColor(R.color.approvedColor))
                                                    }
                                                }
                                            }

                                            showToast("Data Loaded")
                                            isLoading(false)
                                        }

                                        is Result.Error -> {
                                            showToast(status.error)
                                            isLoading(false)
                                        }
                                    }
                                }
                            }

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
                            showToast("Data Loaded")
                            isLoading(false)
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

    override fun repeatFunction() {
        lifecycleScope.launch {
            detailGamelanViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailGamelanActivity)
                    .create(DetailGamelanViewModel::class.java)
            }

            detailGamelanViewModel.getSessionUser().observe(this@DetailGamelanActivity) { user ->
                if (user.isLogin) {
                    detailGamelanViewModel.getUserDatabyId(user.user_id)
                        .observe(this@DetailGamelanActivity) { save ->
                            if (save != null) {
                                when (save) {
                                    is Result.Loading -> {}

                                    is Result.Success -> {
                                        if (save.data.status != user.status) {
                                            detailGamelanViewModel.saveSession(
                                                UserModel(
                                                    nama = save.data.nama,
                                                    access_token = user.access_token,
                                                    email = save.data.email,
                                                    user_id = save.data.id,
                                                    foto_profile = save.data.fotoProfile,
                                                    role = save.data.role,
                                                    status = save.data.status,
                                                    isLogin = true,
                                                    document = save.data.supportDocument
                                                )
                                            )
                                            if (user.role == "67619109cc4fa7bc6c0bdbc8" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                binding.showEverythingCard.visibility = View.VISIBLE
                                                binding.statusBtn.visibility = View.VISIBLE
                                            } else {
                                                binding.showEverythingCard.visibility = View.GONE
                                                binding.statusBtn.visibility = View.GONE
                                            }
                                        }
                                    }

                                    is Result.Error -> {
                                        showToast(save.error)
                                    }
                                }
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

    private fun isLoadingDialog(loading: Boolean) {
        isLoadingBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ID = "gamelanId"
    }
}