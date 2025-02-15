package com.reviling.filamentandroid.ui.detailinstrument

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.reviling.filamentandroid.MainActivity
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.data.response.AudioInstrumenItem
import com.reviling.filamentandroid.data.response.GamelanDataItem
import com.reviling.filamentandroid.databinding.ActivityDetailInstrumentBinding
import com.reviling.filamentandroid.ui.BaseActivity
import com.reviling.filamentandroid.ui.CustomItemDecoration
import com.reviling.filamentandroid.ui.adapter.AudioInstrumentAdapter
import com.reviling.filamentandroid.ui.adapter.GamelanAdapter
import com.reviling.filamentandroid.ui.adapter.MaterialAdapter
import com.reviling.filamentandroid.ui.inputinstrument.InputInstrumentActivity
import com.reviling.filamentandroid.ui.login.LoginActivity
import com.reviling.filamentandroid.ui.seeallinstrument.DetailSeeAllInstrumentActivity
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailInstrumentActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailInstrumentBinding
    private lateinit var detailInstrumentViewModel: DetailInstrumentViewModel
    private lateinit var idInstrument: String
    private lateinit var adapter: GamelanAdapter
    private lateinit var materialAdapter: MaterialAdapter
    private lateinit var audioInstrumentAdapter: AudioInstrumentAdapter
    private var flagsBtnShow: Int = 0
    private var audioIdList: MutableList<String> = mutableListOf()
    private lateinit var isLoadingBar: ProgressBar
    private var statusDetail: String? = null
    private var statusInstrumentListName: MutableList<String> = mutableListOf()
    private var statusInstrumentListId: MutableList<String> = mutableListOf()
    private lateinit var isLoadingEditApproval: ProgressBar

    private var dataSetApproved: MutableList<GamelanDataItem> = mutableListOf()
    private var fullShowOrNot: Boolean = true

    private var userRole: String? = null
    private var userStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailInstrumentBinding.inflate(layoutInflater)
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
        idInstrument = intent.getStringExtra(ID).toString()

        lifecycleScope.launch {
            detailInstrumentViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailInstrumentActivity).create(
                    DetailInstrumentViewModel::class.java)
            }

            detailInstrumentViewModel.getSessionUser().observe(this@DetailInstrumentActivity) { user ->
                userRole = user.role
                userStatus = user.status

                if (user.role == "676190f1cc4fa7bc6c0bdbc4" || user.status == "67618f9ecc4fa7bc6c0bdbbb" || user.status == "67618fe3cc4fa7bc6c0bdbc1") {
                    fullShowOrNot = true
                } else {
                    fullShowOrNot = false
                }

                if (user.role == "676190fdcc4fa7bc6c0bdbc6" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                    binding.statusBtn.visibility = View.VISIBLE
                }

                if (user.role == "67619109cc4fa7bc6c0bdbc8" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                    binding.showEverythingCard.visibility = View.VISIBLE
                    binding.statusBtn.visibility = View.VISIBLE
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
                val builder = BottomSheetDialog(this@DetailInstrumentActivity)
                val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                editApproval.isEnabled = false
                var dataDeskripsi: String? = null
                var idStatus: String? = null

                builder.setContentView(dialog)
                builder.show()

                detailInstrumentViewModel.getNoteData(idInstrument).observe(this@DetailInstrumentActivity) { result ->
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
                    val dialogBuilder = AlertDialog.Builder(this@DetailInstrumentActivity)
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
                        val builderDialog = AlertDialog.Builder(this@DetailInstrumentActivity)
                        val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                        builderDialog.setView(dialogViewList)
                        builderDialog.create()
                        val dialogWindow = builderDialog.show()

                        val adapter = ArrayAdapter(this@DetailInstrumentActivity, android.R.layout.simple_list_item_single_choice, statusInstrumentListName)
                        listViewStatus.adapter = adapter
                        listViewStatus.choiceMode = ListView.CHOICE_MODE_SINGLE

                        if (selectedItem != null) {
                            listViewStatus.setItemChecked(selectedItem!!, true)
                        } else {
                            listViewStatus.setItemChecked(statusInstrumentListId.indexOf(idStatus), true)
                        }

                        listViewStatus.setOnItemClickListener { adapterView, view, i, l ->
                            namaApprovalSelected = statusInstrumentListName[i]
                            idApprovalSelected = statusInstrumentListId[i]
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
                            detailInstrumentViewModel.updateApprovalInstrument(idInstrument, approvalNote, statusId).observe(this@DetailInstrumentActivity) { update ->
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

            detailInstrumentViewModel.getDetailInstrumentById(idInstrument).observe(this@DetailInstrumentActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {

                            Log.d("ISIDATA", result.data.toString())

                            detailInstrumentViewModel.getListStatus().observe(this@DetailInstrumentActivity) { status ->
                                if (status != null) {
                                    when (status) {
                                        is Result.Loading -> {
                                            isLoading(true)
                                        }

                                        is Result.Success -> {
                                            val statusSanggar = result.data.instrumentData[0].status

                                            status.data.forEach {
                                                statusInstrumentListName.add(it.status)
                                                statusInstrumentListId.add(it.id)

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

                            binding.deletebtn.setOnClickListener {
                                val builder = AlertDialog.Builder(this@DetailInstrumentActivity)
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
                                    result.data.instrumentData[0].audioInstrumen.forEach {
                                        audioIdList.add(it.id)
                                    }

                                    Log.d("IsiDariAudioIDLIST", audioIdList.toString())

                                    if (audioIdList.isNotEmpty()) {
                                        detailInstrumentViewModel.deleteAudioByIdList(audioIdList).observe(this@DetailInstrumentActivity) { result ->
                                            if (result != null) {
                                                when (result) {
                                                    is Result.Loading -> {
                                                        isLoadingDialog(true)
                                                    }

                                                    is Result.Success -> {
                                                        showToast(result.data)
                                                        audioIdList = mutableListOf()
                                                        detailInstrumentViewModel.deleteInstrumentById(idInstrument).observe(this@DetailInstrumentActivity) { result ->
                                                            if (result != null) {
                                                                when (result) {
                                                                    is Result.Loading -> {
                                                                        isLoadingDialog(true)
                                                                    }

                                                                    is Result.Success -> {
                                                                        showToast(result.data)
                                                                        isLoadingDialog(false)
                                                                        val intent = Intent(this@DetailInstrumentActivity, DetailSeeAllInstrumentActivity::class.java)
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
                                    } else {
                                        detailInstrumentViewModel.deleteInstrumentById(idInstrument).observe(this@DetailInstrumentActivity) { result ->
                                            if (result != null) {
                                                when (result) {
                                                    is Result.Loading -> {
                                                        isLoadingDialog(true)
                                                    }

                                                    is Result.Success -> {
                                                        showToast(result.data)
                                                        isLoadingDialog(false)
                                                        val intent = Intent(this@DetailInstrumentActivity, DetailSeeAllInstrumentActivity::class.java)
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
                                }

                                buttonBatal.setOnClickListener { dialog.dismiss() }

                            }

                            binding.editdatacard.setOnClickListener {
                                val intent = Intent(this@DetailInstrumentActivity, InputInstrumentActivity::class.java)
                                intent.putExtra(InputInstrumentActivity.IDINSTRUMENT, idInstrument)
                                startActivity(intent)
                            }

                            binding.instrumentName.text = result.data.instrumentData[0].namaInstrument
                            binding.instrumenDesc.text = result.data.instrumentData[0].description
                            binding.instrumenDescFull.text = result.data.instrumentData[0].description
                            binding.fungsiDesc.text = result.data.instrumentData[0].fungsi
                            binding.fungsiDescFull.text = result.data.instrumentData[0].fungsi
                            Glide.with(this@DetailInstrumentActivity)
                                .load(result.data.instrumentData[0].imageInstrumen[0])
                                .transform(CenterCrop(), RoundedCorners(1000))
                                .into(binding.tridiView)

                            binding.buttonClickAll.setOnClickListener {
                                binding.instrumenDesc.visibility = View.GONE
                                binding.buttonClickAll.visibility = View.GONE
                                binding.buttonClickDown.visibility = View.VISIBLE
                                binding.instrumenDescFull.visibility = View.VISIBLE
                            }

                            binding.tridiView.setOnClickListener {
                                val intent = Intent(this@DetailInstrumentActivity, MainActivity::class.java)
                                intent.putExtra(MainActivity.URL, result.data.instrumentData[0].tridImage)
                                intent.putExtra(MainActivity.IDTRIDI, result.data.instrumentData[0].id)
                                if (result.data.instrumentData[0].imageInstrumen.isNotEmpty() && result.data.instrumentData[0].imageInstrumen.size > 1) {
                                    intent.putExtra(MainActivity.IMAGE, result.data.instrumentData[0].imageInstrumen[1])
                                }
                                startActivity(intent)
                            }

                            binding.buttonClickDown.setOnClickListener {
                                binding.instrumenDesc.visibility = View.VISIBLE
                                binding.buttonClickAll.visibility = View.VISIBLE
                                binding.buttonClickDown.visibility = View.GONE
                                binding.instrumenDescFull.visibility = View.GONE
                            }

                            binding.fungsiButtonClickAll.setOnClickListener {
                                binding.fungsiDesc.visibility = View.GONE
                                binding.fungsiButtonClickAll.visibility = View.GONE
                                binding.fungsiButtonClickDown.visibility = View.VISIBLE
                                binding.fungsiDescFull.visibility = View.VISIBLE
                            }

                            binding.fungsiButtonClickDown.setOnClickListener {
                                binding.fungsiDesc.visibility = View.VISIBLE
                                binding.fungsiButtonClickAll.visibility = View.VISIBLE
                                binding.fungsiButtonClickDown.visibility = View.GONE
                                binding.fungsiDescFull.visibility = View.GONE
                            }

                            materialAdapter =  MaterialAdapter(this@DetailInstrumentActivity)
                            binding.rvMaterial.layoutManager = LinearLayoutManager(this@DetailInstrumentActivity,  LinearLayoutManager.HORIZONTAL, false)
                            binding.rvMaterial.setHasFixedSize(true)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                            binding.rvMaterial.addItemDecoration(CustomItemDecoration(spacingInPixels, false))
                            binding.rvMaterial.adapter = materialAdapter
                            materialAdapter.setListMaterial(result.data.instrumentData[0].bahan)

                            audioInstrumentAdapter = AudioInstrumentAdapter(this@DetailInstrumentActivity)
                            binding.rvAudio.layoutManager = LinearLayoutManager(this@DetailInstrumentActivity,  LinearLayoutManager.VERTICAL, false)
                            binding.rvAudio.setHasFixedSize(true)
                            binding.rvAudio.adapter = audioInstrumentAdapter
                            audioInstrumentAdapter.setListAudioInstrument(result.data.instrumentData[0].audioInstrumen)
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

            detailInstrumentViewModel.geDetailGamelanByInstrumentId(idInstrument).observe(this@DetailInstrumentActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            Log.d("ISIDATA", result.data.toString())

                            adapter = GamelanAdapter(this@DetailInstrumentActivity)
                            binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@DetailInstrumentActivity,  LinearLayoutManager.VERTICAL, false)
                            binding.rvGamelanBaliHome.setHasFixedSize(true)
                            binding.rvGamelanBaliHome.adapter = adapter

                            dataSetApproved.clear()
                            if (fullShowOrNot) {
                                result.data.gamelanData.forEach {
                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                        dataSetApproved.add(it)
                                    }
                                }
                                adapter.setListGamelan(dataSetApproved)
                            } else {
                                adapter.setListGamelan(result.data.gamelanData)
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
            binding.statusBtn.isEnabled = false
            audioInstrumentAdapter.stopAudioPlayerInstrument()
            detailInstrumentViewModel.getDetailInstrumentById(idInstrument).observe(this@DetailInstrumentActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {

                            detailInstrumentViewModel.getListStatus().observe(this@DetailInstrumentActivity) { status ->
                                if (status != null) {
                                    when (status) {
                                        is Result.Loading -> {
                                            isLoading(true)
                                        }

                                        is Result.Success -> {
                                            val statusSanggar = result.data.instrumentData[0].status

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
                            binding.instrumentName.text = result.data.instrumentData[0].namaInstrument
                            binding.instrumenDesc.text = result.data.instrumentData[0].description
                            binding.instrumenDescFull.text = result.data.instrumentData[0].description
                            binding.fungsiDesc.text = result.data.instrumentData[0].fungsi
                            binding.fungsiDescFull.text = result.data.instrumentData[0].fungsi
                            Glide.with(this@DetailInstrumentActivity)
                                .load(result.data.instrumentData[0].imageInstrumen[0])
                                .transform(CenterCrop(), RoundedCorners(1000))
                                .into(binding.tridiView)

                            binding.buttonClickAll.setOnClickListener {
                                binding.instrumenDesc.visibility = View.GONE
                                binding.buttonClickAll.visibility = View.GONE
                                binding.buttonClickDown.visibility = View.VISIBLE
                                binding.instrumenDescFull.visibility = View.VISIBLE
                            }

                            binding.tridiView.setOnClickListener {
                                val intent = Intent(this@DetailInstrumentActivity, MainActivity::class.java)
                                intent.putExtra(MainActivity.URL, result.data.instrumentData[0].tridImage)
                                intent.putExtra(MainActivity.IDTRIDI, result.data.instrumentData[0].id)
                                if (result.data.instrumentData[0].imageInstrumen.isNotEmpty() && result.data.instrumentData[0].imageInstrumen.size > 1) {
                                    intent.putExtra(MainActivity.IMAGE, result.data.instrumentData[0].imageInstrumen[1])
                                }
                                startActivity(intent)
                            }

                            binding.buttonClickDown.setOnClickListener {
                                binding.instrumenDesc.visibility = View.VISIBLE
                                binding.buttonClickAll.visibility = View.VISIBLE
                                binding.buttonClickDown.visibility = View.GONE
                                binding.instrumenDescFull.visibility = View.GONE
                            }

                            binding.fungsiButtonClickAll.setOnClickListener {
                                binding.fungsiDesc.visibility = View.GONE
                                binding.fungsiButtonClickAll.visibility = View.GONE
                                binding.fungsiButtonClickDown.visibility = View.VISIBLE
                                binding.fungsiDescFull.visibility = View.VISIBLE
                            }

                            binding.fungsiButtonClickDown.setOnClickListener {
                                binding.fungsiDesc.visibility = View.VISIBLE
                                binding.fungsiButtonClickAll.visibility = View.VISIBLE
                                binding.fungsiButtonClickDown.visibility = View.GONE
                                binding.fungsiDescFull.visibility = View.GONE
                            }

                            materialAdapter =  MaterialAdapter(this@DetailInstrumentActivity)
                            binding.rvMaterial.layoutManager = LinearLayoutManager(this@DetailInstrumentActivity,  LinearLayoutManager.HORIZONTAL, false)
                            binding.rvMaterial.setHasFixedSize(true)
                            binding.rvMaterial.adapter = materialAdapter
                            materialAdapter.setListMaterial(result.data.instrumentData[0].bahan)

                            audioInstrumentAdapter = AudioInstrumentAdapter(this@DetailInstrumentActivity)
                            binding.rvAudio.layoutManager = LinearLayoutManager(this@DetailInstrumentActivity,  LinearLayoutManager.VERTICAL, false)
                            binding.rvAudio.setHasFixedSize(true)
                            binding.rvAudio.adapter = audioInstrumentAdapter
                            audioInstrumentAdapter.setListAudioInstrument(result.data.instrumentData[0].audioInstrumen)

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

            detailInstrumentViewModel.geDetailGamelanByInstrumentId(idInstrument).observe(this@DetailInstrumentActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            Log.d("ISIDATA", result.data.toString())

                            adapter = GamelanAdapter(this@DetailInstrumentActivity)
                            binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@DetailInstrumentActivity,  LinearLayoutManager.VERTICAL, false)
                            binding.rvGamelanBaliHome.setHasFixedSize(true)
                            binding.rvGamelanBaliHome.adapter = adapter

                            dataSetApproved.clear()
                            if (fullShowOrNot) {
                                result.data.gamelanData.forEach {
                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                        dataSetApproved.add(it)
                                    }
                                }
                                adapter.setListGamelan(dataSetApproved)
                            } else {
                                adapter.setListGamelan(result.data.gamelanData)
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
            binding.scrolling.isRefreshing = false
        }
        supportActionBar?.hide()
    }

    override fun repeatFunction() {
        lifecycleScope.launch {
            detailInstrumentViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailInstrumentActivity).create(
                    DetailInstrumentViewModel::class.java
                )
            }

            detailInstrumentViewModel.getSessionUser()
                .observe(this@DetailInstrumentActivity) { user ->
                    if (user.isLogin) {
                        detailInstrumentViewModel.getUserDatabyId(user.user_id)
                            .observe(this@DetailInstrumentActivity) { save ->
                                if (save != null) {
                                    when (save) {
                                        is Result.Loading -> {}

                                        is Result.Success -> {
                                            if (save.data.status != user.status) {
                                                detailInstrumentViewModel.saveSession(
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
                                                    binding.showEverythingCard.visibility =
                                                        View.VISIBLE
                                                    binding.statusBtn.visibility = View.VISIBLE
                                                } else {
                                                    binding.showEverythingCard.visibility =
                                                        View.GONE
                                                    binding.statusBtn.visibility = View.GONE
                                                }
                                            }
                                        }

                                        is Result.Error -> {
                                            showToast(save.error)
                                            Log.d("IsiDariSaveError", save.error)
                                            if (save.error == "Sorry, There is no user with this name ${user.user_id}" || save.error == "Sorry, Token has expired, please login again!" || save.error == "Sorry, Token Invalid!") {
                                                detailInstrumentViewModel.logoutUser()
                                                val intentMain = Intent(this@DetailInstrumentActivity, LoginActivity::class.java)
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
        detailInstrumentViewModel.getDetailInstrumentById(idInstrument).observe(this@DetailInstrumentActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {

                        detailInstrumentViewModel.getListStatus().observe(this@DetailInstrumentActivity) { status ->
                            if (status != null) {
                                when (status) {
                                    is Result.Loading -> {
                                        isLoading(true)
                                    }

                                    is Result.Success -> {
                                        val statusSanggar = result.data.instrumentData[0].status

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
                        binding.instrumentName.text = result.data.instrumentData[0].namaInstrument
                        binding.instrumenDesc.text = result.data.instrumentData[0].description
                        binding.instrumenDescFull.text = result.data.instrumentData[0].description
                        binding.fungsiDesc.text = result.data.instrumentData[0].fungsi
                        binding.fungsiDescFull.text = result.data.instrumentData[0].fungsi
                        Glide.with(this@DetailInstrumentActivity)
                            .load(result.data.instrumentData[0].imageInstrumen[0])
                            .transform(CenterCrop(), RoundedCorners(1000))
                            .into(binding.tridiView)

                        binding.buttonClickAll.setOnClickListener {
                            binding.instrumenDesc.visibility = View.GONE
                            binding.buttonClickAll.visibility = View.GONE
                            binding.buttonClickDown.visibility = View.VISIBLE
                            binding.instrumenDescFull.visibility = View.VISIBLE
                        }

                        binding.tridiView.setOnClickListener {
                            val intent = Intent(this@DetailInstrumentActivity, MainActivity::class.java)
                            intent.putExtra(MainActivity.URL, result.data.instrumentData[0].tridImage)
                            intent.putExtra(MainActivity.IDTRIDI, result.data.instrumentData[0].id)
                            if (result.data.instrumentData[0].imageInstrumen.isNotEmpty() && result.data.instrumentData[0].imageInstrumen.size > 1) {
                                intent.putExtra(MainActivity.IMAGE, result.data.instrumentData[0].imageInstrumen[1])
                            }
                            startActivity(intent)
                        }

                        binding.buttonClickDown.setOnClickListener {
                            binding.instrumenDesc.visibility = View.VISIBLE
                            binding.buttonClickAll.visibility = View.VISIBLE
                            binding.buttonClickDown.visibility = View.GONE
                            binding.instrumenDescFull.visibility = View.GONE
                        }

                        binding.fungsiButtonClickAll.setOnClickListener {
                            binding.fungsiDesc.visibility = View.GONE
                            binding.fungsiButtonClickAll.visibility = View.GONE
                            binding.fungsiButtonClickDown.visibility = View.VISIBLE
                            binding.fungsiDescFull.visibility = View.VISIBLE
                        }

                        binding.fungsiButtonClickDown.setOnClickListener {
                            binding.fungsiDesc.visibility = View.VISIBLE
                            binding.fungsiButtonClickAll.visibility = View.VISIBLE
                            binding.fungsiButtonClickDown.visibility = View.GONE
                            binding.fungsiDescFull.visibility = View.GONE
                        }

                        materialAdapter =  MaterialAdapter(this@DetailInstrumentActivity)
                        binding.rvMaterial.layoutManager = LinearLayoutManager(this@DetailInstrumentActivity,  LinearLayoutManager.HORIZONTAL, false)
                        binding.rvMaterial.setHasFixedSize(true)
                        binding.rvMaterial.adapter = materialAdapter
                        materialAdapter.setListMaterial(result.data.instrumentData[0].bahan)

                        audioInstrumentAdapter = AudioInstrumentAdapter(this@DetailInstrumentActivity)
                        binding.rvAudio.layoutManager = LinearLayoutManager(this@DetailInstrumentActivity,  LinearLayoutManager.VERTICAL, false)
                        binding.rvAudio.setHasFixedSize(true)
                        binding.rvAudio.adapter = audioInstrumentAdapter
                        audioInstrumentAdapter.setListAudioInstrument(result.data.instrumentData[0].audioInstrumen)

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

    companion object {
        const val ID = "instrumentId"
    }
}