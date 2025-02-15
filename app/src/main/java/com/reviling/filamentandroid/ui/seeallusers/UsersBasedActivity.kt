package com.reviling.filamentandroid.ui.seeallusers

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.db.williamchart.ExperimentalFeature
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.databinding.ActivityUsersBasedBinding
import com.reviling.filamentandroid.ui.CustomItemDecorationVerticalUpperBottom
import com.reviling.filamentandroid.ui.adapter.AllUsersAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class UsersBasedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBasedBinding
    private lateinit var allUsersViewModel: AllUsersViewModel

    private lateinit var usersAdapter: AllUsersAdapter

    private var selectedNamaStatus: MutableList<String> = mutableListOf()
    private var selectedIdStatus: MutableList<String> = mutableListOf()
    private var statusNamaList: MutableList<String> = mutableListOf()
    private var statusIdList: MutableList<String> = mutableListOf()
    private var selectedItemStatus: MutableList<Int> = mutableListOf()

    private var selectedNamaRole: MutableList<String> = mutableListOf()
    private var selectedIdRole: MutableList<String> = mutableListOf()
    private var roleNamaList: MutableList<String> = mutableListOf()
    private var roleIdList: MutableList<String> = mutableListOf()
    private var selectedItemRole: MutableList<Int> = mutableListOf()

    private lateinit var isLoadingEditApproval: ProgressBar
    private lateinit var isLoadingBar: ProgressBar
    private var statusName: String? = null

    private var selectedYear: String? = null

    private var barSet = mutableMapOf(
        "JAN" to 0F,
        "FEB" to 0F,
        "MAR" to 0F,
        "APR" to 0F,
        "MAY" to 0F,
        "JUN" to 0F,
        "JUL" to 0F,
        "AUG" to 0F,
        "SEP" to 0F,
        "OCT" to 0F,
        "NOV" to 0F,
        "DES" to 0F
    )

    private val listOfYear = listOf(
        "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025"
    )

    private val animationDuration = 1000L

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBasedBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            allUsersViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@UsersBasedActivity)
                    .create(AllUsersViewModel::class.java)
            }

            getAllRoleList()
            getAllStatusList()

            val totalAdmin: MutableList<String> = mutableListOf()
            val totalPengguna: MutableList<String> = mutableListOf()
            val totalAhli: MutableList<String> = mutableListOf()

            val totalAdminByDate: MutableList<String> = mutableListOf()
            val totalPenggunaByDate: MutableList<String> = mutableListOf()
            val totalAhliByDate: MutableList<String> = mutableListOf()

            val totalJanUsersCount: MutableList<String> = mutableListOf()
            val totalFebUsersCount: MutableList<String> = mutableListOf()
            val totalMarUsersCount: MutableList<String> = mutableListOf()
            val totalAprUsersCount: MutableList<String> = mutableListOf()
            val totalMayUsersCount: MutableList<String> = mutableListOf()
            val totalJunUsersCount: MutableList<String> = mutableListOf()
            val totalJulUsersCount: MutableList<String> = mutableListOf()
            val totalAugUsersCount: MutableList<String> = mutableListOf()
            val totalSepUsersCount: MutableList<String> = mutableListOf()
            val totalOctUsersCount: MutableList<String> = mutableListOf()
            val totalNovUsersCount: MutableList<String> = mutableListOf()
            val totalDecUsersCount: MutableList<String> = mutableListOf()

            allUsersViewModel.getAllUsersData().observe(this@UsersBasedActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            totalAdmin.clear()
                            totalPengguna.clear()
                            totalAhli.clear()

                            val currentDate = LocalDate.now()
                            var selectedItem: Int
                            selectedItem = if (selectedYear == null) {
                                listOfYear.indexOf(currentDate.year.toString())
                            } else {
                                listOfYear.indexOf(selectedYear)
                            }

                            val yearNow = currentDate.year.toString()

                            result.data.dataUser.forEach {
                                if (it.roleId == "676190fdcc4fa7bc6c0bdbc6") {
                                    totalAdmin.add(it.roleId)
                                } else if (it.roleId == "676190f1cc4fa7bc6c0bdbc4") {
                                    totalPengguna.add(it.roleId)
                                } else if (it.roleId == "67619109cc4fa7bc6c0bdbc8") {
                                    totalAhli.add(it.roleId)
                                }

                                val date = LocalDateTime.parse(it.createdAt)
                                val month = date.month
                                val year = date.year.toString()

                                if (year == yearNow) {
                                    if (month.toString() == "JANUARY") {
                                        totalJanUsersCount.add(it.id)
                                        barSet["JAN"] = totalJanUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "FEBRUARY") {
                                        totalFebUsersCount.add(it.id)
                                        barSet["FEB"] = totalFebUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "MARCH") {
                                        totalMarUsersCount.add(it.id)
                                        barSet["MAR"] = totalMarUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "APRIL") {
                                        totalAprUsersCount.add(it.id)
                                        barSet["APR"] = totalAprUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "MAY") {
                                        totalMayUsersCount.add(it.id)
                                        barSet["MAY"] = totalMayUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "JUNE") {
                                        totalJunUsersCount.add(it.id)
                                        barSet["JUN"] = totalJunUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "JULY") {
                                        totalJulUsersCount.add(it.id)
                                        barSet["JUL"] = totalJulUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "AUGUST") {
                                        totalAugUsersCount.add(it.id)
                                        barSet["AUG"] = totalAugUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "SEPTEMBER") {
                                        totalSepUsersCount.add(it.id)
                                        barSet["SEP"] = totalSepUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "OCTOBER") {
                                        totalOctUsersCount.add(it.id)
                                        barSet["OCT"] = totalOctUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "NOVEMBER") {
                                        totalNovUsersCount.add(it.id)
                                        barSet["NOV"] = totalNovUsersCount.count().toFloat()
                                    }

                                    if (month.toString() == "DECEMBER") {
                                        totalDecUsersCount.add(it.id)
                                        barSet["DEC"] = totalDecUsersCount.count().toFloat()
                                    }
                                }
                            }

                            binding.yearChoose.text = currentDate.year.toString()
                            binding.yearChoose.setOnClickListener {
                                val dialogViewList = layoutInflater.inflate(R.layout.status_list_layout, null)
                                val builderDialog = AlertDialog.Builder(this@UsersBasedActivity)
                                val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                                builderDialog.setView(dialogViewList)
                                builderDialog.create()
                                val dialogWindow = builderDialog.show()

                                val adapter = ArrayAdapter(this@UsersBasedActivity, android.R.layout.simple_list_item_single_choice, listOfYear)
                                listViewStatus.adapter = adapter
                                listViewStatus.choiceMode = ListView.CHOICE_MODE_SINGLE

                                listViewStatus.setItemChecked(selectedItem, true)

                                listViewStatus.setOnItemClickListener { adapterView, view, i, l ->
                                    selectedItem = i
                                    selectedYear = listOfYear[i]
                                    binding.yearChoose.text = listOfYear[i]
                                    dialogWindow.dismiss()

                                    barSet.replaceAll {_, _ -> 0F}

                                    totalJanUsersCount.clear()
                                    totalFebUsersCount.clear()
                                    totalMarUsersCount.clear()
                                    totalAprUsersCount.clear()
                                    totalMayUsersCount.clear()
                                    totalJunUsersCount.clear()
                                    totalJulUsersCount.clear()
                                    totalAugUsersCount.clear()
                                    totalSepUsersCount.clear()
                                    totalOctUsersCount.clear()
                                    totalNovUsersCount.clear()
                                    totalDecUsersCount.clear()

                                    totalAhliByDate.clear()
                                    totalPenggunaByDate.clear()
                                    totalAdminByDate.clear()

                                    result.data.dataUser.forEach {
                                        val date = LocalDateTime.parse(it.createdAt)
                                        val month = date.month
                                        val year = date.year.toString()

                                        if (year == selectedYear) {
                                            if (month.toString() == "JANUARY") {
                                                totalJanUsersCount.add(it.id)
                                                barSet["JAN"] = totalJanUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "FEBRUARY") {
                                                totalFebUsersCount.add(it.id)
                                                barSet["FEB"] = totalFebUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "MARCH") {
                                                totalMarUsersCount.add(it.id)
                                                barSet["MAR"] = totalMarUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "APRIL") {
                                                totalAprUsersCount.add(it.id)
                                                barSet["APR"] = totalAprUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "MAY") {
                                                totalMayUsersCount.add(it.id)
                                                barSet["MAY"] = totalMayUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "JUNE") {
                                                totalJunUsersCount.add(it.id)
                                                barSet["JUN"] = totalJunUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "JULY") {
                                                totalJulUsersCount.add(it.id)
                                                barSet["JUL"] = totalJulUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "AUGUST") {
                                                totalAugUsersCount.add(it.id)
                                                barSet["AUG"] = totalAugUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "SEPTEMBER") {
                                                totalSepUsersCount.add(it.id)
                                                barSet["SEP"] = totalSepUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "OCTOBER") {
                                                totalOctUsersCount.add(it.id)
                                                barSet["OCT"] = totalOctUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "NOVEMBER") {
                                                totalNovUsersCount.add(it.id)
                                                barSet["NOV"] = totalNovUsersCount.count().toFloat()
                                            }

                                            if (month.toString() == "DECEMBER") {
                                                totalDecUsersCount.add(it.id)
                                                barSet["DEC"] = totalDecUsersCount.count().toFloat()
                                            }
                                        }
                                    }
                                    Log.d("IsiDariTotalbarSetOutside", "${barSet["JAN"]} dan ${barSet["FEB"]}" )

                                    binding.lineChartView.animation.duration = animationDuration
                                    binding.lineChartView.animate(barSet.toList())
                                }
                            }

                            binding.totalAhliView.text = "${totalAhli.count()}"
                            binding.totalAdminView.text = "${totalAdmin.count()}"
                            binding.totalPenggunaView.text = "${totalPengguna.count()}"

                            binding.lineChartView.animation.duration = animationDuration
                            binding.lineChartView.animate(barSet.toList())
                            binding.lineChartView.onDataPointClickListener = { index, _, _ ->

                                totalAdminByDate.clear()
                                totalAhliByDate.clear()
                                totalPenggunaByDate.clear()

                                result.data.dataUser.forEach {
                                    val date = LocalDateTime.parse(it.createdAt)
                                    val month = date.month.toString()
                                    val year = date.year.toString()
                                    val monthThreeWord = "${month[0]}${month[1]}${month[2]}"
                                    Log.d("IsiDari3KataBulan", monthThreeWord)

                                    if (selectedYear == null) {
                                        if (year == currentDate.year.toString()) {
                                            if (monthThreeWord == barSet.keys.elementAt(index)) {
                                                if (it.roleId == "676190fdcc4fa7bc6c0bdbc6") {
                                                    totalAdminByDate.add(it.roleId)
                                                } else if (it.roleId == "676190f1cc4fa7bc6c0bdbc4") {
                                                    totalPenggunaByDate.add(it.roleId)
                                                } else if (it.roleId == "67619109cc4fa7bc6c0bdbc8") {
                                                    totalAhliByDate.add(it.roleId)
                                                }
                                            }
                                        }
                                    } else {
                                        if (year == selectedYear) {
                                            if (monthThreeWord == barSet.keys.elementAt(index)) {
                                                if (it.roleId == "676190fdcc4fa7bc6c0bdbc6") {
                                                    totalAdminByDate.add(it.roleId)
                                                } else if (it.roleId == "676190f1cc4fa7bc6c0bdbc4") {
                                                    totalPenggunaByDate.add(it.roleId)
                                                } else if (it.roleId == "67619109cc4fa7bc6c0bdbc8") {
                                                    totalAhliByDate.add(it.roleId)
                                                }
                                            }
                                        }
                                    }
                                }

                                val builderSpecificMonth = AlertDialog.Builder(this@UsersBasedActivity)
                                val dialogViewSpecificMonth = layoutInflater.inflate(R.layout.users_diagram_detail_card, null)
                                val countViewAdmin = dialogViewSpecificMonth.findViewById<TextView>(R.id.totalAdminViewDialog)
                                val countViewAhli = dialogViewSpecificMonth.findViewById<TextView>(R.id.totalAhliViewDialog)
                                val countViewPengguna = dialogViewSpecificMonth.findViewById<TextView>(R.id.totalPenggunaViewDialog)
                                val monthView = dialogViewSpecificMonth.findViewById<TextView>(R.id.viewTextMonth)

                                builderSpecificMonth.setView(dialogViewSpecificMonth)
                                val dialogBuild = builderSpecificMonth.create()
                                dialogBuild.show()

                                countViewAhli.text = totalAhliByDate.count().toString()
                                countViewPengguna.text = totalPenggunaByDate.count().toString()
                                countViewAdmin.text = totalAdminByDate.count().toString()

                                if (selectedYear == null) {
                                    monthView.text = "${barSet.keys.elementAt(index)}, ${currentDate.year}"
                                } else {
                                    monthView.text = "${barSet.keys.elementAt(index)}, $selectedYear"
                                }
                            }

                            usersAdapter = AllUsersAdapter(this@UsersBasedActivity) { idClicked ->
                                val builder = AlertDialog.Builder(this@UsersBasedActivity)
                                val inflater = layoutInflater
                                val dialogView = inflater.inflate(R.layout.fragment_profile, null)
                                val imageEdit = dialogView.findViewById<ImageView>(R.id.image_edit)
                                val usernameEdit = dialogView.findViewById<TextView>(R.id.username_edit)
                                val emailView = dialogView.findViewById<TextView>(R.id.email_pengguna)
                                val roleView = dialogView.findViewById<TextView>(R.id.roleView)
                                val statusUser = dialogView.findViewById<MaterialButton>(R.id.status)
                                val editCardPhoto = dialogView.findViewById<CardView>(R.id.edit_button)
                                val logoutButton = dialogView.findViewById<MaterialButton>(R.id.logoutbutton)
                                val dokumen = dialogView.findViewById<MaterialButton>(R.id.seeDoc)
                                var documentLink: String? = null

                                logoutButton.text = getString(R.string.hapus)
                                editCardPhoto.visibility = View.GONE
                                usernameEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                                builder.setView(dialogView)
                                val dialogProfile = builder.create()
                                dialogProfile.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialogProfile.show()

                                result.data.dataUser.forEach {
                                    if (it.id == idClicked ) {
                                        documentLink = it.supportDocument

                                        if (it.roleId != "676190f1cc4fa7bc6c0bdbc4") {
                                            dokumen.visibility = View.VISIBLE
                                        }

                                        if (it.fotoProfile != "none") {
                                            Glide.with(this@UsersBasedActivity)
                                                .load(it.fotoProfile)
                                                .transform(CenterCrop(), RoundedCorners(10000))
                                                .into(imageEdit)
                                        }

                                        roleIdList.forEach { role ->
                                            if (role == it.roleId) {
                                                val roleName = roleNamaList[roleIdList.indexOf(role)]
                                                roleView.text = roleName
                                            }
                                        }

                                        dokumen.setOnClickListener {
                                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(documentLink))
                                            startActivity(mapIntent)
                                        }

                                        usernameEdit.text = it.nama
                                        emailView.text = it.email
                                        Log.d("IsiDariItStatusId", it.statusId)
                                        statusIdList.forEach { status ->
                                            if (status == it.statusId) {
                                                statusName = statusNamaList[statusIdList.indexOf(it.statusId)]
                                                statusUser.text = statusName
                                                if (statusName == "Pending") {
                                                    statusUser.setTextColor(getColor(R.color.white))
                                                    statusUser.setBackgroundColor(getColor(R.color.pendingColor))
                                                } else if (statusName == "Unapproved") {
                                                    statusUser.setTextColor(getColor(R.color.white))
                                                    statusUser.setBackgroundColor(getColor(R.color.unapprovedColor))
                                                } else if (statusName == "Approved") {
                                                    statusUser.setTextColor(getColor(R.color.white))
                                                    statusUser.setBackgroundColor(getColor(R.color.approvedColor))
                                                }
                                            }
                                        }
                                    }
                                }

                                statusUser.setOnClickListener {
                                    var selectedItem: Int? = null
                                    var idApprovalSelected: String? = null
                                    var namaApprovalSelected: String? = null

                                    val dialog = layoutInflater.inflate(R.layout.status_fragment, null)
                                    val builder = BottomSheetDialog(this@UsersBasedActivity)
                                    val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                                    val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                                    val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                                    isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                                    editApproval.isEnabled = false
                                    var dataDeskripsi: String? = null
                                    var idStatus: String? = null

                                    builder.setContentView(dialog)
                                    builder.show()

                                    allUsersViewModel.getNoteData(idClicked).observe(this@UsersBasedActivity) { result ->
                                        if (result != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    isLoadingBar.visibility = View.VISIBLE
                                                }

                                                is Result.Success -> {
                                                    descriptionStatus.visibility = View.VISIBLE
                                                    descriptionStatus.text = result.data.note
                                                    statusApproval.text = statusName

                                                    dataDeskripsi = result.data.note
                                                    idStatus = result.data.idStatus

                                                    if (statusName == "Pending") {
                                                        statusApproval.setTextColor(getColor(R.color.white))
                                                        statusApproval.setBackgroundColor(getColor(R.color.pendingColor))
                                                    } else if (statusName == "Unapproved") {
                                                        statusApproval.setTextColor(getColor(R.color.white))
                                                        statusApproval.setBackgroundColor(getColor(R.color.unapprovedColor))
                                                    } else if (statusName == "Approved") {
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
                                    editApproval.visibility = View.VISIBLE
                                    editApproval.setOnClickListener {
                                        val dialogView = layoutInflater.inflate(R.layout.edit_approval_fragment, null)
                                        val dialogBuilder = AlertDialog.Builder(this@UsersBasedActivity)
                                        val ubahBtn: MaterialButton = dialogView.findViewById(R.id.ubahApproval)
                                        val batalBtn: MaterialButton = dialogView.findViewById(R.id.batalApproval)
                                        val deskripsiView: TextInputEditText = dialogView.findViewById(R.id.descriptionApproval)
                                        val approvalChoose: MaterialButton = dialogView.findViewById(R.id.statusEdit)
                                        isLoadingEditApproval = dialogView.findViewById(R.id.progress_bar_dialog_edit_approval)

                                        deskripsiView.setText(dataDeskripsi)
                                        approvalChoose.text = statusName
                                        if (statusName == "Pending") {
                                            approvalChoose.setTextColor(getColor(R.color.white))
                                            approvalChoose.setBackgroundColor(getColor(R.color.pendingColor))
                                        } else if (statusName == "Unapproved") {
                                            approvalChoose.setTextColor(getColor(R.color.white))
                                            approvalChoose.setBackgroundColor(getColor(R.color.unapprovedColor))
                                        } else if (statusName == "Approved") {
                                            approvalChoose.setTextColor(getColor(R.color.white))
                                            approvalChoose.setBackgroundColor(getColor(R.color.approvedColor))
                                        }

                                        dialogBuilder.setView(dialogView)
                                        dialogBuilder.create()
                                        val editDialog = dialogBuilder.show()

                                        approvalChoose.setOnClickListener {
                                            val dialogViewList = layoutInflater.inflate(R.layout.status_list_layout, null)
                                            val builderDialog = AlertDialog.Builder(this@UsersBasedActivity)
                                            val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                                            builderDialog.setView(dialogViewList)
                                            builderDialog.create()
                                            val dialogWindow = builderDialog.show()

                                            val adapter = ArrayAdapter(this@UsersBasedActivity, android.R.layout.simple_list_item_single_choice, statusNamaList)
                                            listViewStatus.adapter = adapter
                                            listViewStatus.choiceMode = ListView.CHOICE_MODE_SINGLE

                                            if (selectedItem != null) {
                                                listViewStatus.setItemChecked(selectedItem!!, true)
                                            } else {
                                                listViewStatus.setItemChecked(statusIdList.indexOf(idStatus), true)
                                            }

                                            listViewStatus.setOnItemClickListener { adapterView, view, i, l ->
                                                namaApprovalSelected = statusNamaList[i]
                                                idApprovalSelected = statusIdList[i]
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
                                                allUsersViewModel.updateApprovalUsers(idClicked, approvalNote, statusId).observe(this@UsersBasedActivity) { update ->
                                                    if (update != null) {
                                                        when (update) {
                                                            is Result.Loading -> {
                                                                isLoadingEditApproval.visibility = View.VISIBLE
                                                            }

                                                            is Result.Success -> {
                                                                showToast(update.data)
                                                                getAllUsersData()
                                                                isLoadingEditApproval.visibility = View.GONE
                                                                editDialog.dismiss()
                                                                builder.dismiss()
                                                                dialogProfile.dismiss()
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

                                logoutButton.setOnClickListener {
                                    val builderDelete = AlertDialog.Builder(this@UsersBasedActivity)
                                    val dialogViewDelete = layoutInflater.inflate(R.layout.fragment_choose, null)

                                    val buttonDelete = dialogViewDelete.findViewById<Button>(R.id.deletebtnsanggar)
                                    val buttonBatal = dialogViewDelete.findViewById<Button>(R.id.batalbtn)
                                    val loadingBar = dialogViewDelete.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                                    builderDelete.setView(dialogViewDelete)
                                    val dialogViewDel = builderDelete.create()
                                    dialogViewDel.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    dialogViewDel.show()

                                    buttonDelete.setOnClickListener {
                                        allUsersViewModel.deleteUserById(idClicked).observe(this@UsersBasedActivity) { result ->
                                            if (result != null) {
                                                when (result) {
                                                    is Result.Loading -> {
                                                        loadingBar.visibility = View.VISIBLE
                                                    }

                                                    is Result.Success -> {
                                                        showToast(result.data)
                                                        loadingBar.visibility = View.GONE
                                                        getAllUsersData()
                                                        refreshDiagram()
                                                        dialogViewDel.dismiss()
                                                        dialogProfile.dismiss()
                                                    }

                                                    is Result.Error -> {
                                                        showToast(result.error)
                                                        loadingBar.visibility = View.GONE
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    buttonBatal.setOnClickListener { dialogViewDel.dismiss() }
                                }
                            }
                            binding.usersRv.layoutManager = LinearLayoutManager(this@UsersBasedActivity, LinearLayoutManager.VERTICAL, false)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_10dp)
                            binding.usersRv.addItemDecoration(
                                CustomItemDecorationVerticalUpperBottom(spacingInPixels)
                            )
                            binding.usersRv.setHasFixedSize(true)
                            binding.usersRv.adapter = usersAdapter
                            usersAdapter.setListUsers(result.data.dataUser)
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            isLoading(false)
                        }
                    }
                }
            }
        }

        binding.seeAllUsers.setOnClickListener {
            val intent = Intent(this@UsersBasedActivity, AllUsersActivity::class.java)
            startActivity(intent)
        }

        binding.scrolling.setOnRefreshListener {
            refreshDiagram()
            getAllUsersData()
            binding.scrolling.isRefreshing = false
        }

        supportActionBar?.hide()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun refreshDiagram() {
        val totalAdmin: MutableList<String> = mutableListOf()
        val totalPengguna: MutableList<String> = mutableListOf()
        val totalAhli: MutableList<String> = mutableListOf()

        val totalAdminByDate: MutableList<String> = mutableListOf()
        val totalPenggunaByDate: MutableList<String> = mutableListOf()
        val totalAhliByDate: MutableList<String> = mutableListOf()

        val totalJanUsersCount: MutableList<String> = mutableListOf()
        val totalFebUsersCount: MutableList<String> = mutableListOf()
        val totalMarUsersCount: MutableList<String> = mutableListOf()
        val totalAprUsersCount: MutableList<String> = mutableListOf()
        val totalMayUsersCount: MutableList<String> = mutableListOf()
        val totalJunUsersCount: MutableList<String> = mutableListOf()
        val totalJulUsersCount: MutableList<String> = mutableListOf()
        val totalAugUsersCount: MutableList<String> = mutableListOf()
        val totalSepUsersCount: MutableList<String> = mutableListOf()
        val totalOctUsersCount: MutableList<String> = mutableListOf()
        val totalNovUsersCount: MutableList<String> = mutableListOf()
        val totalDecUsersCount: MutableList<String> = mutableListOf()

        allUsersViewModel.getAllUsersData().observe(this@UsersBasedActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        totalAdmin.clear()
                        totalPengguna.clear()
                        totalAhli.clear()

                        barSet.replaceAll {_, _ -> 0F}
                        totalJanUsersCount.clear()
                        totalFebUsersCount.clear()
                        totalMarUsersCount.clear()
                        totalAprUsersCount.clear()
                        totalMayUsersCount.clear()
                        totalJunUsersCount.clear()
                        totalJulUsersCount.clear()
                        totalAugUsersCount.clear()
                        totalSepUsersCount.clear()
                        totalOctUsersCount.clear()
                        totalNovUsersCount.clear()
                        totalDecUsersCount.clear()

                        val currentDate = LocalDate.now()
                        var selectedItem: Int
                        selectedItem = if (selectedYear == null) {
                            listOfYear.indexOf(currentDate.year.toString())
                        } else {
                            listOfYear.indexOf(selectedYear)
                        }

                        val yearNow = currentDate.year.toString()

                        result.data.dataUser.forEach {
                            if (it.roleId == "676190fdcc4fa7bc6c0bdbc6") {
                                totalAdmin.add(it.roleId)
                            } else if (it.roleId == "676190f1cc4fa7bc6c0bdbc4") {
                                totalPengguna.add(it.roleId)
                            } else if (it.roleId == "67619109cc4fa7bc6c0bdbc8") {
                                totalAhli.add(it.roleId)
                            }

                            val date = LocalDateTime.parse(it.createdAt)
                            val month = date.month
                            val year = date.year.toString()

                            if (year == yearNow) {
                                if (month.toString() == "JANUARY") {
                                    totalJanUsersCount.add(it.id)
                                    barSet["JAN"] = totalJanUsersCount.count().toFloat()
                                }

                                if (month.toString() == "FEBRUARY") {
                                    totalFebUsersCount.add(it.id)
                                    barSet["FEB"] = totalFebUsersCount.count().toFloat()
                                }

                                if (month.toString() == "MARCH") {
                                    totalMarUsersCount.add(it.id)
                                    barSet["MAR"] = totalMarUsersCount.count().toFloat()
                                }

                                if (month.toString() == "APRIL") {
                                    totalAprUsersCount.add(it.id)
                                    barSet["APR"] = totalAprUsersCount.count().toFloat()
                                }

                                if (month.toString() == "MAY") {
                                    totalMayUsersCount.add(it.id)
                                    barSet["MAY"] = totalMayUsersCount.count().toFloat()
                                }

                                if (month.toString() == "JUNE") {
                                    totalJunUsersCount.add(it.id)
                                    barSet["JUN"] = totalJunUsersCount.count().toFloat()
                                }

                                if (month.toString() == "JULY") {
                                    totalJulUsersCount.add(it.id)
                                    barSet["JUL"] = totalJulUsersCount.count().toFloat()
                                }

                                if (month.toString() == "AUGUST") {
                                    totalAugUsersCount.add(it.id)
                                    barSet["AUG"] = totalAugUsersCount.count().toFloat()
                                }

                                if (month.toString() == "SEPTEMBER") {
                                    totalSepUsersCount.add(it.id)
                                    barSet["SEP"] = totalSepUsersCount.count().toFloat()
                                }

                                if (month.toString() == "OCTOBER") {
                                    totalOctUsersCount.add(it.id)
                                    barSet["OCT"] = totalOctUsersCount.count().toFloat()
                                }

                                if (month.toString() == "NOVEMBER") {
                                    totalNovUsersCount.add(it.id)
                                    barSet["NOV"] = totalNovUsersCount.count().toFloat()
                                }

                                if (month.toString() == "DECEMBER") {
                                    totalDecUsersCount.add(it.id)
                                    barSet["DEC"] = totalDecUsersCount.count().toFloat()
                                }
                            }
                        }

                        binding.yearChoose.text = currentDate.year.toString()
                        binding.yearChoose.setOnClickListener {
                            val dialogViewList = layoutInflater.inflate(R.layout.status_list_layout, null)
                            val builderDialog = AlertDialog.Builder(this@UsersBasedActivity)
                            val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                            builderDialog.setView(dialogViewList)
                            builderDialog.create()
                            val dialogWindow = builderDialog.show()

                            val adapter = ArrayAdapter(this@UsersBasedActivity, android.R.layout.simple_list_item_single_choice, listOfYear)
                            listViewStatus.adapter = adapter
                            listViewStatus.choiceMode = ListView.CHOICE_MODE_SINGLE

                            listViewStatus.setItemChecked(selectedItem, true)

                            listViewStatus.setOnItemClickListener { adapterView, view, i, l ->
                                selectedItem = i
                                selectedYear = listOfYear[i]
                                binding.yearChoose.text = listOfYear[i]
                                dialogWindow.dismiss()

                                barSet.replaceAll {_, _ -> 0F}

                                totalJanUsersCount.clear()
                                totalFebUsersCount.clear()
                                totalMarUsersCount.clear()
                                totalAprUsersCount.clear()
                                totalMayUsersCount.clear()
                                totalJunUsersCount.clear()
                                totalJulUsersCount.clear()
                                totalAugUsersCount.clear()
                                totalSepUsersCount.clear()
                                totalOctUsersCount.clear()
                                totalNovUsersCount.clear()
                                totalDecUsersCount.clear()

                                totalAhliByDate.clear()
                                totalPenggunaByDate.clear()
                                totalAdminByDate.clear()

                                result.data.dataUser.forEach {
                                    val date = LocalDateTime.parse(it.createdAt)
                                    val month = date.month
                                    val year = date.year.toString()

                                    if (year == selectedYear) {
                                        if (month.toString() == "JANUARY") {
                                            totalJanUsersCount.add(it.id)
                                            barSet["JAN"] = totalJanUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "FEBRUARY") {
                                            totalFebUsersCount.add(it.id)
                                            barSet["FEB"] = totalFebUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "MARCH") {
                                            totalMarUsersCount.add(it.id)
                                            barSet["MAR"] = totalMarUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "APRIL") {
                                            totalAprUsersCount.add(it.id)
                                            barSet["APR"] = totalAprUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "MAY") {
                                            totalMayUsersCount.add(it.id)
                                            barSet["MAY"] = totalMayUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "JUNE") {
                                            totalJunUsersCount.add(it.id)
                                            barSet["JUN"] = totalJunUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "JULY") {
                                            totalJulUsersCount.add(it.id)
                                            barSet["JUL"] = totalJulUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "AUGUST") {
                                            totalAugUsersCount.add(it.id)
                                            barSet["AUG"] = totalAugUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "SEPTEMBER") {
                                            totalSepUsersCount.add(it.id)
                                            barSet["SEP"] = totalSepUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "OCTOBER") {
                                            totalOctUsersCount.add(it.id)
                                            barSet["OCT"] = totalOctUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "NOVEMBER") {
                                            totalNovUsersCount.add(it.id)
                                            barSet["NOV"] = totalNovUsersCount.count().toFloat()
                                        }

                                        if (month.toString() == "DECEMBER") {
                                            totalDecUsersCount.add(it.id)
                                            barSet["DEC"] = totalDecUsersCount.count().toFloat()
                                        }
                                    }
                                }
                                Log.d("IsiDariTotalbarSetOutside", "${barSet["JAN"]} dan ${barSet["FEB"]}" )

                                binding.lineChartView.animation.duration = animationDuration
                                binding.lineChartView.animate(barSet.toList())
                            }
                        }

                        binding.totalAhliView.text = "${totalAhli.count()}"
                        binding.totalAdminView.text = "${totalAdmin.count()}"
                        binding.totalPenggunaView.text = "${totalPengguna.count()}"

                        binding.lineChartView.animation.duration = animationDuration
                        binding.lineChartView.animate(barSet.toList())
                        binding.lineChartView.onDataPointClickListener = { index, _, _ ->

                            totalAdminByDate.clear()
                            totalAhliByDate.clear()
                            totalPenggunaByDate.clear()

                            result.data.dataUser.forEach {
                                val date = LocalDateTime.parse(it.createdAt)
                                val month = date.month.toString()
                                val year = date.year.toString()
                                val monthThreeWord = "${month[0]}${month[1]}${month[2]}"
                                Log.d("IsiDari3KataBulan", monthThreeWord)

                                if (selectedYear == null) {
                                    if (year == currentDate.year.toString()) {
                                        if (monthThreeWord == barSet.keys.elementAt(index)) {
                                            if (it.roleId == "676190fdcc4fa7bc6c0bdbc6") {
                                                totalAdminByDate.add(it.roleId)
                                            } else if (it.roleId == "676190f1cc4fa7bc6c0bdbc4") {
                                                totalPenggunaByDate.add(it.roleId)
                                            } else if (it.roleId == "67619109cc4fa7bc6c0bdbc8") {
                                                totalAhliByDate.add(it.roleId)
                                            }
                                        }
                                    }
                                } else {
                                    if (year == selectedYear) {
                                        if (monthThreeWord == barSet.keys.elementAt(index)) {
                                            if (it.roleId == "676190fdcc4fa7bc6c0bdbc6") {
                                                totalAdminByDate.add(it.roleId)
                                            } else if (it.roleId == "676190f1cc4fa7bc6c0bdbc4") {
                                                totalPenggunaByDate.add(it.roleId)
                                            } else if (it.roleId == "67619109cc4fa7bc6c0bdbc8") {
                                                totalAhliByDate.add(it.roleId)
                                            }
                                        }
                                    }
                                }
                            }

                            val builderSpecificMonth = AlertDialog.Builder(this@UsersBasedActivity)
                            val dialogViewSpecificMonth = layoutInflater.inflate(R.layout.users_diagram_detail_card, null)
                            val countViewAdmin = dialogViewSpecificMonth.findViewById<TextView>(R.id.totalAdminViewDialog)
                            val countViewAhli = dialogViewSpecificMonth.findViewById<TextView>(R.id.totalAhliViewDialog)
                            val countViewPengguna = dialogViewSpecificMonth.findViewById<TextView>(R.id.totalPenggunaViewDialog)
                            val monthView = dialogViewSpecificMonth.findViewById<TextView>(R.id.viewTextMonth)

                            builderSpecificMonth.setView(dialogViewSpecificMonth)
                            val dialogBuild = builderSpecificMonth.create()
                            dialogBuild.show()

                            countViewAhli.text = totalAhliByDate.count().toString()
                            countViewPengguna.text = totalPenggunaByDate.count().toString()
                            countViewAdmin.text = totalAdminByDate.count().toString()

                            if (selectedYear == null) {
                                monthView.text = "${barSet.keys.elementAt(index)}, ${currentDate.year}"
                            } else {
                                monthView.text = "${barSet.keys.elementAt(index)}, $selectedYear"
                            }
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

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllUsersData() {
        allUsersViewModel.getAllUsersData().observe(this@UsersBasedActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        usersAdapter = AllUsersAdapter(this@UsersBasedActivity) { idClicked ->
                            val builder = AlertDialog.Builder(this@UsersBasedActivity)
                            val inflater = layoutInflater
                            val dialogView = inflater.inflate(R.layout.fragment_profile, null)
                            val imageEdit = dialogView.findViewById<ImageView>(R.id.image_edit)
                            val usernameEdit = dialogView.findViewById<TextView>(R.id.username_edit)
                            val emailView = dialogView.findViewById<TextView>(R.id.email_pengguna)
                            val roleView = dialogView.findViewById<TextView>(R.id.roleView)
                            val statusUser = dialogView.findViewById<MaterialButton>(R.id.status)
                            val editCardPhoto = dialogView.findViewById<CardView>(R.id.edit_button)
                            val logoutButton = dialogView.findViewById<MaterialButton>(R.id.logoutbutton)
                            val dokumen = dialogView.findViewById<MaterialButton>(R.id.seeDoc)
                            var documentLink: String? = null

                            logoutButton.text = getString(R.string.hapus)
                            editCardPhoto.visibility = View.GONE
                            usernameEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                            builder.setView(dialogView)
                            val dialogProfile = builder.create()
                            dialogProfile.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialogProfile.show()

                            result.data.dataUser.forEach {
                                if (it.id == idClicked ) {
                                    documentLink = it.supportDocument

                                    if (it.roleId != "676190f1cc4fa7bc6c0bdbc4") {
                                        dokumen.visibility = View.VISIBLE
                                    }

                                    dokumen.setOnClickListener {
                                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(documentLink))
                                        startActivity(mapIntent)
                                    }

                                    if (it.fotoProfile != "none") {
                                        Glide.with(this@UsersBasedActivity)
                                            .load(it.fotoProfile)
                                            .transform(CenterCrop(), RoundedCorners(10000))
                                            .into(imageEdit)
                                    }

                                    roleIdList.forEach { role ->
                                        if (role == it.roleId) {
                                            val roleName = roleNamaList[roleIdList.indexOf(role)]
                                            roleView.text = roleName
                                        }
                                    }

                                    usernameEdit.text = it.nama
                                    emailView.text = it.email
                                    Log.d("IsiDariItStatusId", it.statusId)
                                    statusIdList.forEach { status ->
                                        if (status == it.statusId) {
                                            statusName = statusNamaList[statusIdList.indexOf(it.statusId)]
                                            statusUser.text = statusName
                                            if (statusName == "Pending") {
                                                statusUser.setTextColor(getColor(R.color.white))
                                                statusUser.setBackgroundColor(getColor(R.color.pendingColor))
                                            } else if (statusName == "Unapproved") {
                                                statusUser.setTextColor(getColor(R.color.white))
                                                statusUser.setBackgroundColor(getColor(R.color.unapprovedColor))
                                            } else if (statusName == "Approved") {
                                                statusUser.setTextColor(getColor(R.color.white))
                                                statusUser.setBackgroundColor(getColor(R.color.approvedColor))
                                            }
                                        }
                                    }
                                }
                            }

                            statusUser.setOnClickListener {
                                var selectedItem: Int? = null
                                var idApprovalSelected: String? = null
                                var namaApprovalSelected: String? = null

                                val dialog = layoutInflater.inflate(R.layout.status_fragment, null)
                                val builder = BottomSheetDialog(this@UsersBasedActivity)
                                val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                                val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                                val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                                isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                                editApproval.isEnabled = false
                                var dataDeskripsi: String? = null
                                var idStatus: String? = null

                                builder.setContentView(dialog)
                                builder.show()

                                allUsersViewModel.getNoteData(idClicked).observe(this@UsersBasedActivity) { result ->
                                    if (result != null) {
                                        when (result) {
                                            is Result.Loading -> {
                                                isLoadingBar.visibility = View.VISIBLE
                                            }

                                            is Result.Success -> {
                                                descriptionStatus.visibility = View.VISIBLE
                                                descriptionStatus.text = result.data.note
                                                statusApproval.text = statusName

                                                dataDeskripsi = result.data.note
                                                idStatus = result.data.idStatus

                                                if (statusName == "Pending") {
                                                    statusApproval.setTextColor(getColor(R.color.white))
                                                    statusApproval.setBackgroundColor(getColor(R.color.pendingColor))
                                                } else if (statusName == "Unapproved") {
                                                    statusApproval.setTextColor(getColor(R.color.white))
                                                    statusApproval.setBackgroundColor(getColor(R.color.unapprovedColor))
                                                } else if (statusName == "Approved") {
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
                                editApproval.visibility = View.VISIBLE
                                editApproval.setOnClickListener {
                                    val dialogView = layoutInflater.inflate(R.layout.edit_approval_fragment, null)
                                    val dialogBuilder = AlertDialog.Builder(this@UsersBasedActivity)
                                    val ubahBtn: MaterialButton = dialogView.findViewById(R.id.ubahApproval)
                                    val batalBtn: MaterialButton = dialogView.findViewById(R.id.batalApproval)
                                    val deskripsiView: TextInputEditText = dialogView.findViewById(R.id.descriptionApproval)
                                    val approvalChoose: MaterialButton = dialogView.findViewById(R.id.statusEdit)
                                    isLoadingEditApproval = dialogView.findViewById(R.id.progress_bar_dialog_edit_approval)

                                    deskripsiView.setText(dataDeskripsi)
                                    approvalChoose.text = statusName
                                    if (statusName == "Pending") {
                                        approvalChoose.setTextColor(getColor(R.color.white))
                                        approvalChoose.setBackgroundColor(getColor(R.color.pendingColor))
                                    } else if (statusName == "Unapproved") {
                                        approvalChoose.setTextColor(getColor(R.color.white))
                                        approvalChoose.setBackgroundColor(getColor(R.color.unapprovedColor))
                                    } else if (statusName == "Approved") {
                                        approvalChoose.setTextColor(getColor(R.color.white))
                                        approvalChoose.setBackgroundColor(getColor(R.color.approvedColor))
                                    }

                                    dialogBuilder.setView(dialogView)
                                    dialogBuilder.create()
                                    val editDialog = dialogBuilder.show()

                                    approvalChoose.setOnClickListener {
                                        val dialogViewList = layoutInflater.inflate(R.layout.status_list_layout, null)
                                        val builderDialog = AlertDialog.Builder(this@UsersBasedActivity)
                                        val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                                        builderDialog.setView(dialogViewList)
                                        builderDialog.create()
                                        val dialogWindow = builderDialog.show()

                                        val adapter = ArrayAdapter(this@UsersBasedActivity, android.R.layout.simple_list_item_single_choice, statusNamaList)
                                        listViewStatus.adapter = adapter
                                        listViewStatus.choiceMode = ListView.CHOICE_MODE_SINGLE

                                        if (selectedItem != null) {
                                            listViewStatus.setItemChecked(selectedItem!!, true)
                                        } else {
                                            listViewStatus.setItemChecked(statusIdList.indexOf(idStatus), true)
                                        }

                                        listViewStatus.setOnItemClickListener { adapterView, view, i, l ->
                                            namaApprovalSelected = statusNamaList[i]
                                            idApprovalSelected = statusIdList[i]
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
                                            allUsersViewModel.updateApprovalUsers(idClicked, approvalNote, statusId).observe(this@UsersBasedActivity) { update ->
                                                if (update != null) {
                                                    when (update) {
                                                        is Result.Loading -> {
                                                            isLoadingEditApproval.visibility = View.VISIBLE
                                                        }

                                                        is Result.Success -> {
                                                            showToast(update.data)
                                                            getAllUsersData()
                                                            isLoadingEditApproval.visibility = View.GONE
                                                            editDialog.dismiss()
                                                            builder.dismiss()
                                                            dialogProfile.dismiss()
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

                            logoutButton.setOnClickListener {
                                val builderDelete = AlertDialog.Builder(this@UsersBasedActivity)
                                val dialogViewDelete = layoutInflater.inflate(R.layout.fragment_choose, null)

                                val buttonDelete = dialogViewDelete.findViewById<Button>(R.id.deletebtnsanggar)
                                val buttonBatal = dialogViewDelete.findViewById<Button>(R.id.batalbtn)
                                val loadingBar = dialogViewDelete.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                                builderDelete.setView(dialogViewDelete)
                                val dialogViewDel = builderDelete.create()
                                dialogViewDel.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialogViewDel.show()

                                buttonDelete.setOnClickListener {
                                    allUsersViewModel.deleteUserById(idClicked).observe(this@UsersBasedActivity) { result ->
                                        if (result != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    loadingBar.visibility = View.VISIBLE
                                                }

                                                is Result.Success -> {
                                                    showToast(result.data)
                                                    loadingBar.visibility = View.GONE
                                                    getAllUsersData()
                                                    dialogViewDel.dismiss()
                                                    dialogProfile.dismiss()
                                                }

                                                is Result.Error -> {
                                                    showToast(result.error)
                                                    loadingBar.visibility = View.GONE
                                                }
                                            }
                                        }
                                    }
                                }

                                buttonBatal.setOnClickListener { dialogViewDel.dismiss() }
                            }
                        }
                        binding.usersRv.layoutManager = LinearLayoutManager(this@UsersBasedActivity, LinearLayoutManager.VERTICAL, false)
                        binding.usersRv.setHasFixedSize(true)
                        binding.usersRv.adapter = usersAdapter
                        usersAdapter.setListUsers(result.data.dataUser)
                        usersAdapter.notifyDataSetChanged()
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun getAllRoleList() {
        allUsersViewModel.getAllRoleList().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        result.data.forEach {
                            roleNamaList.add(it.role)
                            roleIdList.add(it.id)
                        }
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

    private fun getAllStatusList() {
        allUsersViewModel.getListStatus().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        result.data.forEach {
                            statusNamaList.add(it.status)
                            statusIdList.add(it.id)
                        }
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

    private fun showToast(message: String) {
        Toast.makeText(this@UsersBasedActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}