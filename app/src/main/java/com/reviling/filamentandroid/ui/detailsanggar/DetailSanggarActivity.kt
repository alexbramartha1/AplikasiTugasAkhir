package com.reviling.filamentandroid.ui.detailsanggar

import android.app.DownloadManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.response.GamelanDataItem
import com.reviling.filamentandroid.databinding.ActivityDetailSanggarBinding
import com.reviling.filamentandroid.ui.adapter.GamelanAdapter
import com.reviling.filamentandroid.ui.inputsanggar.InputDataSanggarActivity
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailSanggarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSanggarBinding
    private lateinit var sanggarId: String
    private var flags: Boolean = true
    private lateinit var detailSanggarViewModel: DetailSanggarViewModel
    private var flagsEdit: Boolean = false
    private var idUser: String = "idUser"
    private lateinit var gamelanAdapter: GamelanAdapter
    private var idListGamelan: MutableList<String> = mutableListOf()
    private lateinit var manager: DownloadManager
    private var statusDetail: String? = null
    private lateinit var isLoadingBar: ProgressBar
    private var statusSanggarListName: MutableList<String> = mutableListOf()
    private var statusSanggarListId: MutableList<String> = mutableListOf()
    private lateinit var isLoadingEditApproval: ProgressBar
    private var userStatus: String? = null
    private var userRole: String? = null

    private var dataSetApproved: MutableList<GamelanDataItem> = mutableListOf()
    private var fullShowOrNot: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSanggarBinding.inflate(layoutInflater)
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
        sanggarId = intent.getStringExtra(ID).toString()

        lifecycleScope.launch {
            detailSanggarViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailSanggarActivity)
                    .create(DetailSanggarViewModel::class.java)
            }

            detailSanggarViewModel.getSessionUser().observe(this@DetailSanggarActivity) { user ->
                idUser = user.user_id
                userRole = user.role
                userStatus = user.status

                if (user.role == "676190f1cc4fa7bc6c0bdbc4" || user.status == "67618f9ecc4fa7bc6c0bdbbb" || user.status == "67618fe3cc4fa7bc6c0bdbc1") {
                    fullShowOrNot = true
                } else {
                    fullShowOrNot = false
                }
            }

            binding.statusBtn.setOnClickListener {
                var selectedItem: Int? = null
                var idApprovalSelected: String? = null
                var namaApprovalSelected: String? = null

                val dialog = layoutInflater.inflate(R.layout.status_fragment, null)
                val builder = BottomSheetDialog(this@DetailSanggarActivity)
                val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                editApproval.isEnabled = false
                var dataDeskripsi: String? = null
                var idStatus: String? = null

                builder.setContentView(dialog)
                builder.show()

                detailSanggarViewModel.getNoteData(sanggarId).observe(this@DetailSanggarActivity) { result ->
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
                    val dialogBuilder = AlertDialog.Builder(this@DetailSanggarActivity)
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
                        val builderDialog = AlertDialog.Builder(this@DetailSanggarActivity)
                        val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                        builderDialog.setView(dialogViewList)
                        builderDialog.create()
                        val dialogWindow = builderDialog.show()

                        val adapter = ArrayAdapter(this@DetailSanggarActivity, android.R.layout.simple_list_item_single_choice, statusSanggarListName)
                        listViewStatus.adapter = adapter
                        listViewStatus.choiceMode = ListView.CHOICE_MODE_SINGLE

                        if (selectedItem != null) {
                            listViewStatus.setItemChecked(selectedItem!!, true)
                        } else {
                            listViewStatus.setItemChecked(statusSanggarListId.indexOf(idStatus), true)
                        }

                        listViewStatus.setOnItemClickListener { adapterView, view, i, l ->
                            namaApprovalSelected = statusSanggarListName[i]
                            idApprovalSelected = statusSanggarListId[i]
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
                            detailSanggarViewModel.updateApprovalSanggar(sanggarId, approvalNote, statusId).observe(this@DetailSanggarActivity) { update ->
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

            detailSanggarViewModel.getDetailSanggarById(sanggarId).observe(this@DetailSanggarActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            if (idUser != result.data.sanggarData[0].idCreator && userRole != "676190fdcc4fa7bc6c0bdbc6") {
                                binding.statusBtn.visibility = View.GONE
                            } else {
                                binding.statusBtn.visibility = View.VISIBLE
                                detailSanggarViewModel.getListStatus().observe(this@DetailSanggarActivity) { status ->
                                    if (status != null) {
                                        when (status) {
                                            is Result.Loading -> {
                                                isLoading(true)
                                            }

                                            is Result.Success -> {
                                                val statusSanggar = result.data.sanggarData[0].status

                                                status.data.forEach {
                                                    statusSanggarListName.add(it.status)
                                                    statusSanggarListId.add(it.id)

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
                            }

                            Log.d("ISIDATA", result.data.toString())

                            binding.downloadbutton.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.data.sanggarData[0].supportDocument))
                                startActivity(intent)
                            }

                            binding.sanggarName.text = result.data.sanggarData[0].namaSanggar
                            binding.sanggarDesc.text = result.data.sanggarData[0].deskripsi
                            binding.sanggarDescFull.text = result.data.sanggarData[0].deskripsi
                            binding.alamatDesc.text = result.data.sanggarData[0].alamatLengkap
                            binding.phoneNumber.text = result.data.sanggarData[0].noTelepon
                            Glide.with(this@DetailSanggarActivity)
                                .load(result.data.sanggarData[0].image)
                                .transform(CenterCrop())
                                .into(binding.sanggarImage)

                            if (idUser == result.data.sanggarData[0].idCreator) {
                                flagsEdit = true
                            } else {
                                flagsEdit = false
                            }

                            result.data.sanggarData[0].gamelanId.forEach {
                                idListGamelan.add(it.replace("\"", ""))
                            }

                            if (idListGamelan.isNotEmpty()) {
                                detailSanggarViewModel.getGamelanByIdList(idListGamelan).observe(this@DetailSanggarActivity) { resultGamelan ->
                                    if (resultGamelan != null) {
                                        when (resultGamelan) {
                                            is Result.Loading -> {
                                                isLoading(true)
                                            }

                                            is Result.Success -> {
                                                gamelanAdapter = GamelanAdapter(this@DetailSanggarActivity)
                                                binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@DetailSanggarActivity,  LinearLayoutManager.VERTICAL, false)
                                                binding.rvGamelanBaliHome.setHasFixedSize(true)
                                                binding.rvGamelanBaliHome.adapter = gamelanAdapter

                                                dataSetApproved.clear()
                                                if (fullShowOrNot) {
                                                    resultGamelan.data.forEach {
                                                        if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                            dataSetApproved.add(it)
                                                        }
                                                    }
                                                    gamelanAdapter.setListGamelan(dataSetApproved)
                                                } else {
                                                    gamelanAdapter.setListGamelan(resultGamelan.data)
                                                }

                                                showToast("Data Loaded")
                                                isLoading(false)
                                            }

                                            is Result.Error -> {
                                                showToast(resultGamelan.error)
                                                isLoading(false)
                                            }
                                        }
                                    }
                                }
                            }

                            binding.gotomaps.setOnClickListener {
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${result.data.sanggarData[0].namaSanggar}"))
                                startActivity(mapIntent)
                            }

                            binding.chats.setOnClickListener {
                                val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${result.data.sanggarData[0].noTelepon}"))
                                startActivity(whatsappIntent)
                            }

                            binding.calls.setOnClickListener {
                                val phoneNumber = "tel:${result.data.sanggarData[0].noTelepon}"
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse(phoneNumber)
                                }
                                startActivity(dialIntent)
                            }

                            binding.editdatacard.setOnClickListener {
                                val intent = Intent(this@DetailSanggarActivity, InputDataSanggarActivity::class.java)
                                intent.putExtra(InputDataSanggarActivity.ID, result.data.sanggarData[0].id)
                                startActivity(intent)
                            }

                            binding.deletebtn.setOnClickListener {
                                val builder = AlertDialog.Builder(this@DetailSanggarActivity)
                                val inflater = layoutInflater
                                val dialogView = inflater.inflate(R.layout.fragment_choose, null)

                                val buttonDelete = dialogView.findViewById<Button>(R.id.deletebtnsanggar)
                                val buttonBatal = dialogView.findViewById<Button>(R.id.batalbtn)
                                val loadingBar = dialogView.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                                builder.setView(dialogView)
                                val dialog = builder.create()
                                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.show()

                                buttonDelete.setOnClickListener {
                                    detailSanggarViewModel.deleteSanggarData(sanggarId).observe(this@DetailSanggarActivity) { result ->
                                        if (result != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    loadingBar.visibility = View.VISIBLE
                                                }

                                                is Result.Success -> {
                                                    showToast(result.data)
                                                    loadingBar.visibility = View.GONE
                                                    dialog.dismiss()
                                                    val intent = Intent(this@DetailSanggarActivity, SeeAllSanggarActivity::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                    startActivity(intent)
                                                    finish()
                                                }

                                                is Result.Error -> {
                                                    showToast(result.error)
                                                    loadingBar.visibility = View.GONE
                                                }
                                            }
                                        }
                                    }
                                }

                                buttonBatal.setOnClickListener { dialog.dismiss() }
                            }

                            binding.buttonClickAll.setOnClickListener {
                                binding.sanggarDesc.visibility = View.GONE
                                binding.buttonClickAll.visibility = View.GONE
                                binding.buttonClickDown.visibility = View.VISIBLE
                                binding.sanggarDescFull.visibility = View.VISIBLE
                            }

                            binding.buttonClickDown.setOnClickListener {
                                binding.sanggarDesc.visibility = View.VISIBLE
                                binding.buttonClickAll.visibility = View.VISIBLE
                                binding.buttonClickDown.visibility = View.GONE
                                binding.sanggarDescFull.visibility = View.GONE
                            }
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

            detailSanggarViewModel.getSessionUser().observe(this@DetailSanggarActivity) { user ->
                idUser = user.user_id
            }

            detailSanggarViewModel.getDetailSanggarById(sanggarId).observe(this@DetailSanggarActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)
                            Log.d("ISIDATA", result.data.toString())

                            if (idUser != result.data.sanggarData[0].idCreator && userRole != "676190fdcc4fa7bc6c0bdbc6") {
                                binding.statusBtn.visibility = View.GONE
                            } else {
                                binding.statusBtn.visibility = View.VISIBLE
                                detailSanggarViewModel.getListStatus().observe(this@DetailSanggarActivity) { status ->
                                    if (status != null) {
                                        when (status) {
                                            is Result.Loading -> {
                                                isLoading(true)
                                            }

                                            is Result.Success -> {
                                                val statusSanggar = result.data.sanggarData[0].status

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
                            }

                            binding.sanggarName.text = result.data.sanggarData[0].namaSanggar
                            binding.sanggarDesc.text = result.data.sanggarData[0].deskripsi
                            binding.sanggarDescFull.text = result.data.sanggarData[0].deskripsi
                            binding.alamatDesc.text = result.data.sanggarData[0].alamatLengkap
                            binding.phoneNumber.text = result.data.sanggarData[0].noTelepon
                            Glide.with(this@DetailSanggarActivity)
                                .load(result.data.sanggarData[0].image)
                                .transform(CenterCrop())
                                .into(binding.sanggarImage)

                            if (idUser == result.data.sanggarData[0].idCreator) {
                                flagsEdit = true
                            } else {
                                flagsEdit = false
                            }
                            idListGamelan.clear()
                            idListGamelan = result.data.sanggarData[0].gamelanId

                            if (idListGamelan.isNotEmpty()) {
                                detailSanggarViewModel.getGamelanByIdList(idListGamelan).observe(this@DetailSanggarActivity) { resultGamelan ->
                                    if (resultGamelan != null) {
                                        when (resultGamelan) {
                                            is Result.Loading -> {
                                                isLoading(true)
                                            }

                                            is Result.Success -> {
                                                gamelanAdapter = GamelanAdapter(this@DetailSanggarActivity)
                                                binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@DetailSanggarActivity,  LinearLayoutManager.VERTICAL, false)
                                                binding.rvGamelanBaliHome.setHasFixedSize(true)
                                                binding.rvGamelanBaliHome.adapter = gamelanAdapter

                                                dataSetApproved.clear()
                                                if (fullShowOrNot) {
                                                    resultGamelan.data.forEach {
                                                        if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                            dataSetApproved.add(it)
                                                        }
                                                    }
                                                    gamelanAdapter.setListGamelan(dataSetApproved)
                                                } else {
                                                    gamelanAdapter.setListGamelan(resultGamelan.data)
                                                }

                                                showToast("Data Loaded")
                                                isLoading(false)
                                            }

                                            is Result.Error -> {
                                                showToast(resultGamelan.error)
                                                isLoading(false)
                                            }
                                        }
                                    }
                                }
                            }

                            binding.downloadbutton.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.data.sanggarData[0].supportDocument))
                                startActivity(intent)
                            }

                            binding.gotomaps.setOnClickListener {
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${result.data.sanggarData[0].namaSanggar}"))
                                startActivity(mapIntent)
                            }

                            binding.chats.setOnClickListener {
                                val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${result.data.sanggarData[0].noTelepon}"))
                                startActivity(whatsappIntent)
                            }

                            binding.calls.setOnClickListener {
                                val phoneNumber = "tel:${result.data.sanggarData[0].noTelepon}"
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse(phoneNumber)
                                }
                                startActivity(dialIntent)
                            }

                            binding.editdatacard.setOnClickListener {
                                val intent = Intent(this@DetailSanggarActivity, InputDataSanggarActivity::class.java)
                                intent.putExtra(InputDataSanggarActivity.ID, result.data.sanggarData[0].id)
                                startActivity(intent)
                            }

                            binding.deletebtn.setOnClickListener {
                                val builder = AlertDialog.Builder(this@DetailSanggarActivity)
                                val inflater = layoutInflater
                                val dialogView = inflater.inflate(R.layout.fragment_choose, null)

                                val buttonDelete = dialogView.findViewById<Button>(R.id.deletebtnsanggar)
                                val buttonBatal = dialogView.findViewById<Button>(R.id.batalbtn)
                                val loadingBar = dialogView.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                                builder.setView(dialogView)
                                val dialog = builder.create()
                                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.show()

                                buttonDelete.setOnClickListener {
                                    detailSanggarViewModel.deleteSanggarData(sanggarId).observe(this@DetailSanggarActivity) { result ->
                                        if (result != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    loadingBar.visibility = View.VISIBLE
                                                }

                                                is Result.Success -> {
                                                    showToast(result.data)
                                                    loadingBar.visibility = View.GONE
                                                    dialog.dismiss()

                                                    val intent = Intent(this@DetailSanggarActivity, SeeAllSanggarActivity::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                    startActivity(intent)
                                                    finish()
                                                }

                                                is Result.Error -> {
                                                    showToast(result.error)
                                                    loadingBar.visibility = View.GONE
                                                }
                                            }
                                        }
                                    }
                                }

                                buttonBatal.setOnClickListener { dialog.dismiss() }
                            }

                            binding.buttonClickAll.setOnClickListener {
                                binding.sanggarDesc.visibility = View.GONE
                                binding.buttonClickAll.visibility = View.GONE
                                binding.buttonClickDown.visibility = View.VISIBLE
                                binding.sanggarDescFull.visibility = View.VISIBLE
                            }

                            binding.buttonClickDown.setOnClickListener {
                                binding.sanggarDesc.visibility = View.VISIBLE
                                binding.buttonClickAll.visibility = View.VISIBLE
                                binding.buttonClickDown.visibility = View.GONE
                                binding.sanggarDescFull.visibility = View.GONE
                            }
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

        binding.showEverything.setOnClickListener {
            if (flags) {
                binding.calls.visibility = View.VISIBLE
                binding.chats.visibility = View.VISIBLE
                binding.gotomaps.visibility = View.VISIBLE
                binding.downloadbutton.visibility = View.VISIBLE

                if (flagsEdit){
                    binding.editdatacard.visibility = View.VISIBLE
                    binding.deletebtn.visibility = View.VISIBLE
                } else {
                    binding.editdatacard.visibility = View.GONE
                    binding.deletebtn.visibility = View.GONE
                }

                flags = false
            } else {
                binding.calls.visibility = View.GONE
                binding.chats.visibility = View.GONE
                binding.gotomaps.visibility = View.GONE
                binding.downloadbutton.visibility = View.GONE

                if (flagsEdit){
                    binding.editdatacard.visibility = View.GONE
                    binding.deletebtn.visibility = View.GONE
                } else {
                    binding.editdatacard.visibility = View.GONE
                    binding.deletebtn.visibility = View.GONE
                }

                flags = true
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

    private fun updateStateData() {
        detailSanggarViewModel.getDetailSanggarById(sanggarId).observe(this@DetailSanggarActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)
                        Log.d("ISIDATA", result.data.toString())

                        if (idUser != result.data.sanggarData[0].idCreator && userRole != "676190fdcc4fa7bc6c0bdbc6") {
                            binding.statusBtn.visibility = View.GONE
                        } else {
                            binding.statusBtn.visibility = View.VISIBLE
                            detailSanggarViewModel.getListStatus().observe(this@DetailSanggarActivity) { status ->
                                if (status != null) {
                                    when (status) {
                                        is Result.Loading -> {
                                            isLoading(true)
                                        }

                                        is Result.Success -> {
                                            val statusSanggar = result.data.sanggarData[0].status

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
                        }

                        binding.sanggarName.text = result.data.sanggarData[0].namaSanggar
                        binding.sanggarDesc.text = result.data.sanggarData[0].deskripsi
                        binding.sanggarDescFull.text = result.data.sanggarData[0].deskripsi
                        binding.alamatDesc.text = result.data.sanggarData[0].alamatLengkap
                        binding.phoneNumber.text = result.data.sanggarData[0].noTelepon
                        Glide.with(this@DetailSanggarActivity)
                            .load(result.data.sanggarData[0].image)
                            .transform(CenterCrop())
                            .into(binding.sanggarImage)

                        if (idUser == result.data.sanggarData[0].idCreator) {
                            flagsEdit = true
                        } else {
                            flagsEdit = false
                        }

                        idListGamelan.clear()
                        idListGamelan = result.data.sanggarData[0].gamelanId

                        if (idListGamelan.isNotEmpty()) {
                            detailSanggarViewModel.getGamelanByIdList(idListGamelan).observe(this@DetailSanggarActivity) { resultGamelan ->
                                if (resultGamelan != null) {
                                    when (resultGamelan) {
                                        is Result.Loading -> {
                                            isLoading(true)
                                        }

                                        is Result.Success -> {
                                            gamelanAdapter = GamelanAdapter(this@DetailSanggarActivity)
                                            binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@DetailSanggarActivity,  LinearLayoutManager.VERTICAL, false)
                                            binding.rvGamelanBaliHome.setHasFixedSize(true)
                                            binding.rvGamelanBaliHome.adapter = gamelanAdapter

                                            dataSetApproved.clear()
                                            if (fullShowOrNot) {
                                                resultGamelan.data.forEach {
                                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                        dataSetApproved.add(it)
                                                    }
                                                }
                                                gamelanAdapter.setListGamelan(dataSetApproved)
                                            } else {
                                                gamelanAdapter.setListGamelan(resultGamelan.data)
                                            }

                                            showToast("Data Loaded")
                                            isLoading(false)
                                        }

                                        is Result.Error -> {
                                            showToast(resultGamelan.error)
                                            isLoading(false)
                                        }
                                    }
                                }
                            }
                        }

                        binding.downloadbutton.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.data.sanggarData[0].supportDocument))
                            startActivity(intent)
                        }

                        binding.gotomaps.setOnClickListener {
                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${result.data.sanggarData[0].namaSanggar}"))
                            startActivity(mapIntent)
                        }

                        binding.chats.setOnClickListener {
                            val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${result.data.sanggarData[0].noTelepon}"))
                            startActivity(whatsappIntent)
                        }

                        binding.calls.setOnClickListener {
                            val phoneNumber = "tel:${result.data.sanggarData[0].noTelepon}"
                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse(phoneNumber)
                            }
                            startActivity(dialIntent)
                        }

                        binding.editdatacard.setOnClickListener {
                            val intent = Intent(this@DetailSanggarActivity, InputDataSanggarActivity::class.java)
                            intent.putExtra(InputDataSanggarActivity.ID, result.data.sanggarData[0].id)
                            startActivity(intent)
                        }

                        binding.deletebtn.setOnClickListener {
                            val builder = AlertDialog.Builder(this@DetailSanggarActivity)
                            val inflater = layoutInflater
                            val dialogView = inflater.inflate(R.layout.fragment_choose, null)

                            val buttonDelete = dialogView.findViewById<Button>(R.id.deletebtnsanggar)
                            val buttonBatal = dialogView.findViewById<Button>(R.id.batalbtn)
                            val loadingBar = dialogView.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                            builder.setView(dialogView)
                            val dialog = builder.create()
                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialog.show()

                            buttonDelete.setOnClickListener {
                                detailSanggarViewModel.deleteSanggarData(sanggarId).observe(this@DetailSanggarActivity) { result ->
                                    if (result != null) {
                                        when (result) {
                                            is Result.Loading -> {
                                                loadingBar.visibility = View.VISIBLE
                                            }

                                            is Result.Success -> {
                                                showToast(result.data)
                                                loadingBar.visibility = View.GONE
                                                dialog.dismiss()

                                                val intent = Intent(this@DetailSanggarActivity, SeeAllSanggarActivity::class.java)
                                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                startActivity(intent)
                                                finish()
                                            }

                                            is Result.Error -> {
                                                showToast(result.error)
                                                loadingBar.visibility = View.GONE
                                            }
                                        }
                                    }
                                }
                            }

                            buttonBatal.setOnClickListener { dialog.dismiss() }
                        }

                        binding.buttonClickAll.setOnClickListener {
                            binding.sanggarDesc.visibility = View.GONE
                            binding.buttonClickAll.visibility = View.GONE
                            binding.buttonClickDown.visibility = View.VISIBLE
                            binding.sanggarDescFull.visibility = View.VISIBLE
                        }

                        binding.buttonClickDown.setOnClickListener {
                            binding.sanggarDesc.visibility = View.VISIBLE
                            binding.buttonClickAll.visibility = View.VISIBLE
                            binding.buttonClickDown.visibility = View.GONE
                            binding.sanggarDescFull.visibility = View.GONE
                        }
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
        const val ID = "sanggarId"
    }
}