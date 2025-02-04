package com.reviling.filamentandroid.ui.inputinstrument

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.databinding.ActivityInputInstrumentBinding
import com.reviling.filamentandroid.ui.adapter.InputMaterialAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class InputInstrumentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputInstrumentBinding
    private lateinit var inputInstrumentViewModel: InputInstrumentViewModel
    private var itemsMaterial: MutableList<String> = mutableListOf()
    private lateinit var adapterMaterial: InputMaterialAdapter
    private var file: File? = null
    private var file2: File? = null
    private var currentImageUri: Uri? = null
    private var flagsBtnImage: Int = 0
    private var listImageFile: MutableList<File> = mutableListOf()
    private var fileTridi: File? = null

    private var namaInstrumenBefore: String? = null
    private var fungsiInstrumenBefore: String? = null
    private var deskripsiInstrumenBefore: String? = null
    private var itemsMaterialBefore: MutableList<String> = mutableListOf()
    private var flagsEdittedIndexPhoto: String? = null
    private var fileTridiBefore: String? = null
    private var listImageBefore: List<String> = emptyList()

    private var firstImage: String? = null
    private var secondImage: String? = null

    private var idInstrument: String? = null

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->

        if (flagsBtnImage == 1) {
            if (uri != null) {
                currentImageUri = uri
                file = uriToFile(currentImageUri!!, this)
                flagsEdittedIndexPhoto = "0"
            }
        } else if (flagsBtnImage == 2) {
            if (uri != null) {
                currentImageUri = uri
                file2 = uriToFile(currentImageUri!!, this)
                flagsEdittedIndexPhoto = "1"
            }
        }

        if (file != null) {
            binding.inputFirstFoto.text = getString(R.string.gambar_added)
        } else {
            binding.inputFirstFoto.text = getString(R.string.tambahkan_gambar_preview)
        }

        if (file2 != null) {
            binding.inputSecondFoto.text = getString(R.string.gambar_added)
        } else {
            binding.inputSecondFoto.text = getString(R.string.tambahkan_gambar_bagian_dimainkan)
        }

        if (firstImage != null) {
            binding.inputFirstFoto.text = getString(R.string.gambar_added)
        }

        if (secondImage != null) {
            binding.inputSecondFoto.text = getString(R.string.gambar_added)
        }
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

    private val requestFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val fileName = uriToName(uri, this)

                    if (fileName.endsWith(".glb", ignoreCase = true)) {
                        fileTridi = uriToFile(uri, this)

                        binding.inputTridiView.text = getString(R.string.gambar_added)

                    } else {
                        if (fileTridi != null) {
                            binding.inputTridiView.text = getString(R.string.gambar_added)
                        } else {
                            binding.inputTridiView.text = getString(R.string.upload_gambar_3d_instrumen)
                        }

                        if (fileTridiBefore != null) {
                            binding.inputTridiView.text = getString(R.string.gambar_added)
                        }

                        Toast.makeText(this, "Please select a valid .glb file", Toast.LENGTH_SHORT).show()
                    }
                }

                if (fileTridi != null) {
                    binding.inputTridiView.text = getString(R.string.gambar_added)
                } else {
                    binding.inputTridiView.text = getString(R.string.upload_gambar_3d_instrumen)
                }

                if (fileTridiBefore != null) {
                    binding.inputTridiView.text = getString(R.string.gambar_added)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openSpecificFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"  // Set the MIME type to filter files
            val uri = Uri.parse("content://com.android.externalstorage")
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        }

        requestFileLauncher.launch(intent)
    }

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInputInstrumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

        idInstrument = intent.getStringExtra(IDINSTRUMENT)

        lifecycleScope.launch {
            inputInstrumentViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@InputInstrumentActivity).create(
                    InputInstrumentViewModel::class.java)
            }

            binding.edFungsiInstrument.setOnTouchListener { v, event ->
                if (v.id == R.id.ed_fungsi_instrument) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }

            binding.edDeskripsiInstrument.setOnTouchListener { v, event ->
                if (v.id == R.id.ed_deskripsi_instrument) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }

            if (idInstrument != null) {
                inputInstrumentViewModel.getDetailInstrumentById(idInstrument!!).observe(this@InputInstrumentActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                namaInstrumenBefore = result.data.instrumentData[0].namaInstrument
                                fungsiInstrumenBefore = result.data.instrumentData[0].fungsi
                                deskripsiInstrumenBefore = result.data.instrumentData[0].description

                                result.data.instrumentData[0].bahan.forEach {
                                    itemsMaterialBefore.add(it)
                                }

                                binding.uploadInstrumen.text = "Simpan dan Edit Audio"

                                fileTridiBefore = result.data.instrumentData[0].tridImage
                                listImageBefore = result.data.instrumentData[0].imageInstrumen

                                binding.edNamaInstrument.setText(result.data.instrumentData[0].namaInstrument)
                                binding.edFungsiInstrument.setText(result.data.instrumentData[0].fungsi)
                                binding.edDeskripsiInstrument.setText(result.data.instrumentData[0].description)

                                result.data.instrumentData[0].bahan.forEach {
                                    itemsMaterial.add(it)
                                }

                                createListView(itemsMaterial)
                                binding.listViewMaterial.visibility = View.VISIBLE

                                if (fileTridiBefore != null) {
                                    binding.inputTridiView.text = getString(R.string.gambar_added)
                                }

                                if (listImageBefore.isNotEmpty()) {
                                    firstImage = listImageBefore[0]
                                    secondImage = listImageBefore[1]
                                }

                                if (firstImage != null) {
                                    binding.inputFirstFoto.text = getString(R.string.gambar_added)
                                }

                                if (secondImage != null) {
                                    binding.inputSecondFoto.text = getString(R.string.gambar_added)
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

            binding.inputFirstFoto.setOnClickListener {
                flagsBtnImage = 1
                startGallery()
            }

            binding.inputSecondFoto.setOnClickListener {
                flagsBtnImage = 2
                startGallery()
            }

            binding.inputTridiView.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.choose_fragment, null)
                val btnChooseStorage: MaterialButton = dialogView.findViewById(R.id.chooseInStorage)
                val btnChooseFromPolycam: MaterialButton = dialogView.findViewById(R.id.create3DFirst)
                val builderConfirm = BottomSheetDialog(this@InputInstrumentActivity)

                builderConfirm.setContentView(dialogView)
                builderConfirm.show()

                btnChooseStorage.setOnClickListener {
                    openSpecificFolder()
                    builderConfirm.cancel()
                }

                btnChooseFromPolycam.setOnClickListener {
//                    val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//
//                    for (packageInfo in packages) {
//                        Log.d("TAGFile", "Package name:" + packageInfo.packageName)
//                    }
                    val intent = packageManager.getLaunchIntentForPackage("com.polycam.polycam")
                    if (intent != null) {
                        builderConfirm.cancel()
                        startActivity(intent)
                    } else {
                        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ai.polycam&pcampaignid=web_share"))
                        builderConfirm.cancel()
                        startActivity(playStoreIntent)
                    }
                }
            }

            binding.inputMaterial.setOnClickListener {
                val builderConfirm = AlertDialog.Builder(this@InputInstrumentActivity)
                builderConfirm.setTitle("Input Material")

                val input = EditText(this@InputInstrumentActivity)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                params.setMargins(16, 0, 16, 0)
                input.layoutParams = params

                builderConfirm.setView(input)
                builderConfirm.setPositiveButton("Tambah") { dialog, which ->
                    val userInput = input.text.toString()
                    if (userInput.isNotEmpty()) {
                        itemsMaterial.add(userInput)
                        createListView(itemsMaterial)
                        Log.d("IsidariMaterialInstrumen", itemsMaterial.toString())
                        binding.listViewMaterial.visibility = View.VISIBLE
                    }
                }

                builderConfirm.setNegativeButton("Batal") { dialog, which ->
                    dialog.dismiss()
                }

                builderConfirm.create().show()
            }

            binding.uploadInstrumen.setOnClickListener {
                var namaInstrumen = binding.edNamaInstrument.text.toString()
                var fungsiInstrumen = binding.edFungsiInstrument.text.toString()
                var deskripsiInstrumen = binding.edDeskripsiInstrument.text.toString()
                var materialInstrumen = itemsMaterial

                if (idInstrument == null){
                    if (namaInstrumen.isEmpty()) {
                        binding.edNamaInstrument.error = getString(R.string.cannot_empty)
                    } else if (fungsiInstrumen.isEmpty()) {
                        binding.edFungsiInstrument.error = getString(R.string.cannot_empty)
                    } else if (deskripsiInstrumen.isEmpty()) {
                        binding.edDeskripsiInstrument.error = getString(R.string.cannot_empty)
                    } else if (materialInstrumen.isEmpty()) {
                        showToast(getString(R.string.material_cannot_empty))
                    } else if (file == null) {
                        showToast(getString(R.string.tambahkan_gambar_preview))
                    } else if (file2 == null) {
                        showToast(getString(R.string.tambahkan_gambar_bagian_dimainkan))
                    } else if (fileTridi == null) {
                        showToast(getString(R.string.upload_gambar_3d_instrumen))
                    } else {
                        listImageFile.add(file!!)
                        listImageFile.add(file2!!)

                        Log.d(
                            "IsiYangDikirim",
                            "${namaInstrumen}, ${fungsiInstrumen}, ${deskripsiInstrumen}"
                        )
                        inputInstrumentViewModel.createInstrumentData(
                            namaInstrumen,
                            deskripsiInstrumen,
                            fungsiInstrumen,
                            listImageFile,
                            fileTridi!!,
                            materialInstrumen
                        ).observe(this@InputInstrumentActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        isLoading(true)
                                    }

                                    is Result.Success -> {
                                        showToast("Instrument Added")
                                        isLoading(false)
                                        val intentBack = Intent(
                                            this@InputInstrumentActivity,
                                            InputAudioInstrumentActivity::class.java
                                        )
                                        intentBack.putExtra(
                                            InputAudioInstrumentActivity.ID,
                                            result.data
                                        )
                                        startActivity(intentBack)
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
                    if (namaInstrumen == namaInstrumenBefore) {
                        namaInstrumen = ""
                    }

                    if (deskripsiInstrumen == deskripsiInstrumenBefore) {
                        deskripsiInstrumen = ""
                    }

                    if (fungsiInstrumen == fungsiInstrumenBefore) {
                        fungsiInstrumen = ""
                    }
                    Log.d("IsidariMaterialInstrumen", itemsMaterial.toString())
                    Log.d("IsidariMaterialInstrumenBefore", itemsMaterialBefore.toString())
                    if (itemsMaterial == itemsMaterialBefore) {
                        itemsMaterial = mutableListOf()
                    }

                    if (file != null && file2 != null) {
                        listImageFile.add(file!!)
                        listImageFile.add(file2!!)
                        flagsEdittedIndexPhoto = "2"
                    } else if (file != null) {
                        listImageFile.add(file!!)
                    } else if (file2 != null) {
                        listImageFile.add(file2!!)
                    }

                    if (fileTridi == null) {
                        fileTridi = null
                    }

                    if (flagsEdittedIndexPhoto == null) {
                        flagsEdittedIndexPhoto = ""
                    }

                    inputInstrumentViewModel.updateInstrumentData(
                        idInstrument!!,
                        flagsEdittedIndexPhoto,
                        namaInstrumen,
                        deskripsiInstrumen,
                        fungsiInstrumen,
                        listImageFile,
                        fileTridi,
                        itemsMaterial
                    ).observe(this@InputInstrumentActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast(result.data.toString())
                                    isLoading(false)

                                    val intentBack = Intent(
                                        this@InputInstrumentActivity,
                                        InputAudioInstrumentActivity::class.java
                                    )
                                    intentBack.putExtra(
                                        InputAudioInstrumentActivity.ID,
                                        idInstrument
                                    )
                                    startActivity(intentBack)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun createListView(items: MutableList<String>) {
        adapterMaterial = InputMaterialAdapter(this@InputInstrumentActivity) {
            itemsMaterial = it
            adapterMaterial.notifyDataSetChanged()
            if (itemsMaterial.isEmpty()) {
                binding.listViewMaterial.visibility = View.GONE
            }
        }

        binding.listViewMaterial.layoutManager = LinearLayoutManager(this@InputInstrumentActivity,  LinearLayoutManager.VERTICAL, false)
        binding.listViewMaterial.setHasFixedSize(true)
        binding.listViewMaterial.adapter = adapterMaterial
        adapterMaterial.setListMaterialInput(items)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
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

    private fun uriToName(uri: Uri, context: Context): String {
        var name = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex)
                }
            }
        }
        return name
    }

    companion object {
        const val IDINSTRUMENT = "id_instrument"
    }

}