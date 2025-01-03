package com.reviling.filamentandroid.ui.inputsanggar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.search.SearchBar
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.databinding.ActivityInputDataSanggarBinding
import com.reviling.filamentandroid.ui.adapter.HomeAdapter
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.home.HomeActivity
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class InputDataSanggarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputDataSanggarBinding
    private lateinit var inputDataSanggarViewModel: InputDataSanggarViewModel
    private var userId = ""
    private var currentImageUri: Uri? = null
    private var file: File? = null
    private var idSanggar: String? = null
    private var fileFromEdit: String? = null

    private var namaSanggarBefore: String? = null
    private var noTeleponBefore: String? = null
    private var namaJalanBefore: String? = null
    private var namaDesaBefore: String? = null
    private var namaKecamatanBefore: String? = null
    private var namaKabupatenBefore: String? = null
    private var namaProvinsiBefore: String? = null
    private var namaKodePosBefore: String? = null
    private var namaDeskripsiBefore: String? = null
    private var namaAlamatBefore: String? = null
    private var idDesaBefore: String? = null
    private var selectedIdGamelanBefore: MutableList<String> = mutableListOf()
    private var selectedIdGamelanBeforeReal: MutableList<String> = mutableListOf()

    private val itemsGamelan = mutableListOf<String>()
    private val idGamelan: MutableList<String> = mutableListOf()
    private var flagsGamelan: MutableList<Int> = mutableListOf()
    private var selectedIdGamelan: MutableList<String> = mutableListOf()
    private var selectedNameGamelan: MutableList<String?> = mutableListOf()

    private val items = mutableListOf<String>()
    private val idKab = mutableListOf<String>()
    private var flagsKabupaten: Int? = null
    private var idKabupaten: String? = null
    private var namaKabupatenPick: String? = null

    private val itemsKec = mutableListOf<String>()
    private val idKec = mutableListOf<String>()
    private var flagskecamatan: Int? = null
    private var idKecamatan: String? = null
    private var namaKecamatanPick: String? = null

    private val itemsDesa = mutableListOf<String>()
    private val idDes = mutableListOf<String>()
    private var flagsdesa: Int? = null
    private var idDesa: String? = null
    private var namaDesaPick: String? = null

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->

        if (uri != null) {
            currentImageUri = uri
            file = uriToFile(currentImageUri!!, this)
        }

        if (file != null) {
            binding.imageView.text = getString(R.string.gambar_added)
        } else {
            binding.imageView.text = getString(R.string.upload_gambar_sanggar)
        }

        if (fileFromEdit != null) {
            binding.imageView.text = getString(R.string.gambar_added)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataSanggarBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.

            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        lifecycleScope.launch {
            inputDataSanggarViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@InputDataSanggarActivity).create(InputDataSanggarViewModel::class.java)
            }

            getListGamelan()

            binding.chooseGamelan.setOnClickListener {
                var selectedItem: MutableList<Int> = mutableListOf()

                if (idSanggar != null) {
                    val trimmedIdBefore: MutableList<String> = mutableListOf()

                    selectedIdGamelanBefore.forEach {
                        trimmedIdBefore.add(it.replace("\"", ""))
                    }

                    Log.d("IsiDariSelectedIdBefore", "$trimmedIdBefore")
                    Log.d("IsiDariSelectedId", "$idGamelan")
                    trimmedIdBefore.forEach { idList ->
                        val index = idGamelan.indexOf(idList)
                        Log.d("IsiDariSelectedIndex", index.toString())
                        if (index != -1) {
                            selectedItem.add(index)
                        }
                    }
                }

                if (flagsGamelan.isNotEmpty()) {
                    selectedItem = flagsGamelan
                }

                val dialogView = layoutInflater.inflate(R.layout.dialog_list_alamat, null)
                val listView: ListView = dialogView.findViewById(R.id.listView)
                val buttonPilih: Button = dialogView.findViewById(R.id.pilihkabbtn)
                val buttonBatal: Button = dialogView.findViewById(R.id.batalkabbtn)
                val headerView: TextView = dialogView.findViewById(R.id.title_alamat)

                val adapter = ArrayAdapter(this@InputDataSanggarActivity, android.R.layout.simple_list_item_single_choice, itemsGamelan)
                listView.adapter = adapter

                listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                Log.d("IsiSelectedItem", selectedItem.toString())
                selectedItem.forEach {
                    listView.setItemChecked(it, true)
                }

                val builder = AlertDialog.Builder(this@InputDataSanggarActivity)
                builder
                    .setView(dialogView)

                val dialog = builder.create()
                dialog.show()
                headerView.setText(R.string.pilih_gamelan)
                buttonPilih.setOnClickListener {
                    flagsGamelan.clear()
                    selectedIdGamelan.clear()
                    selectedIdGamelanBefore.clear()
                    selectedNameGamelan.clear()
                    for (i in 0 until listView.count) {
                        if (listView.isItemChecked(i)) {
                            flagsGamelan.add(i)
                            selectedIdGamelan.add(idGamelan[i].replace("\"", ""))
                            selectedNameGamelan.add(adapter.getItem(i))
                        }
                    }

                    if (selectedIdGamelan.isNotEmpty()) {
                        binding.chooseGamelan.setText(R.string.gamelan_added)
                    } else {
                        binding.chooseGamelan.setText(R.string.pilih_gamelan)
                    }

                    dialog.dismiss()
                }

                buttonBatal.setOnClickListener { dialog.dismiss() }
            }

            inputDataSanggarViewModel.getSessionUser().observe(this@InputDataSanggarActivity) { user ->
                if (user.isLogin) {
                    userId = user.user_id
                }
            }

            idSanggar = intent.getStringExtra(ID)
            idSanggar?.let { Log.d("IdSanggarValue", it) }

            inputDataSanggarViewModel.getListKabupaten().observe(this@InputDataSanggarActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.pickKabupaten.isEnabled = false
                        binding.pickKecamatan.isEnabled = false
                        binding.pickDesa.isEnabled = false
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        binding.pickKabupaten.isEnabled = false
                        binding.pickKecamatan.isEnabled = false
                        binding.pickDesa.isEnabled = false
                        result.data.forEach {
                            items.add(it.namaKabupaten)
                            idKab.add(it.id)
                        }
                        binding.pickKabupaten.isEnabled = true

                        binding.pickKabupaten.setOnClickListener {
                            var selectedItem = 0

                            if (flagsKabupaten != null) {
                                selectedItem = flagsKabupaten as Int
                            }

                            Log.d("IsiDariItems", items.toString())


                            val dialogView = layoutInflater.inflate(R.layout.dialog_list_alamat, null)
                            val listView: ListView = dialogView.findViewById(R.id.listView)
                            val buttonPilih: Button = dialogView.findViewById(R.id.pilihkabbtn)
                            val buttonBatal: Button = dialogView.findViewById(R.id.batalkabbtn)
                            val headerView: TextView = dialogView.findViewById(R.id.title_alamat)

                            val adapter = ArrayAdapter(this@InputDataSanggarActivity, android.R.layout.simple_list_item_single_choice, items)
                            listView.adapter = adapter

                            listView.choiceMode = ListView.CHOICE_MODE_SINGLE
                            listView.setItemChecked(selectedItem, true)

                            val builder = AlertDialog.Builder(this@InputDataSanggarActivity)
                            builder
                                .setView(dialogView)

                            val dialog = builder.create()
                            dialog.show()
                            headerView.setText(R.string.pilih_kabupaten)
                            buttonPilih.setOnClickListener {
                                val selectedPosition = listView.checkedItemPosition
                                binding.pickKabupaten.text = adapter.getItem(selectedPosition)
                                flagsKabupaten = selectedPosition
                                idKabupaten = idKab[selectedPosition]
                                namaKabupatenPick = adapter.getItem(selectedPosition)
                                inputDataSanggarViewModel.getListKecamatan(idKabupaten.toString()).observe(this@InputDataSanggarActivity) { result ->
                                    if (result != null) {
                                        when (result) {
                                            is Result.Loading -> {
                                                binding.pickKecamatan.isEnabled = false
                                                binding.pickDesa.isEnabled = false
                                                isLoading(true)
                                            }

                                            is Result.Success -> {
                                                isLoading(false)
                                                binding.pickKecamatan.isEnabled = false
                                                binding.pickDesa.isEnabled = false

                                                if (itemsKec != null && idKec != null) {
                                                    itemsKec.clear()
                                                    idKec.clear()
                                                    binding.pickKecamatan.setText(R.string.pilih_kecamatan)

                                                    itemsDesa.clear()
                                                    idDes.clear()
                                                    binding.pickDesa.setText(R.string.pilih_desa)

                                                    namaKecamatanPick = null
                                                    namaDesaPick = null

                                                    result.data.forEach {
                                                        itemsKec.add(it.namaKecamatan)
                                                        idKec.add(it.id)
                                                    }
                                                } else {
                                                    result.data.forEach {
                                                        itemsKec.add(it.namaKecamatan)
                                                        idKec.add(it.id)
                                                    }
                                                }
                                                binding.pickKecamatan.isEnabled = true
                                            }

                                            is Result.Error -> {
                                                showToast(result.error)
                                                binding.pickKecamatan.isEnabled = false
                                                binding.pickDesa.isEnabled = false
                                                isLoading(false)
                                            }
                                        }
                                    }
                                }

                                dialog.dismiss()
                            }

                            buttonBatal.setOnClickListener { dialog.dismiss() }
                        }

                        isLoading(false)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        binding.pickKabupaten.isEnabled = false
                        binding.pickKecamatan.isEnabled = false
                        binding.pickDesa.isEnabled = false
                        isLoading(false)
                    }
                }
            }

            binding.pickKecamatan.setOnClickListener {
                dialogBuildingPickKecamatan(itemsKec)
            }

            binding.pickDesa.setOnClickListener {
                dialogBuildingPickDesa(itemsDesa)
            }

            if (idSanggar != null) {
                inputDataSanggarViewModel.getDetailSanggarById(idSanggar!!).observe(this@InputDataSanggarActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast("Data Loaded")
                                isLoading(false)
                                binding.pickKabupaten.isEnabled = true
                                binding.pickKecamatan.isEnabled = true
                                binding.pickDesa.isEnabled = true
                                inputDataSanggarViewModel.getListAllAlamatByIdDesa(result.data.sanggarData[0].idDesa).observe(this@InputDataSanggarActivity) { resultAlamat ->
                                    if (resultAlamat != null) {
                                        when (resultAlamat) {
                                            is Result.Loading -> {
                                                isLoading(true)
                                            }

                                            is Result.Success -> {
                                                showToast("Data Loaded")
                                                isLoading(false)

                                                resultAlamat.data.desaData.forEach {
                                                    itemsDesa.add(it.namaDesa)
                                                    idDes.add(it.id)
                                                }

                                                resultAlamat.data.kecamatanData.forEach {
                                                    itemsKec.add(it.namaKecamatan)
                                                    idKec.add(it.id)
                                                }
                                            }

                                            is Result.Error -> {
                                                showToast(resultAlamat.error)
                                                isLoading(false)
                                            }
                                        }
                                    }
                                }

                                val valuebeforeidgamelan = result.data.sanggarData[0].gamelanId
                                valuebeforeidgamelan.forEach {
                                    selectedIdGamelanBefore.add(it.replace("\"", ""))
                                    selectedIdGamelanBeforeReal.add(it.replace("\"", ""))
                                }
                                namaKabupatenPick = result.data.sanggarData[0].kabupaten
                                namaKecamatanPick = result.data.sanggarData[0].kecamatan
                                namaDesaPick = result.data.sanggarData[0].desa
                                namaSanggarBefore = result.data.sanggarData[0].namaSanggar
                                noTeleponBefore = result.data.sanggarData[0].noTelepon
                                namaJalanBefore = result.data.sanggarData[0].namaJalan
                                namaDesaBefore = result.data.sanggarData[0].desa
                                namaKecamatanBefore = result.data.sanggarData[0].kecamatan
                                namaKabupatenBefore = result.data.sanggarData[0].kabupaten
                                namaProvinsiBefore = result.data.sanggarData[0].provinsi
                                namaKodePosBefore = result.data.sanggarData[0].kodePos
                                namaDeskripsiBefore = result.data.sanggarData[0].deskripsi
                                namaAlamatBefore = result.data.sanggarData[0].alamatLengkap
                                binding.edNamaSanggar.setText(result.data.sanggarData[0].namaSanggar)
                                binding.edNoTelepon.setText(result.data.sanggarData[0].noTelepon)
                                binding.edNamaJalan.setText(result.data.sanggarData[0].namaJalan)
                                binding.pickDesa.text = result.data.sanggarData[0].desa
                                binding.pickKecamatan.text = result.data.sanggarData[0].kecamatan
                                binding.pickKabupaten.text = result.data.sanggarData[0].kabupaten
                                binding.pickProvinsi.text = result.data.sanggarData[0].provinsi
                                binding.edKodePos.setText(result.data.sanggarData[0].kodePos)
                                binding.edDeskripsi.setText(result.data.sanggarData[0].deskripsi)
                                binding.imageView.text = getString(R.string.gambar_added)
                                idDesaBefore = result.data.sanggarData[0].idDesa
                                fileFromEdit = result.data.sanggarData[0].image

                                if (selectedIdGamelanBefore.isNotEmpty()) {
                                    binding.chooseGamelan.setText(R.string.gamelan_added)
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

            binding.cardView.setOnClickListener {
                startGallery()
                Log.d("isicurrentImageUri", currentImageUri.toString())
            }

            binding.upload.setOnClickListener {
                var namaSanggar = binding.edNamaSanggar.text.toString()
                var noTelepon = binding.edNoTelepon.text.toString()
                var namaJalan = binding.edNamaJalan.text.toString()
                var kodePos = binding.edKodePos.text.toString()
                var deskripsi = binding.edDeskripsi.text.toString()
                var fileImage = file

                if (idSanggar == null) {
                    Log.d("isidarifile", fileImage.toString())
                    if (namaSanggar.isEmpty()) {
                        binding.edNamaSanggar.error = getString(R.string.cannot_empty)
                    } else if (noTelepon.isEmpty()) {
                        binding.edNoTelepon.error = getString(R.string.cannot_empty)
                    } else if (namaJalan.isEmpty()) {
                        binding.edNamaJalan.error = getString(R.string.cannot_empty)
                    } else if (kodePos.isEmpty()) {
                        binding.edKodePos.error = getString(R.string.cannot_empty)
                    } else if (deskripsi.isEmpty()) {
                        binding.edDeskripsi.error = getString(R.string.cannot_empty)
                    } else if (fileImage == null) {
                        showToast(getString(R.string.cannot_empty))
                    } else if (idDesa == null){
                        showToast(getString(R.string.cannot_field))
                    } else if (selectedIdGamelan.isEmpty()) {
                        showToast(getString(R.string.cannot_field))
                    } else {
                        selectedIdGamelan.forEach { it.replace("\"", "") }
                        inputDataSanggarViewModel.createSanggarData(
                            fileImage = fileImage,
                            namaSanggar = namaSanggar,
                            noTelepon = noTelepon,
                            namaJalan = namaJalan,
                            kodePost = kodePos,
                            deskripsi = deskripsi,
                            idDesa = idDesa!!,
                            gamelanId = selectedIdGamelan
                        ).observe(this@InputDataSanggarActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        isLoading(true)
                                    }

                                    is Result.Success -> {
                                        showToast(result.data)
                                        isLoading(false)
                                        onBackPressed()
                                    }

                                    is Result.Error -> {
                                        showToast(result.error)
                                        isLoading(false)
                                    }
                                }
                            }
                        }
                    }
                } else {

                    if (namaJalan == namaJalanBefore) {
                        namaJalan = ""
                    }

                    if (kodePos == namaKodePosBefore) {
                        kodePos = ""
                    }

                    if (idDesa == idDesaBefore) {
                        idDesa = ""
                    }

                    if (selectedIdGamelan == selectedIdGamelanBeforeReal) {
                        selectedIdGamelan = mutableListOf()
                    }

                    if (namaSanggar == namaSanggarBefore) {
                        namaSanggar = ""
                    }

                    if (noTelepon == noTeleponBefore) {
                        noTelepon = ""
                    }

                    if (deskripsi == namaDeskripsiBefore) {
                        deskripsi = ""
                    }

                    if (fileImage == null) {
                        fileImage = null
                    }

                    selectedIdGamelan.forEach { it.replace("\"", "") }
                    Log.d("IsiDariSEelctedgfa", selectedIdGamelan.toString())
                    inputDataSanggarViewModel.updateSanggarData(
                        id = idSanggar!!,
                        fileImage = fileImage,
                        namaSanggar = namaSanggar,
                        noTelepon = noTelepon,
                        namaJalan = namaJalan,
                        kodePost = kodePos,
                        deskripsi = deskripsi,
                        idDesa = idDesa,
                        gamelanId = selectedIdGamelan
                    ).observe(this@InputDataSanggarActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast(result.data.message.toString())
                                    isLoading(false)
                                    onBackPressed()
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    isLoading(false)
                                }
                            }
                        }
                    }
                }
            }
        }

        supportActionBar?.hide()
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun getListGamelan() {
        inputDataSanggarViewModel.seeAllGamelanBali().observe(this@InputDataSanggarActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        result.data.forEach {
                            itemsGamelan.add(it.namaGamelan)
                            idGamelan.add(it.id)
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

    private fun dialogBuildingPickKecamatan(items: List<String>) {
        var selectedItem = 0

        if (flagskecamatan != null) {
            selectedItem = flagskecamatan as Int
        }

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_list_alamat,
            null
        )
        val listView: ListView =
            dialogView.findViewById(R.id.listView)
        val buttonPilih: Button =
            dialogView.findViewById(R.id.pilihkabbtn)
        val buttonBatal: Button =
            dialogView.findViewById(R.id.batalkabbtn)
        val headerView: TextView =
            dialogView.findViewById(R.id.title_alamat)

        val adapterKecamatan = ArrayAdapter(
            this@InputDataSanggarActivity,
            android.R.layout.simple_list_item_single_choice,
            items
        )

        listView.adapter = adapterKecamatan

        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.setItemChecked(selectedItem, true)

        val builder =
            AlertDialog.Builder(this@InputDataSanggarActivity)
        builder
            .setView(dialogView) // Set view kustom yang sudah dibuat

        val dialog = builder.create()
        dialog.show()
        headerView.setText(R.string.pilih_kecamatan)
        buttonPilih.setOnClickListener {
            if (namaKabupatenPick != null && items.isNotEmpty()) {
                val selectedPosition =
                    listView.checkedItemPosition
                Log.d("IsiSelectedIfNull", selectedPosition.toString())

                binding.pickKecamatan.text =
                    adapterKecamatan.getItem(selectedPosition)
                namaKecamatanPick = adapterKecamatan.getItem(selectedPosition)
                flagskecamatan = selectedPosition
                idKecamatan = idKec[selectedPosition]

                inputDataSanggarViewModel.getListDesa(idKecamatan.toString()).observe(this@InputDataSanggarActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.pickDesa.isEnabled = false
                                isLoading(true)
                            }

                            is Result.Success -> {
                                isLoading(false)
                                binding.pickDesa.isEnabled = false
                                if (itemsDesa != null && idDes != null) {
                                    itemsDesa.clear()
                                    idDes.clear()
                                    binding.pickDesa.setText(R.string.pilih_desa)
                                    result.data.forEach {
                                        itemsDesa.add(it.namaDesa)
                                        idDes.add(it.id)
                                    }
                                } else {
                                    result.data.forEach {
                                        itemsDesa.add(it.namaDesa)
                                        idDes.add(it.id)
                                    }
                                }
                                binding.pickDesa.isEnabled = true
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                binding.pickDesa.isEnabled = false
                                isLoading(false)
                            }
                        }
                    }
                }
                dialog.dismiss()
            }

            dialog.dismiss()
        }

        buttonBatal.setOnClickListener { dialog.dismiss() }
    }

    private fun dialogBuildingPickDesa(items: List<String>) {
        var selectedItem = 0

        if (flagsdesa != null) {
            selectedItem = flagsdesa as Int
        }

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_list_alamat,
            null
        )
        val listView: ListView =
            dialogView.findViewById(R.id.listView)
        val buttonPilih: Button =
            dialogView.findViewById(R.id.pilihkabbtn)
        val buttonBatal: Button =
            dialogView.findViewById(R.id.batalkabbtn)
        val headerView: TextView =
            dialogView.findViewById(R.id.title_alamat)

        val adapterDesa = ArrayAdapter(
            this@InputDataSanggarActivity,
            android.R.layout.simple_list_item_single_choice,
            items
        )

        listView.adapter = adapterDesa

        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.setItemChecked(selectedItem, true)

        val builder =
            AlertDialog.Builder(this@InputDataSanggarActivity)
        builder
            .setView(dialogView) // Set view kustom yang sudah dibuat

        val dialog = builder.create()
        dialog.show()

        headerView.setText(R.string.pilih_desa)
        buttonPilih.setOnClickListener {
            if (namaKecamatanPick != null && items.isNotEmpty()) {
                val selectedPosition =
                    listView.checkedItemPosition

                binding.pickDesa.text =
                    adapterDesa.getItem(selectedPosition)
                namaDesaPick = adapterDesa.getItem(selectedPosition)
                flagsdesa = selectedPosition
                idDesa = idDes[selectedPosition]
                dialog.dismiss()
            }
            dialog.dismiss()
        }

        buttonBatal.setOnClickListener { dialog.dismiss() }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val ID = "fromBtnEdit"
    }
}