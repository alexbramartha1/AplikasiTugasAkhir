package com.reviling.filamentandroid.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.google.android.material.search.SearchBar
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.databinding.ActivityHomeBinding
import com.reviling.filamentandroid.ui.adapter.HomeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.data.response.GamelanDataItem
import com.reviling.filamentandroid.data.response.InstrumentDataItem
import com.reviling.filamentandroid.data.response.SanggarDataItem
import com.reviling.filamentandroid.ui.BaseActivity
import com.reviling.filamentandroid.ui.CustomItemDecoration
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.adapter.SanggarAdapter
import com.reviling.filamentandroid.ui.login.LoginActivity
import com.reviling.filamentandroid.ui.login.LoginViewModel
import com.reviling.filamentandroid.ui.seeallgamelan.SeeAllGamelanBaliActivity
import com.reviling.filamentandroid.ui.seeallinstrument.DetailSeeAllInstrumentActivity
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity
import com.reviling.filamentandroid.ui.seeallusers.AllUsersActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import java.io.File

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: HomeAdapter
    private lateinit var adapterInstrument: InstrumentAdapter
    private lateinit var adapterSanggar: SanggarAdapter
    private lateinit var homeViewModel: HomeViewModel
    private var currentImageUri: Uri? = null
    private var file: File? = null
    private var roleId: String? = null
    private var statusId: String? = null
    private var nameRole: String? = null
    private var tokenUser: String? = null
    private lateinit var buttonUpdateDoc: MaterialButton
    private lateinit var buttonViewDoc: MaterialButton
    private var userID: String? = null
    private var fileSelected: File? = null
    private lateinit var isLoadingProfile: ProgressBar
    private var fullShowOrNot: Boolean = true
    private var dataSetApproved: MutableList<GamelanDataItem> = mutableListOf()
    private var dataSetApprovedInstrument: MutableList<InstrumentDataItem> = mutableListOf()
    private var dataSetApprovedSanggar: MutableList<SanggarDataItem> = mutableListOf()

    private val getFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data.let { uri ->
                if (uri != null) {
                    fileSelected = uriToFile(uri, this@HomeActivity)

                    lifecycleScope.launch {
                        homeViewModel = withContext(Dispatchers.IO) {
                            ViewModelFactory.getInstance(this@HomeActivity).create(HomeViewModel::class.java)
                        }

                        homeViewModel.updateDocumenet(userID!!, fileSelected!!).observe(this@HomeActivity) { user ->
                            if (user != null) {
                                when (user) {
                                    is Result.Loading -> {
                                        isLoadingProfile.visibility = View.VISIBLE
                                    }

                                    is Result.Success -> {
                                        isLoadingProfile.visibility = View.GONE
                                        showToast("Document Updated")
                                    }

                                    is Result.Error -> {
                                        showToast(user.error)
                                        isLoadingProfile.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun openFolderFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            val uri = Uri.parse("content://com.android.externalstorage")
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        }
        getFile.launch(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrolling)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            homeViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@HomeActivity).create(HomeViewModel::class.java)
            }

            binding.seeAllInstrumenBtn.setOnClickListener {
                val intent = Intent(this@HomeActivity, DetailSeeAllInstrumentActivity::class.java)
                startActivity(intent)
            }

            binding.seeAllGamelanBtn.setOnClickListener {
                val intent = Intent(this@HomeActivity, SeeAllGamelanBaliActivity::class.java)
                startActivity(intent)
            }

            binding.seeAllSanggarBtn.setOnClickListener {
                val intent = Intent(this@HomeActivity, SeeAllSanggarActivity::class.java)
                startActivity(intent)
            }

            binding.showEverything.setOnClickListener {
                val intent = Intent(this@HomeActivity, AllUsersActivity::class.java)
                startActivity(intent)
            }

            homeViewModel.getSessionUser().observe(this@HomeActivity) { user ->
                if (user.isLogin) {
                    binding.usernameTv.text = user.nama

                    if (user.role == "676190fdcc4fa7bc6c0bdbc6") {
                        binding.showEverythingCard.visibility = View.VISIBLE
                    }

                    if (user.foto_profile != "none") {
                        Glide.with(this@HomeActivity)
                            .load(user.foto_profile)
                            .transform(CenterCrop(), RoundedCorners(500))
                            .into(binding.profileImage)
                    }

                    homeViewModel.getUserDatabyId(user.user_id).observe(this@HomeActivity) { save ->
                        if (save != null) {
                            when (save) {
                                is Result.Loading -> {
                                    isLoading(false)
                                }

                                is Result.Success -> {
                                    if (save.data.status != user.status) {
                                        homeViewModel.saveSession(UserModel(
                                            nama = save.data.nama,
                                            access_token = user.access_token,
                                            email = save.data.email,
                                            user_id = save.data.id,
                                            foto_profile = save.data.fotoProfile,
                                            role = save.data.role,
                                            status = save.data.status,
                                            isLogin = true,
                                            document = save.data.supportDocument
                                        ))

                                        showToast("Status Updated")
                                        isLoading(false)
                                    }
                                }

                                is Result.Error -> {
                                    showToast(save.error)
                                    isLoading(false)
                                }
                            }
                        }
                    }

                    tokenUser = user.access_token
                    userID = user.user_id
                    roleId = user.role
                    statusId = user.status
                    getAllRoleList()

                    if (roleId == "676190f1cc4fa7bc6c0bdbc4" || statusId == "67618f9ecc4fa7bc6c0bdbbb" || statusId == "67618fe3cc4fa7bc6c0bdbc1") {
                        fullShowOrNot = true
                    } else {
                        fullShowOrNot = false
                    }

                    binding.profileImage.setOnClickListener {
                        val builder = AlertDialog.Builder(this@HomeActivity)
                        val inflater = layoutInflater
                        val dialogView = inflater.inflate(R.layout.fragment_profile, null)

                        val imageEdit = dialogView.findViewById<ImageView>(R.id.image_edit)
                        val usernameEdit = dialogView.findViewById<TextView>(R.id.username_edit)
                        val logoutButton = dialogView.findViewById<Button>(R.id.logoutbutton)
                        isLoadingProfile = dialogView.findViewById(R.id.progress_bar_profile)
                        val emailView = dialogView.findViewById<TextView>(R.id.email_pengguna)
                        val roleView = dialogView.findViewById<TextView>(R.id.roleView)
                        val statusUser = dialogView.findViewById<MaterialButton>(R.id.status)
                        val refreshBtn = dialogView.findViewById<MaterialButton>(R.id.refreshBtn)
                        buttonViewDoc = dialogView.findViewById(R.id.seeDoc)
                        buttonUpdateDoc = dialogView.findViewById(R.id.editDoc)

                        if (roleId != "676190f1cc4fa7bc6c0bdbc4") {
                            buttonViewDoc.visibility = View.VISIBLE
                            buttonUpdateDoc.visibility = View.VISIBLE
                        } else {
                            buttonViewDoc.visibility = View.GONE
                            buttonUpdateDoc.visibility = View.GONE
                        }

                        buttonViewDoc.setOnClickListener {
                            homeViewModel.getUserDatabyId(userID!!).observe(this@HomeActivity) { save ->
                                if (save != null) {
                                    when (save) {
                                        is Result.Loading -> {
                                            isLoadingProfile.visibility = View.VISIBLE
                                        }

                                        is Result.Success -> {
                                            Log.d("IsidariFile", user.document)
                                            Log.d("IsidariFileServer", save.data.supportDocument)
                                            isLoadingProfile.visibility = View.GONE
                                            homeViewModel.saveSession(UserModel(
                                                nama = save.data.nama,
                                                access_token = tokenUser!!,
                                                email = save.data.email,
                                                user_id = save.data.id,
                                                foto_profile = save.data.fotoProfile,
                                                role = save.data.role,
                                                status = save.data.status,
                                                isLogin = true,
                                                document = save.data.supportDocument
                                            ))

                                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(save.data.supportDocument))
                                            startActivity(mapIntent)
                                        }

                                        is Result.Error -> {
                                            showToast(save.error)
                                            isLoadingProfile.visibility = View.GONE
                                        }
                                    }
                                }
                            }
                        }

                        buttonUpdateDoc.setOnClickListener {
                            openFolderFile()
                        }

                        refreshBtn.setOnClickListener {
                            homeViewModel.getUserDatabyId(user.user_id).observe(this@HomeActivity) { save ->
                                if (save != null) {
                                    when (save) {
                                        is Result.Loading -> {}

                                        is Result.Success -> {
                                            if (save.data.status != user.status) {
                                                homeViewModel.saveSession(UserModel(
                                                    nama = save.data.nama,
                                                    access_token = user.access_token,
                                                    email = save.data.email,
                                                    user_id = save.data.id,
                                                    foto_profile = save.data.fotoProfile,
                                                    role = save.data.role,
                                                    status = save.data.status,
                                                    isLogin = true,
                                                    document = save.data.supportDocument
                                                ))
                                            }

                                            homeViewModel.getListStatus().observe(this@HomeActivity) { status ->
                                                if (status != null) {
                                                    when (status) {
                                                        is Result.Loading -> {
                                                            isLoadingProfile.visibility = View.VISIBLE
                                                        }

                                                        is Result.Success -> {
                                                            status.data.forEach {
                                                                if (it.id == user.status) {
                                                                    statusUser.text = it.status
                                                                    if (it.status == "Pending") {
                                                                        statusUser.setTextColor(getColor(R.color.white))
                                                                        statusUser.setBackgroundColor(getColor(R.color.pendingColor))
                                                                    } else if (it.status == "Unapproved") {
                                                                        statusUser.setTextColor(getColor(R.color.white))
                                                                        statusUser.setBackgroundColor(getColor(R.color.unapprovedColor))
                                                                    } else if (it.status == "Approved") {
                                                                        statusUser.setTextColor(getColor(R.color.white))
                                                                        statusUser.setBackgroundColor(getColor(R.color.approvedColor))
                                                                    }
                                                                }
                                                            }
                                                            isLoadingProfile.visibility = View.GONE
                                                            showToast("Status Updated")
                                                        }

                                                        is Result.Error -> {
                                                            showToast(status.error)
                                                            isLoadingProfile.visibility = View.GONE
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        is Result.Error -> {}
                                    }
                                }
                            }
                        }

                        builder.setView(dialogView)
                        val dialog = builder.create()
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.show()

                        if (user.foto_profile != "none") {
                            Glide.with(this@HomeActivity)
                                .load(user.foto_profile)
                                .transform(CenterCrop(), RoundedCorners(10000))
                                .into(imageEdit)
                        }

                        homeViewModel.getListStatus().observe(this@HomeActivity) { status ->
                            if (status != null) {
                                when (status) {
                                    is Result.Loading -> {
                                        isLoadingProfile.visibility = View.VISIBLE
                                    }

                                    is Result.Success -> {
                                        status.data.forEach {
                                            if (it.id == user.status) {
                                                statusUser.text = it.status
                                                if (it.status == "Pending") {
                                                    statusUser.setTextColor(getColor(R.color.white))
                                                    statusUser.setBackgroundColor(getColor(R.color.pendingColor))
                                                } else if (it.status == "Unapproved") {
                                                    statusUser.setTextColor(getColor(R.color.white))
                                                    statusUser.setBackgroundColor(getColor(R.color.unapprovedColor))
                                                } else if (it.status == "Approved") {
                                                    statusUser.setTextColor(getColor(R.color.white))
                                                    statusUser.setBackgroundColor(getColor(R.color.approvedColor))
                                                }
                                            }
                                        }
                                        isLoadingProfile.visibility = View.GONE
                                    }

                                    is Result.Error -> {
                                        showToast(status.error)
                                        isLoadingProfile.visibility = View.GONE
                                    }
                                }
                            }
                        }

                        usernameEdit.text = user.nama
                        emailView.text = user.email
                        roleView.text = nameRole

                        imageEdit.setOnClickListener {
                            startGallery()

                            runBlocking {
                                delay(2000) // Waits for 2 seconds without blocking
                                val builderConfirm = AlertDialog.Builder(this@HomeActivity)
                                builderConfirm.setTitle("Confirmation")
                                builderConfirm.setMessage("Yakin ingin mengubah?")

                                // Positive button
                                builderConfirm.setPositiveButton("Yakin") { dialog, which ->
                                    // Handle positive button action
                                    if (file != null) {
                                        showToast("Confirmed!")
                                        homeViewModel.uploadPhotoUser(user.user_id, file!!).observe(this@HomeActivity) { result ->
                                            if (result != null) {
                                                when (result) {
                                                    is Result.Loading -> {
                                                        isLoadingProfile.visibility = View.VISIBLE
                                                    }

                                                    is Result.Success -> {
                                                        showToast("Profile Updated")
                                                        homeViewModel.getUserDatabyId(user.user_id).observe(this@HomeActivity) { save ->
                                                            if (save != null) {
                                                                when (save) {
                                                                    is Result.Loading -> {
                                                                        isLoading(true)
                                                                    }

                                                                    is Result.Success -> {
                                                                        homeViewModel.saveSession(UserModel(
                                                                            nama = save.data.nama,
                                                                            access_token = user.access_token,
                                                                            email = save.data.email,
                                                                            user_id = save.data.id,
                                                                            foto_profile = save.data.fotoProfile,
                                                                            role = save.data.role,
                                                                            status = save.data.status,
                                                                            isLogin = true,
                                                                            document = save.data.supportDocument
                                                                        ))

                                                                        Glide.with(this@HomeActivity)
                                                                            .load(save.data.fotoProfile)
                                                                            .transform(CenterCrop(), RoundedCorners(10000))
                                                                            .into(imageEdit)

                                                                        showToast("GET the Image")
                                                                        isLoading(false)
                                                                    }

                                                                    is Result.Error -> {
                                                                        showToast(save.error)
                                                                        isLoading(false)
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        isLoadingProfile.visibility = View.GONE
                                                    }

                                                    is Result.Error -> {
                                                        showToast(result.error)
                                                        isLoadingProfile.visibility = View.GONE
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        showToast("No file selected!")
                                        dialog.dismiss()
                                    }
                                }

                                // Negative button
                                builderConfirm.setNegativeButton("Batal") { dialog, which ->
                                    // Handle negative button action
                                    dialog.dismiss() // Dismiss the dialog
                                }

                                // Show the dialog
                                builderConfirm.create().show()
                            }
                        }

                        usernameEdit.setOnClickListener {
                            val builderConfirm = AlertDialog.Builder(this@HomeActivity)
                            builderConfirm.setTitle("Input New Full Name")

                            val input = EditText(this@HomeActivity)
                            val params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                            )
                            params.setMargins(16, 0, 16, 0)
                            input.layoutParams = params

                            builderConfirm.setView(input)
                            builderConfirm.setPositiveButton("Ubah") { dialog, which ->
                                val userInput = input.text.toString()

                                if (userInput.isNotEmpty()) {
                                    showToast("Confirmed!")
                                    homeViewModel.updateUsername(user.user_id, userInput).observe(this@HomeActivity) { result ->
                                        if (result != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    isLoadingProfile.visibility = View.VISIBLE
                                                }

                                                is Result.Success -> {
                                                    homeViewModel.getUserDatabyId(user.user_id).observe(this@HomeActivity) { save ->
                                                        if (save != null) {
                                                            when (save) {
                                                                is Result.Loading -> {
                                                                    isLoading(true)
                                                                }

                                                                is Result.Success -> {
                                                                    showToast("Profile Updated")
                                                                    homeViewModel.saveSession(UserModel(
                                                                        nama = save.data.nama,
                                                                        access_token = user.access_token,
                                                                        email = save.data.email,
                                                                        user_id = save.data.id,
                                                                        foto_profile = save.data.fotoProfile,
                                                                        role = save.data.role,
                                                                        status = save.data.status,
                                                                        isLogin = true,
                                                                        document = save.data.supportDocument
                                                                    ))

                                                                    usernameEdit.text = save.data.nama

                                                                    isLoading(false)
                                                                }

                                                                is Result.Error -> {
                                                                    showToast(save.error)
                                                                    isLoading(false)
                                                                }
                                                            }
                                                        }
                                                    }

                                                    isLoadingProfile.visibility = View.GONE
                                                }

                                                is Result.Error -> {
                                                    showToast(result.error)
                                                    isLoadingProfile.visibility = View.GONE
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    showToast("Input Something!")
                                }
                            }

                            // Negative button
                            builderConfirm.setNegativeButton("Batal") { dialog, which ->
                                // Handle negative button action
                                dialog.dismiss() // Dismiss the dialog
                            }


                            builderConfirm.create().show()
                        }

                        logoutButton.setOnClickListener {
                            homeViewModel.logoutUser()
                            dialog.dismiss()
                            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    }
                } else {
                    val intentMain = Intent(this@HomeActivity, LoginActivity::class.java)
                    intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intentMain)
                    finish()
                }
            }

            homeViewModel.getAllGamelanBali().observe(this@HomeActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            adapter = HomeAdapter(this@HomeActivity)
                            binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@HomeActivity,  LinearLayoutManager.HORIZONTAL, false)
                            binding.rvGamelanBaliHome.setHasFixedSize(true)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                            binding.rvGamelanBaliHome.addItemDecoration(CustomItemDecoration(spacingInPixels, false))
                            binding.rvGamelanBaliHome.adapter = adapter

                            dataSetApproved.clear()
                            if (fullShowOrNot) {
                                result.data.forEach {
                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                        dataSetApproved.add(it)
                                    }
                                }
                                adapter.setListGamelan(dataSetApproved)
                            } else {
                                adapter.setListGamelan(result.data)
                            }
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            if (result.error == "Sorry, {\"detail\":\"Token Invalid!\"}") {
                                homeViewModel.logoutUser()
                                val intentMain = Intent(this@HomeActivity, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intentMain)
                                finish()
                            }
                            isLoading(false)
                        }
                    }
                }
            }

            homeViewModel.getAllInstrumentBali().observe(this@HomeActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            adapterInstrument = InstrumentAdapter(this@HomeActivity)
                            binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                            binding.rvInstrumenHome.setHasFixedSize(true)
                            binding.rvInstrumenHome.adapter = adapterInstrument

                            dataSetApprovedInstrument.clear()
                            if (fullShowOrNot) {
                                result.data.forEach {
                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                        dataSetApprovedInstrument.add(it)
                                    }
                                }
                                adapterInstrument.setListInstrument(dataSetApprovedInstrument.take(4))
                            } else {
                                adapterInstrument.setListInstrument(result.data.take(4))
                            }
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            isLoading(false)
                        }
                    }
                }
            }

            homeViewModel.getAllSanggarBali().observe(this@HomeActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            adapterSanggar = SanggarAdapter(this@HomeActivity)
                            binding.rvSanggarHome.layoutManager = LinearLayoutManager(this@HomeActivity,  LinearLayoutManager.HORIZONTAL, false)
                            binding.rvSanggarHome.setHasFixedSize(true)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                            binding.rvSanggarHome.addItemDecoration(CustomItemDecoration(spacingInPixels, false))
                            binding.rvSanggarHome.adapter = adapterSanggar

                            dataSetApprovedSanggar.clear()
                            if (fullShowOrNot) {
                                result.data.forEach {
                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                        dataSetApprovedSanggar.add(it)
                                    }
                                }
                                adapterSanggar.setListSanggar(dataSetApprovedSanggar)
                            } else {
                                adapterSanggar.setListSanggar(result.data)
                            }
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            isLoading(false)
                        }
                    }
                }
            }

            binding.searchViewGamelan.setupWithSearchBar(binding.searchBarGamelan)

            binding.searchViewGamelan
                .editText
                .setOnEditorActionListener{ textView, actionId, event ->
                    binding.searchViewGamelan.hide()
                    val value = binding.searchViewGamelan.text.toString()
                    homeViewModel.getSearchAllGamelanInstrument(value).observe(this@HomeActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)
                                    adapterInstrument = InstrumentAdapter(this@HomeActivity)
                                    binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                                    binding.rvInstrumenHome.setHasFixedSize(true)
                                    binding.rvInstrumenHome.adapter = adapterInstrument

                                    adapter = HomeAdapter(this@HomeActivity)
                                    binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@HomeActivity,  LinearLayoutManager.HORIZONTAL, false)
                                    binding.rvGamelanBaliHome.setHasFixedSize(true)
                                    binding.rvGamelanBaliHome.adapter = adapter

                                    dataSetApproved.clear()
                                    dataSetApprovedInstrument.clear()
                                    if (fullShowOrNot) {
                                        result.data.gamelanData.forEach {
                                            if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                dataSetApproved.add(it)
                                            }
                                        }
                                        result.data.instrumentData.forEach { instrumentStatus ->
                                            if (instrumentStatus.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                dataSetApprovedInstrument.add(instrumentStatus)
                                            }
                                        }
                                        adapter.setListGamelan(dataSetApproved)
                                        adapterInstrument.setListInstrument(dataSetApprovedInstrument)
                                    } else {
                                        adapter.setListGamelan(result.data.gamelanData)
                                        adapterInstrument.setListInstrument(result.data.instrumentData)
                                    }
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

            binding.scrolling.setOnRefreshListener {
                homeViewModel.getSessionUser().observe(this@HomeActivity) { user ->
                    if (user.isLogin) {
                        binding.usernameTv.text = user.nama

                        if (user.foto_profile != "none") {
                            Glide.with(this@HomeActivity)
                                .load(user.foto_profile)
                                .transform(CenterCrop(), RoundedCorners(10000))
                                .into(binding.profileImage)
                        }

                        homeViewModel.getUserDatabyId(user.user_id).observe(this@HomeActivity) { save ->
                            if (save != null) {
                                when (save) {
                                    is Result.Loading -> {
                                        isLoading(false)
                                    }

                                    is Result.Success -> {
                                        if (save.data.status != user.status) {
                                            homeViewModel.saveSession(UserModel(
                                                nama = save.data.nama,
                                                access_token = user.access_token,
                                                email = save.data.email,
                                                user_id = save.data.id,
                                                foto_profile = save.data.fotoProfile,
                                                role = save.data.role,
                                                status = save.data.status,
                                                isLogin = true,
                                                document = save.data.supportDocument
                                            ))
                                            showToast("Status Updated")
                                            isLoading(false)
                                        }
                                    }

                                    is Result.Error -> {
                                        showToast(save.error)
                                        isLoading(false)
                                    }
                                }
                            }
                        }
                    } else {
                        val intentMain = Intent(this@HomeActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentMain)
                        finish()
                    }
                }

                homeViewModel.getAllGamelanBali().observe(this@HomeActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast("Data Loaded")
                                isLoading(false)

                                adapter = HomeAdapter(this@HomeActivity)
                                binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@HomeActivity,  LinearLayoutManager.HORIZONTAL, false)
                                binding.rvGamelanBaliHome.setHasFixedSize(true)
                                binding.rvGamelanBaliHome.adapter = adapter

                                dataSetApproved.clear()
                                if (fullShowOrNot) {
                                    result.data.forEach {
                                        if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                            dataSetApproved.add(it)
                                        }
                                    }
                                    adapter.setListGamelan(dataSetApproved)
                                } else {
                                    adapter.setListGamelan(result.data)
                                }
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                if (result.error == "Sorry, {\"detail\":\"Token Invalid!\"}") {
                                    homeViewModel.logoutUser()
                                    val intentMain = Intent(this@HomeActivity, LoginActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intentMain)
                                    finish()
                                }
                                isLoading(false)
                            }
                        }
                    }
                }

                homeViewModel.getAllInstrumentBali().observe(this@HomeActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast("Data Loaded")
                                isLoading(false)

                                adapterInstrument = InstrumentAdapter(this@HomeActivity)
                                binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                                binding.rvInstrumenHome.setHasFixedSize(true)
                                binding.rvInstrumenHome.adapter = adapterInstrument

                                dataSetApprovedInstrument.clear()
                                if (fullShowOrNot) {
                                    result.data.forEach {
                                        if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                            dataSetApprovedInstrument.add(it)
                                        }
                                    }
                                    adapterInstrument.setListInstrument(dataSetApprovedInstrument.take(4))
                                } else {
                                    adapterInstrument.setListInstrument(result.data.take(4))
                                }
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                isLoading(false)
                            }
                        }
                    }
                }

                homeViewModel.getAllSanggarBali().observe(this@HomeActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast("Data Loaded")
                                isLoading(false)

                                adapterSanggar = SanggarAdapter(this@HomeActivity)
                                binding.rvSanggarHome.layoutManager = LinearLayoutManager(this@HomeActivity,  LinearLayoutManager.HORIZONTAL, false)
                                binding.rvSanggarHome.setHasFixedSize(true)
                                binding.rvSanggarHome.adapter = adapterSanggar

                                dataSetApprovedSanggar.clear()
                                if (fullShowOrNot) {
                                    result.data.forEach {
                                        if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                            dataSetApprovedSanggar.add(it)
                                        }
                                    }
                                    adapterSanggar.setListSanggar(dataSetApprovedSanggar)
                                } else {
                                    adapterSanggar.setListSanggar(result.data)
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
        }

        supportActionBar?.hide()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            file = uriToFile(currentImageUri!!, this)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun getAllRoleList() {
        homeViewModel.getAllRoleList().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        isLoading(false)
                        result.data.forEach {
                            if (it.id == roleId) {
                                nameRole = it.role
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

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, "temp_file_${System.currentTimeMillis()}")

        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }

    override fun repeatFunction() {
        lifecycleScope.launch {
            homeViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@HomeActivity).create(HomeViewModel::class.java)
            }
            homeViewModel.getSessionUser().observe(this@HomeActivity) { user ->
                if (user.isLogin) {
                    binding.usernameTv.text = user.nama

                    if (user.foto_profile != "none") {
                        Glide.with(this@HomeActivity)
                            .load(user.foto_profile)
                            .transform(CenterCrop(), RoundedCorners(10000))
                            .into(binding.profileImage)
                    }

                    homeViewModel.getUserDatabyId(user.user_id).observe(this@HomeActivity) { save ->
                        if (save != null) {
                            when (save) {
                                is Result.Loading -> { }

                                is Result.Success -> {
                                    if (save.data.status != user.status) {
                                        homeViewModel.saveSession(UserModel(
                                            nama = save.data.nama,
                                            access_token = user.access_token,
                                            email = save.data.email,
                                            user_id = save.data.id,
                                            foto_profile = save.data.fotoProfile,
                                            role = save.data.role,
                                            status = save.data.status,
                                            isLogin = true,
                                            document = save.data.supportDocument
                                        ))
                                    }
                                }

                                is Result.Error -> {
                                    showToast(save.error)
                                }
                            }
                        }
                    }
                } else {
                    val intentMain = Intent(this@HomeActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentMain)
                    finish()
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

