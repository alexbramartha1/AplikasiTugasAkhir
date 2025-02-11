package com.reviling.filamentandroid.ui.seeallusers

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.databinding.ActivityAllUsersBinding
import com.reviling.filamentandroid.ui.CustomItemDecorationVertical
import com.reviling.filamentandroid.ui.CustomItemDecorationVerticalUpperBottom
import com.reviling.filamentandroid.ui.adapter.AllUsersAdapter
import com.reviling.filamentandroid.ui.adapter.SanggarSeeAllAdapter
import com.reviling.filamentandroid.ui.adapter.SanggarViewPagerAdapter
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity.Companion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllUsersBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllUsersBinding.inflate(layoutInflater)
        val view = binding.root

        enableEdgeToEdge()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            allUsersViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@AllUsersActivity)
                    .create(AllUsersViewModel::class.java)
            }

            getAllRoleList()
            getAllStatusList()

            allUsersViewModel.getAllUsersData().observe(this@AllUsersActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            usersAdapter = AllUsersAdapter(this@AllUsersActivity) { idClicked ->
                                val builder = AlertDialog.Builder(this@AllUsersActivity)
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
                                            Glide.with(this@AllUsersActivity)
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
                                    val builder = BottomSheetDialog(this@AllUsersActivity)
                                    val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                                    val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                                    val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                                    isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                                    editApproval.isEnabled = false
                                    var dataDeskripsi: String? = null
                                    var idStatus: String? = null

                                    builder.setContentView(dialog)
                                    builder.show()

                                    allUsersViewModel.getNoteData(idClicked).observe(this@AllUsersActivity) { result ->
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
                                        val dialogBuilder = AlertDialog.Builder(this@AllUsersActivity)
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
                                            val builderDialog = AlertDialog.Builder(this@AllUsersActivity)
                                            val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                                            builderDialog.setView(dialogViewList)
                                            builderDialog.create()
                                            val dialogWindow = builderDialog.show()

                                            val adapter = ArrayAdapter(this@AllUsersActivity, android.R.layout.simple_list_item_single_choice, statusNamaList)
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
                                                allUsersViewModel.updateApprovalUsers(idClicked, approvalNote, statusId).observe(this@AllUsersActivity) { update ->
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
                                    val builderDelete = AlertDialog.Builder(this@AllUsersActivity)
                                    val dialogViewDelete = layoutInflater.inflate(R.layout.fragment_choose, null)

                                    val buttonDelete = dialogViewDelete.findViewById<Button>(R.id.deletebtnsanggar)
                                    val buttonBatal = dialogViewDelete.findViewById<Button>(R.id.batalbtn)
                                    val loadingBar = dialogViewDelete.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                                    builderDelete.setView(dialogViewDelete)
                                    val dialogViewDel = builderDelete.create()
                                    dialogViewDel.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    dialogViewDel.show()

                                    buttonDelete.setOnClickListener {
                                        allUsersViewModel.deleteUserById(idClicked).observe(this@AllUsersActivity) { result ->
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
                            binding.usersRv.layoutManager = LinearLayoutManager(this@AllUsersActivity, LinearLayoutManager.VERTICAL, false)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_10dp)
                            binding.usersRv.addItemDecoration(CustomItemDecorationVerticalUpperBottom(spacingInPixels))
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

            binding.searchBarUsers.inflateMenu(R.menu.menu)
            binding.searchViewUsers.setupWithSearchBar(binding.searchBarUsers)
            binding.searchViewUsers
                .editText
                .setOnEditorActionListener { textView, i, keyEvent ->
                    binding.searchViewUsers.hide()
                    val value = binding.searchViewUsers.text.toString()
                    Log.d("IsiDariValue", value)

                    allUsersViewModel.getUsersByName(value).observe(this@AllUsersActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)

                                    usersAdapter = AllUsersAdapter(this@AllUsersActivity) { idClicked ->
                                        val builder = AlertDialog.Builder(this@AllUsersActivity)
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
                                                    Glide.with(this@AllUsersActivity)
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
                                            val builder = BottomSheetDialog(this@AllUsersActivity)
                                            val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                                            val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                                            val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                                            isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                                            editApproval.isEnabled = false
                                            var dataDeskripsi: String? = null
                                            var idStatus: String? = null

                                            builder.setContentView(dialog)
                                            builder.show()

                                            allUsersViewModel.getNoteData(idClicked).observe(this@AllUsersActivity) { result ->
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
                                                val dialogBuilder = AlertDialog.Builder(this@AllUsersActivity)
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
                                                    val builderDialog = AlertDialog.Builder(this@AllUsersActivity)
                                                    val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                                                    builderDialog.setView(dialogViewList)
                                                    builderDialog.create()
                                                    val dialogWindow = builderDialog.show()

                                                    val adapter = ArrayAdapter(this@AllUsersActivity, android.R.layout.simple_list_item_single_choice, statusNamaList)
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
                                                        allUsersViewModel.updateApprovalUsers(idClicked, approvalNote, statusId!!).observe(this@AllUsersActivity) { update ->
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
                                            val builderDelete = AlertDialog.Builder(this@AllUsersActivity)
                                            val dialogViewDelete = layoutInflater.inflate(R.layout.fragment_choose, null)

                                            val buttonDelete = dialogViewDelete.findViewById<Button>(R.id.deletebtnsanggar)
                                            val buttonBatal = dialogViewDelete.findViewById<Button>(R.id.batalbtn)
                                            val loadingBar = dialogViewDelete.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                                            builderDelete.setView(dialogViewDelete)
                                            val dialogViewDel = builderDelete.create()
                                            dialogViewDel.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                            dialogViewDel.show()

                                            buttonDelete.setOnClickListener {
                                                allUsersViewModel.deleteUserById(idClicked).observe(this@AllUsersActivity) { result ->
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
                                    binding.usersRv.layoutManager = LinearLayoutManager(this@AllUsersActivity, LinearLayoutManager.VERTICAL, false)
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

                    false
                }

            binding.searchBarUsers.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_filter -> {
                        val dialogView = layoutInflater.inflate(R.layout.filter_users_dialog, null)
                        val listViewStatus: ListView = dialogView.findViewById(R.id.listViewStatus)
                        val listViewRole: ListView = dialogView.findViewById(R.id.listViewRole)
                        val buttonPilih: Button = dialogView.findViewById(R.id.pilihFilter)
                        val buttonBatal: Button = dialogView.findViewById(R.id.batalFilter)
                        val buttonClearFilter: Button = dialogView.findViewById(R.id.clearFilter)

                        val adapterStatus = ArrayAdapter(
                            this@AllUsersActivity,
                            android.R.layout.simple_list_item_multiple_choice,
                            statusNamaList
                        )
                        listViewStatus.adapter = adapterStatus
                        listViewStatus.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                        selectedItemStatus.forEach {
                            listViewStatus.setItemChecked(it, true)
                        }


                        val adapterRole = ArrayAdapter(
                            this@AllUsersActivity,
                            android.R.layout.simple_list_item_multiple_choice,
                            roleNamaList
                        )
                        listViewRole.adapter = adapterRole
                        listViewRole.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                        selectedItemRole.forEach {
                            listViewRole.setItemChecked(it, true)
                        }

                        val builder = AlertDialog.Builder(this@AllUsersActivity)
                        builder
                            .setView(dialogView)

                        val dialog = builder.create()
                        dialog.show()

                        buttonPilih.setOnClickListener {
                            selectedIdStatus.clear()
                            selectedNamaStatus.clear()
                            selectedItemStatus.clear()
                            for (i in 0 until listViewStatus.count) {
                                if (listViewStatus.isItemChecked(i)) {
                                    selectedItemStatus.add(i)
                                    selectedIdStatus.add(statusIdList[i])
                                    adapterStatus.getItem(i)
                                        ?.let { it1 -> selectedNamaStatus.add(it1) }
                                }
                            }

                            selectedIdRole.clear()
                            selectedNamaRole.clear()
                            selectedItemRole.clear()
                            for (i in 0 until listViewRole.count) {
                                if (listViewRole.isItemChecked(i)) {
                                    selectedItemRole.add(i)
                                    selectedIdRole.add(roleIdList[i])
                                    adapterRole.getItem(i)
                                        ?.let { it1 -> selectedNamaRole.add(it1) }
                                }
                            }

                            if (selectedIdStatus.isNotEmpty() && selectedIdRole.isNotEmpty()) {
                                loadAllUsersByFilter()
                            }

                            dialog.dismiss()
                        }

                        buttonClearFilter.setOnClickListener {
                            selectedNamaStatus.clear()
                            selectedIdStatus.clear()
                            selectedItemStatus.clear()
                            selectedIdRole.clear()
                            selectedNamaRole.clear()
                            selectedItemRole.clear()
                            getAllUsersData()
                            dialog.dismiss()
                        }

                        buttonBatal.setOnClickListener {
                            dialog.dismiss()
                        }

                        true
                    }
                    else -> false
                }
            }

            binding.scrolling.setOnRefreshListener {
                getAllUsersData()
                selectedNamaStatus.clear()
                selectedIdStatus.clear()
                selectedItemStatus.clear()

                selectedIdRole.clear()
                selectedNamaRole.clear()
                selectedItemRole.clear()
                binding.scrolling.isRefreshing = false
            }
        }

        supportActionBar?.hide()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllUsersData() {
        allUsersViewModel.getAllUsersData().observe(this@AllUsersActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        usersAdapter = AllUsersAdapter(this@AllUsersActivity) { idClicked ->
                            val builder = AlertDialog.Builder(this@AllUsersActivity)
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
                                        Glide.with(this@AllUsersActivity)
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
                                val builder = BottomSheetDialog(this@AllUsersActivity)
                                val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                                val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                                val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                                isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                                editApproval.isEnabled = false
                                var dataDeskripsi: String? = null
                                var idStatus: String? = null

                                builder.setContentView(dialog)
                                builder.show()

                                allUsersViewModel.getNoteData(idClicked).observe(this@AllUsersActivity) { result ->
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
                                    val dialogBuilder = AlertDialog.Builder(this@AllUsersActivity)
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
                                        val builderDialog = AlertDialog.Builder(this@AllUsersActivity)
                                        val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                                        builderDialog.setView(dialogViewList)
                                        builderDialog.create()
                                        val dialogWindow = builderDialog.show()

                                        val adapter = ArrayAdapter(this@AllUsersActivity, android.R.layout.simple_list_item_single_choice, statusNamaList)
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
                                            allUsersViewModel.updateApprovalUsers(idClicked, approvalNote, statusId).observe(this@AllUsersActivity) { update ->
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
                                val builderDelete = AlertDialog.Builder(this@AllUsersActivity)
                                val dialogViewDelete = layoutInflater.inflate(R.layout.fragment_choose, null)

                                val buttonDelete = dialogViewDelete.findViewById<Button>(R.id.deletebtnsanggar)
                                val buttonBatal = dialogViewDelete.findViewById<Button>(R.id.batalbtn)
                                val loadingBar = dialogViewDelete.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                                builderDelete.setView(dialogViewDelete)
                                val dialogViewDel = builderDelete.create()
                                dialogViewDel.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialogViewDel.show()

                                buttonDelete.setOnClickListener {
                                    allUsersViewModel.deleteUserById(idClicked).observe(this@AllUsersActivity) { result ->
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
                        binding.usersRv.layoutManager = LinearLayoutManager(this@AllUsersActivity, LinearLayoutManager.VERTICAL, false)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun loadAllUsersByFilter() {
        allUsersViewModel.getUsersByFilter(selectedIdRole, selectedIdStatus).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        usersAdapter = AllUsersAdapter(this@AllUsersActivity) { idClicked ->
                            val builder = AlertDialog.Builder(this@AllUsersActivity)
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
                                        Glide.with(this@AllUsersActivity)
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
                                val builder = BottomSheetDialog(this@AllUsersActivity)
                                val descriptionStatus: TextView = dialog.findViewById(R.id.status_description)
                                val statusApproval: MaterialButton = dialog.findViewById(R.id.status_approval)
                                val editApproval: MaterialButton = dialog.findViewById(R.id.editApprovalBtn)
                                isLoadingBar = dialog.findViewById(R.id.progress_bar_dialog_ask)

                                editApproval.isEnabled = false
                                var dataDeskripsi: String? = null
                                var idStatus: String? = null

                                builder.setContentView(dialog)
                                builder.show()

                                allUsersViewModel.getNoteData(idClicked).observe(this@AllUsersActivity) { result ->
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
                                    val dialogBuilder = AlertDialog.Builder(this@AllUsersActivity)
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
                                        val builderDialog = AlertDialog.Builder(this@AllUsersActivity)
                                        val listViewStatus: ListView = dialogViewList.findViewById(R.id.listViewStatusApproval)

                                        builderDialog.setView(dialogViewList)
                                        builderDialog.create()
                                        val dialogWindow = builderDialog.show()

                                        val adapter = ArrayAdapter(this@AllUsersActivity, android.R.layout.simple_list_item_single_choice, statusNamaList)
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
                                            allUsersViewModel.updateApprovalUsers(idClicked, approvalNote, statusId).observe(this@AllUsersActivity) { update ->
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
                                val builderDelete = AlertDialog.Builder(this@AllUsersActivity)
                                val dialogViewDelete = layoutInflater.inflate(R.layout.fragment_choose, null)

                                val buttonDelete = dialogViewDelete.findViewById<Button>(R.id.deletebtnsanggar)
                                val buttonBatal = dialogViewDelete.findViewById<Button>(R.id.batalbtn)
                                val loadingBar = dialogViewDelete.findViewById<ProgressBar>(R.id.progress_bar_dialog_ask)

                                builderDelete.setView(dialogViewDelete)
                                val dialogViewDel = builderDelete.create()
                                dialogViewDel.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialogViewDel.show()

                                buttonDelete.setOnClickListener {
                                    allUsersViewModel.deleteUserById(idClicked).observe(this@AllUsersActivity) { result ->
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
                        binding.usersRv.layoutManager = LinearLayoutManager(this@AllUsersActivity, LinearLayoutManager.VERTICAL, false)
                        binding.usersRv.setHasFixedSize(true)
                        binding.usersRv.adapter = usersAdapter
                        usersAdapter.setListUsers(result.data.dataUser)
                        usersAdapter.notifyDataSetChanged()
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
        Toast.makeText(this@AllUsersActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}