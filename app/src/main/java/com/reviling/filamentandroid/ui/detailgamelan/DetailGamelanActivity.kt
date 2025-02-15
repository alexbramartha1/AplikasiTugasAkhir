package com.reviling.filamentandroid.ui.detailgamelan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
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
import com.google.android.filament.Material
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.StatePlayer
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.data.response.InstrumentDataItem
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
    private lateinit var isLoadingEditApproval: ProgressBar
    private var statusDetail: String? = null
    private var statusGamelanListName: MutableList<String> = mutableListOf()
    private var statusGamelanListId: MutableList<String> = mutableListOf()

    private var dataSetApproved: MutableList<InstrumentDataItem> = mutableListOf()
    private var fullShowOrNot: Boolean = true

    private var userRole: String? = null
    private var userStatus: String? = null

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

        binding.statusBtn.isEnabled = false

        idGamelan = intent.getStringExtra(ID).toString()

        lifecycleScope.launch {
            detailGamelanViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailGamelanActivity).create(DetailGamelanViewModel::class.java)
            }

            detailGamelanViewModel.getSessionUser().observe(this@DetailGamelanActivity) { user ->

                userRole = user.role
                userStatus = user.status

                if (user.role == "676190f1cc4fa7bc6c0bdbc4" || user.status == "67618f9ecc4fa7bc6c0bdbbb" || user.status == "67618fe3cc4fa7bc6c0bdbc1") {
                    fullShowOrNot = true
                } else {
                    fullShowOrNot = false
                }

                if (user.isLogin) {
                    if (user.role == "676190fdcc4fa7bc6c0bdbc6" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                        binding.statusBtn.visibility = View.VISIBLE
                    }

                    if (user.role == "67619109cc4fa7bc6c0bdbc8" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                        binding.showEverythingCard.visibility = View.VISIBLE
                        binding.statusBtn.visibility = View.VISIBLE
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
                var selectedItem: Int? = null
                var idApprovalSelected: String? = null
                var namaApprovalSelected: String? = null

                val dialog = layoutInflater.inflate(R.layout.status_fragment, null)
                val builder = BottomSheetDialog(this@DetailGamelanActivity)
                val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                editApproval.isEnabled = false
                var dataDeskripsi: String? = null
                var idStatus: String? = null

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

                                dataDeskripsi = result.data.note
                                idStatus = result.data.idStatus

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
                                editApproval.isEnabled = true
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                isLoadingBar.visibility = View.GONE
                            }
                        }
                    }
                }

                if (userRole == "676190fdcc4fa7bc6c0bdbc6" && userStatus == "67618fc3cc4fa7bc6c0bdbbf") {
                    editApproval.visibility = View.VISIBLE
                }

                editApproval.setOnClickListener {
                    val dialogView = layoutInflater.inflate(R.layout.edit_approval_fragment, null)
                    val dialogBuilder = AlertDialog.Builder(this@DetailGamelanActivity)
                    val ubahBtn: MaterialButton = dialogView.findViewById(R.id.ubahApproval)
                    val batalBtn: MaterialButton = dialogView.findViewById(R.id.batalApproval)
                    val deskripsiView: TextInputEditText = dialogView.findViewById(R.id.descriptionApproval)
                    val approvalChoose: MaterialButton = dialogView.findViewById(R.id.statusEdit)
                    isLoadingEditApproval = dialogView.findViewById(R.id.progress_bar_dialog_edit_approval)

                    deskripsiView.setText(dataDeskripsi)
                    approvalChoose.text = statusDetail
                    if (statusDetail == "Pending") {
                        approvalChoose.setTextColor(getColor(R.color.white))
                        approvalChoose.setBackgroundColor(getColor(R.color.pendingColor))
                    } else if (statusDetail == "Unapproved") {
                        approvalChoose.setTextColor(getColor(R.color.white))
                        approvalChoose.setBackgroundColor(getColor(R.color.unapprovedColor))
                    } else if (statusDetail == "Approved") {
                        approvalChoose.setTextColor(getColor(R.color.white))
                        approvalChoose.setBackgroundColor(getColor(R.color.approvedColor))
                    }

                    dialogBuilder.setView(dialogView)
                    dialogBuilder.create()
                    val editDialog = dialogBuilder.show()

                    approvalChoose.setOnClickListener {
                        val dialogViewList = layoutInflater.inflate(R.layout.status_list_layout, null)
                        val builderDialog = AlertDialog.Builder(this@DetailGamelanActivity)
                        val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                        builderDialog.setView(dialogViewList)
                        builderDialog.create()
                        val dialogWindow = builderDialog.show()

                        val adapter = ArrayAdapter(this@DetailGamelanActivity, android.R.layout.simple_list_item_single_choice, statusGamelanListName)
                        listViewStatus.adapter = adapter
                        listViewStatus.choiceMode = ListView.CHOICE_MODE_SINGLE

                        if (selectedItem != null) {
                            listViewStatus.setItemChecked(selectedItem!!, true)
                        } else {
                            listViewStatus.setItemChecked(statusGamelanListId.indexOf(idStatus), true)
                        }

                        listViewStatus.setOnItemClickListener { adapterView, view, i, l ->
                            namaApprovalSelected = statusGamelanListName[i]
                            idApprovalSelected = statusGamelanListId[i]
                            selectedItem = i

                            approvalChoose.text = namaApprovalSelected
                            if (namaApprovalSelected == "Pending") {
                                approvalChoose.setTextColor(getColor(R.color.white))
                                approvalChoose.setBackgroundColor(getColor(R.color.pendingColor))
                            } else if (namaApprovalSelected == "Unapproved") {
                                approvalChoose.setTextColor(getColor(R.color.white))
                                approvalChoose.setBackgroundColor(getColor(R.color.unapprovedColor))
                            } else if (namaApprovalSelected == "Approved") {
                                approvalChoose.setTextColor(getColor(R.color.white))
                                approvalChoose.setBackgroundColor(getColor(R.color.approvedColor))
                            }
                            dialogWindow.dismiss()
                        }
                    }

                    ubahBtn.setOnClickListener {
                        val approvalNote = deskripsiView.text.toString()
                        var statusId = idApprovalSelected

                        if (idStatus != null && statusId == null) {
                            statusId = idStatus
                        }

                        if (approvalNote.isEmpty()) {
                            deskripsiView.error = getString(R.string.cannot_empty)
                        } else if (statusId == null) {
                            showToast(getString(R.string.pilih_status_approval))
                        } else {
                            detailGamelanViewModel.updateApprovalGamelan(idGamelan, approvalNote, statusId).observe(this@DetailGamelanActivity) { update ->
                                if (update != null) {
                                    when (update) {
                                        is Result.Loading -> {
                                            isLoadingEditApproval.visibility = View.VISIBLE
                                        }

                                        is Result.Success -> {
                                            showToast(update.data)
                                            updateStateData()
                                            isLoadingEditApproval.visibility = View.GONE
                                            editDialog.dismiss()
                                            builder.dismiss()
                                        }

                                        is Result.Error -> {
                                            showToast(update.error)
                                            isLoadingEditApproval.visibility = View.GONE
                                        }
                                    }
                                }
                            }
                        }
                    }

                    batalBtn.setOnClickListener {
                        namaApprovalSelected = null
                        idApprovalSelected = null
                        selectedItem = null
                        editDialog.dismiss()
                    }

                    editDialog.setOnCancelListener {
                        namaApprovalSelected = null
                        idApprovalSelected = null
                        selectedItem = null
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
                                                statusGamelanListName.add(it.status)
                                                statusGamelanListId.add(it.id)

                                                if (it.id == statusGamelan) {
                                                    binding.statusBtn.text = it.status
                                                    statusDetail = it.status
                                                    binding.statusBtn.isEnabled = true
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
                                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                                    startActivity(intent)
                                                                    finish()
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
                                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
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
                            Log.d("IsiDariFullshow", fullShowOrNot.toString())
                            dataSetApproved.clear()
                            if (fullShowOrNot) {
                                result.data.instrumentData.forEach {
                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                        dataSetApproved.add(it)
                                    }
                                }
                                instrumentAdapter.setListInstrument(dataSetApproved)
                            } else {
                                instrumentAdapter.setListInstrument(result.data.instrumentData)
                            }

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
            binding.statusBtn.isEnabled = false

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

                                            status.data.forEach {
                                                if (it.id == statusSanggar) {
                                                    binding.statusBtn.text = it.status
                                                    statusDetail = it.status
                                                    binding.statusBtn.isEnabled = true

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
                            dataSetApproved.clear()
                            if (fullShowOrNot) {
                                result.data.instrumentData.forEach {
                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                        dataSetApproved.add(it)
                                    }
                                }
                                instrumentAdapter.setListInstrument(dataSetApproved)
                            } else {
                                instrumentAdapter.setListInstrument(result.data.instrumentData)
                            }
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
                                            if (save.data.role == "67619109cc4fa7bc6c0bdbc8" && save.data.status == "67618fc3cc4fa7bc6c0bdbbf") {
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
                                        Log.d("IsiDariSaveError", save.error)
                                        if (save.error == "Sorry, There is no user with this name ${user.user_id}" || save.error == "Sorry, Token has expired, please login again!" || save.error == "Sorry, Token Invalid!") {
                                            detailGamelanViewModel.logoutUser()
                                            val intentMain = Intent(this@DetailGamelanActivity, LoginActivity::class.java)
                                            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intentMain)
                                            finish()
                                        }
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

    private fun updateStateData() {
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

                                        status.data.forEach {
                                            if (it.id == statusSanggar) {
                                                binding.statusBtn.text = it.status
                                                statusDetail = it.status
                                                binding.statusBtn.isEnabled = true

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
                        dataSetApproved.clear()
                        if (fullShowOrNot) {
                            result.data.instrumentData.forEach {
                                if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                    dataSetApproved.add(it)
                                }
                            }
                            instrumentAdapter.setListInstrument(dataSetApproved)
                        } else {
                            instrumentAdapter.setListInstrument(result.data.instrumentData)
                        }
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
    }

    companion object {
        const val ID = "gamelanId"
    }
}