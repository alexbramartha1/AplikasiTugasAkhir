package com.reviling.filamentandroid.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
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
import com.reviling.filamentandroid.ui.CustomItemDecoration
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.adapter.SanggarAdapter
import com.reviling.filamentandroid.ui.login.LoginActivity
import com.reviling.filamentandroid.ui.login.LoginViewModel
import com.reviling.filamentandroid.ui.seeallgamelan.SeeAllGamelanBaliActivity
import com.reviling.filamentandroid.ui.seeallinstrument.DetailSeeAllInstrumentActivity
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: HomeAdapter
    private lateinit var adapterInstrument: InstrumentAdapter
    private lateinit var adapterSanggar: SanggarAdapter
    private lateinit var homeViewModel: HomeViewModel
    private var currentImageUri: Uri? = null
    private var file: File? = null
    private var roleId: String? = null
    private var nameRole: String? = null

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

            homeViewModel.getSessionUser().observe(this@HomeActivity) { user ->
                if (user.isLogin) {
                    binding.usernameTv.text = user.nama

                    if (user.foto_profile != "none") {
                        Glide.with(this@HomeActivity)
                            .load(user.foto_profile)
                            .transform(CenterCrop(), RoundedCorners(500))
                            .into(binding.profileImage)
                    }

                    roleId = user.role
                    getAllRoleList()

                    binding.profileImage.setOnClickListener {
                        val builder = AlertDialog.Builder(this@HomeActivity)
                        val inflater = layoutInflater
                        val dialogView = inflater.inflate(R.layout.fragment_profile, null)

                        val imageEdit = dialogView.findViewById<ImageView>(R.id.image_edit)
                        val usernameEdit = dialogView.findViewById<TextView>(R.id.username_edit)
                        val logoutButton = dialogView.findViewById<Button>(R.id.logoutbutton)
                        val loadingView = dialogView.findViewById<ProgressBar>(R.id.progress_bar_profile)
                        val emailView = dialogView.findViewById<TextView>(R.id.email_pengguna)
                        val roleView = dialogView.findViewById<TextView>(R.id.roleView)

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
                                                        loadingView.visibility = View.VISIBLE
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
                                                                            isLogin = true
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

                                                        loadingView.visibility = View.GONE
                                                    }

                                                    is Result.Error -> {
                                                        showToast(result.error)
                                                        loadingView.visibility = View.GONE
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
                            builderConfirm.setTitle("Input Username")

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
                                                    loadingView.visibility = View.VISIBLE
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
                                                                        isLogin = true
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

                                                    loadingView.visibility = View.GONE
                                                }

                                                is Result.Error -> {
                                                    showToast(result.error)
                                                    loadingView.visibility = View.GONE
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
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
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
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                            binding.rvGamelanBaliHome.addItemDecoration(CustomItemDecoration(spacingInPixels, false))
                            binding.rvGamelanBaliHome.adapter = adapter

                            adapter.setListGamelan(result.data)
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
                            adapterInstrument.setListInstrument(result.data.take(4))
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
                            adapterSanggar.setListSanggar(result.data)
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
                                    adapterInstrument.setListInstrument(result.data.instrumentData)

                                    adapter = HomeAdapter(this@HomeActivity)
                                    binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@HomeActivity,  LinearLayoutManager.HORIZONTAL, false)
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
                    Log.d("IsiSearchView", binding.searchViewGamelan.text.toString())
                    false
                }
        }

        binding.scrolling.setOnRefreshListener {
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
                                adapter.setListGamelan(result.data)
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
                                adapterInstrument.setListInstrument(result.data.take(4))
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
                                adapterSanggar.setListSanggar(result.data)
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                isLoading(false)
                            }
                        }
                    }
                }
            }
            binding.scrolling.isRefreshing = false
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

