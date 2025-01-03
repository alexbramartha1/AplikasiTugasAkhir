package com.reviling.filamentandroid.ui.inputgamelan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.response.AudioArrayGamelanItem
import com.reviling.filamentandroid.data.response.AudioArrayItem
import com.reviling.filamentandroid.databinding.ActivityInputAudioGamelanBinding
import com.reviling.filamentandroid.ui.CustomItemDecorationVertical
import com.reviling.filamentandroid.ui.adapter.InputAudioGamelanAdapter
import com.reviling.filamentandroid.ui.adapter.InputAudioInstrumentAdapter
import com.reviling.filamentandroid.ui.seeallgamelan.SeeAllGamelanBaliActivity
import com.reviling.filamentandroid.ui.seeallinstrument.DetailSeeAllInstrumentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class InputAudioGamelanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputAudioGamelanBinding
    private lateinit var inputGamelanViewModel: InputGamelanViewModel

    private lateinit var idGamelan: String
    private lateinit var adapterAudioList: InputAudioGamelanAdapter

    private var audioName: String? = null
    private var audioDeskripsi: String? = null
    private var fileAudio: File? = null
    private lateinit var buttonPickAudioTry: MaterialButton
    private lateinit var loadingDialog: ProgressBar

    private var itemsAudio: List<AudioArrayGamelanItem> = emptyList()

    private var flagsEditAudio: Int = 0

    private var idAudioGamelan: String? = null
    private var namaAudioBefore: String? = null
    private var audioDeskripsiBefore: String? = null
    private var audioFileBefore: String? = null

    private val requestFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    fileAudio = uriToFile(uri, this)
                }

                if (fileAudio != null) {
                    buttonPickAudioTry.text = getString(R.string.audio_added)
                } else {
                    buttonPickAudioTry.text = getString(R.string.upload_tabuh_gamelan)
                }

                if (audioFileBefore != null) {
                    buttonPickAudioTry.text = getString(R.string.audio_added)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openSpecificFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"  // Set the MIME type to filter files
            val uri = Uri.parse("content://com.android.externalstorage")
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        }

        requestFileLauncher.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputAudioGamelanBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            inputGamelanViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@InputAudioGamelanActivity).create(
                    InputGamelanViewModel::class.java
                )
            }

            idGamelan = intent.getStringExtra(IDGAMELAN).toString()

            fetchAudioData()

            binding.upload.setOnClickListener {
                val intent = Intent(this@InputAudioGamelanActivity, SeeAllGamelanBaliActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            binding.inputAudio.setOnClickListener {
                flagsEditAudio = 0
                createDialogAudio()
            }
        }

        supportActionBar?.hide()
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createListView() {
        adapterAudioList = InputAudioGamelanAdapter(this@InputAudioGamelanActivity) {
            if (it.flags == "delete") {
                deleteAudio(it.id)
                adapterAudioList.notifyDataSetChanged()
                if (itemsAudio.isEmpty()) {
                    binding.listViewAudio.visibility = View.GONE
                }
            } else if (it.flags == "edit") {
                flagsEditAudio = 1
                idAudioGamelan = it.id
                namaAudioBefore = it.audioName
                audioDeskripsiBefore = it.deskripsi
                audioFileBefore = it.audioPath
                createDialogAudio()
            }
        }

        binding.listViewAudio.layoutManager = LinearLayoutManager(this@InputAudioGamelanActivity,  LinearLayoutManager.VERTICAL, false)
        binding.listViewAudio.setHasFixedSize(false)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
        binding.listViewAudio.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
        binding.listViewAudio.adapter = adapterAudioList
        adapterAudioList.setListAudioGamelanInput(itemsAudio)
    }

    private fun deleteAudio(idAudioGamelan: String): Boolean {
        inputGamelanViewModel.deleteAudioGamelanByItsId(idAudioGamelan).observe(this@InputAudioGamelanActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast(result.data)
                        isLoading(false)
                    }

                    is Result.Error -> {
                        isLoading(false)
                        showToast(result.error)
                    }
                }
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDialogAudio() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_input_field_audio, null)
        val inputNameAudio: TextInputEditText = dialogView.findViewById(R.id.ed_audio_name)
        val buttonPilih: MaterialButton = dialogView.findViewById(R.id.uploadAudio)
        val buttonBatal: MaterialButton = dialogView.findViewById(R.id.batal)
        val inputDeskripsi: TextInputEditText = dialogView.findViewById(R.id.ed_audio_desc)
        val textFieldAudioDesc: TextInputLayout = dialogView.findViewById(R.id.textFieldAudioDesc)
        buttonPickAudioTry = dialogView.findViewById(R.id.inputAudioFile)
        loadingDialog = dialogView.findViewById(R.id.progress_bar_dialog)
        textFieldAudioDesc.visibility = View.VISIBLE
        val builder = AlertDialog.Builder(this@InputAudioGamelanActivity)
        builder
            .setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        buttonPickAudioTry.setOnClickListener {
            openSpecificFolder()
        }

        if (flagsEditAudio == 1) {
            inputNameAudio.setText(namaAudioBefore)
            inputDeskripsi.setText(audioDeskripsiBefore)
            buttonPickAudioTry.text = getString(R.string.audio_added)
        }

        buttonPilih.setOnClickListener {
            audioName = inputNameAudio.text.toString()
            audioDeskripsi = inputDeskripsi.text.toString()

            if (flagsEditAudio == 0) {
                if (audioName!!.isEmpty()) {
                    inputNameAudio.error = getString(R.string.cannot_empty)
                } else if (audioDeskripsi!!.isEmpty()) {
                    inputDeskripsi.error = getString(R.string.cannot_empty)
                } else if (fileAudio == null) {
                    showToast(getString(R.string.upload_tabuh_gamelan))
                } else {
                    inputGamelanViewModel.createAudioGamelanData(idGamelan, audioDeskripsi!!, audioName!!, fileAudio!!).observe(this@InputAudioGamelanActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoadingDialog(true)
                                }
                                is Result.Success -> {
                                    showToast(result.data)
                                    isLoadingDialog(false)
                                    fetchAudioData()
                                    audioName = null
                                    audioDeskripsi = null
                                    fileAudio = null
                                    dialog.dismiss()
                                }
                                is Result.Error -> {
                                    isLoadingDialog(false)
                                    showToast(result.error)
                                }
                            }
                        }
                    }
                }
            } else {

                if (audioName == namaAudioBefore) {
                    audioName = ""
                }

                if (fileAudio == null) {
                    fileAudio = null
                }

                if (audioDeskripsi == audioDeskripsiBefore) {
                    audioDeskripsi = ""
                }

                inputGamelanViewModel.updateAudioGamelanData(idAudioGamelan!!, audioDeskripsi, audioName, fileAudio).observe(this@InputAudioGamelanActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoadingDialog(true)
                            }
                            is Result.Success -> {
                                showToast(result.data.toString())
                                fetchAudioData()
                                isLoadingDialog(false)
                                dialog.dismiss()
                            }
                            is Result.Error -> {
                                isLoadingDialog(false)
                                showToast(result.error)
                            }
                        }
                    }
                }
            }
        }

        buttonBatal.setOnClickListener {
            audioName = null
            fileAudio = null
            audioDeskripsi = null
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            audioName = null
            fileAudio = null
            audioDeskripsi = null
            dialog.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchAudioData() {
        inputGamelanViewModel.fetchAudioGamelanByIdGamelan(idGamelan).observe(this@InputAudioGamelanActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        itemsAudio = emptyList()
                        itemsAudio = result.data
                        createListView()
                        binding.listViewAudio.visibility = View.VISIBLE
                        isLoading(false)
                    }

                    is Result.Error -> {
                        isLoading(false)
                        showToast(result.error)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@InputAudioGamelanActivity, SeeAllGamelanBaliActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun isLoadingDialog(loading: Boolean) {
        loadingDialog.visibility = if (loading) View.VISIBLE else View.GONE
    }

    companion object {
        const val IDGAMELAN = "idGamelan"
    }
}