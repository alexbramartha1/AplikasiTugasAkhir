package com.reviling.filamentandroid.ui.inputgamelan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.databinding.ActivityInputGamelanBinding
import com.reviling.filamentandroid.ui.adapter.InputMaterialAdapter
import com.reviling.filamentandroid.ui.adapter.InputUpacaraGamelanAdapter
import com.reviling.filamentandroid.ui.inputinstrument.InputAudioInstrumentActivity
import com.reviling.filamentandroid.ui.inputinstrument.InputInstrumentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputGamelanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputGamelanBinding
    private lateinit var inputGamelanBaliViewModel: InputGamelanViewModel

    private var upacaraInputList: MutableList<String> = mutableListOf()
    private lateinit var adapterUpacara: InputUpacaraGamelanAdapter
    private var upacaraListBefore: MutableList<String> = mutableListOf()

    private var golonganNameList: MutableList<String> = mutableListOf()
    private var golonganIdList: MutableList<String> = mutableListOf()
    private var selectedGolonganBefore: String? = null
    private var selectedIdGolongan: String? = null
    private var selectedNamaGolongan: String? = null
    private var flagsGolongan: Int? = null

    private var instrumentNameList: MutableList<String> = mutableListOf()
    private var instrumentIdList: MutableList<String> = mutableListOf()
    private var selectedIdInstrumentBefore: MutableList<String> = mutableListOf()
    private var selectedIdInstrument: MutableList<String> = mutableListOf()
    private var selectedNamaInstrument: MutableList<String?> = mutableListOf()
    private var flagsInstrument: MutableList<Int> = mutableListOf()

    private var idGamelan: String? = null
    private var namaGamelanBefore: String? = null
    private var deskripsiGamelanBefore: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputGamelanBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            inputGamelanBaliViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@InputGamelanActivity).create(
                    InputGamelanViewModel::class.java)
            }

            idGamelan = intent.getStringExtra(IDGAMELAN)

            inputGamelanBaliViewModel.fetchGolonganGamelan().observe(this@InputGamelanActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            result.data.forEach {
                                golonganNameList.add(it.golongan)
                                golonganIdList.add(it.id)
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

            inputGamelanBaliViewModel.fetchInstrumentOnlyNameId().observe(this@InputGamelanActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            result.data.forEach {
                                instrumentIdList.add(it.id)
                                instrumentNameList.add(it.namaInstrument)
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

            if (idGamelan != null) {
                inputGamelanBaliViewModel.geDetailGamelanInstrument(idGamelan!!).observe(this@InputGamelanActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                binding.edNamaGamelan.setText(result.data.gamelanData[0].namaGamelan)
                                binding.edDeskripsiGamelan.setText(result.data.gamelanData[0].description)
                                namaGamelanBefore = result.data.gamelanData[0].namaGamelan
                                deskripsiGamelanBefore = result.data.gamelanData[0].description

                                val index = golonganIdList.indexOf(result.data.gamelanData[0].golonganId)
                                val namaGolongan = golonganNameList[index]
                                selectedIdGolongan = result.data.gamelanData[0].golonganId
                                selectedGolonganBefore = result.data.gamelanData[0].golonganId
                                binding.inputGolongan.text = namaGolongan

                                result.data.gamelanData[0].instrumentId.forEach {
                                    selectedIdInstrumentBefore.add(it)
                                    selectedIdInstrument.add(it)
                                }

                                if (selectedIdInstrument.isNotEmpty()) {
                                    binding.inputInstrumentId.setText(R.string.instrument_added)
                                } else {
                                    binding.inputInstrumentId.setText(R.string.pilih_instrument_gamelan)
                                }

                                result.data.gamelanData[0].upacara.forEach {
                                    upacaraInputList.add(it)
                                    upacaraListBefore.add(it)
                                }

                                if (upacaraInputList.isNotEmpty()) {
                                    createListView(upacaraInputList)
                                    binding.listViewUpacara.visibility = View.VISIBLE
                                } else {
                                    binding.listViewUpacara.visibility = View.GONE
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

            binding.inputGolongan.setOnClickListener {
                var selectedItem = 0

                if (idGamelan != null) {
                    val index = golonganIdList.indexOf(selectedGolonganBefore)
                    Log.d("IsiDariSelectedIndex", index.toString())
                    if (index != -1) {
                        selectedItem = index
                    }
                }

                if (flagsGolongan != null) {
                    selectedItem = flagsGolongan as Int
                }

                val dialogView = layoutInflater.inflate(R.layout.dialog_list_alamat, null)
                val listView: ListView = dialogView.findViewById(R.id.listView)
                val buttonPilih: Button = dialogView.findViewById(R.id.pilihkabbtn)
                val buttonBatal: Button = dialogView.findViewById(R.id.batalkabbtn)
                val headerView: TextView = dialogView.findViewById(R.id.title_alamat)

                val adapter = ArrayAdapter(this@InputGamelanActivity, android.R.layout.simple_list_item_single_choice, golonganNameList)
                listView.adapter = adapter

                listView.choiceMode = ListView.CHOICE_MODE_SINGLE
                listView.setItemChecked(selectedItem, true)

                val builder = AlertDialog.Builder(this@InputGamelanActivity)
                builder
                    .setView(dialogView)

                val dialog = builder.create()
                dialog.show()
                headerView.setText(R.string.pilih_gamelan)
                buttonPilih.setOnClickListener {
                    val selectedPosition = listView.checkedItemPosition
                    binding.inputGolongan.text = adapter.getItem(selectedPosition)
                    flagsGolongan = selectedPosition
                    selectedIdGolongan = golonganIdList[selectedPosition]
                    selectedNamaGolongan = adapter.getItem(selectedPosition)
                    dialog.dismiss()
                }

                buttonBatal.setOnClickListener { dialog.dismiss() }

            }

            binding.inputUpacara.setOnClickListener {
                val builderConfirm = AlertDialog.Builder(this@InputGamelanActivity)
                builderConfirm.setTitle("Input Upacara")

                val input = EditText(this@InputGamelanActivity)
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
                        upacaraInputList.add(userInput)
                        createListView(upacaraInputList)
                        binding.listViewUpacara.visibility = View.VISIBLE
                    }
                }

                builderConfirm.setNegativeButton("Batal") { dialog, which ->
                    dialog.dismiss()
                }

                builderConfirm.create().show()
            }

            binding.inputInstrumentId.setOnClickListener {
                var selectedItem: MutableList<Int> = mutableListOf()

                if (idGamelan != null) {
                    val trimmedIdBefore: MutableList<String> = mutableListOf()

                    selectedIdInstrumentBefore.forEach {
                        trimmedIdBefore.add(it.replace("\"", ""))
                    }

                    trimmedIdBefore.forEach { idList ->
                        val index = instrumentIdList.indexOf(idList)
                        Log.d("IsiDariSelectedIndex", index.toString())
                        if (index != -1) {
                            selectedItem.add(index)
                        }
                    }
                }

                if (flagsInstrument.isNotEmpty()) {
                    selectedItem = flagsInstrument
                }

                val dialogView = layoutInflater.inflate(R.layout.dialog_list_alamat, null)
                val listView: ListView = dialogView.findViewById(R.id.listView)
                val buttonPilih: Button = dialogView.findViewById(R.id.pilihkabbtn)
                val buttonBatal: Button = dialogView.findViewById(R.id.batalkabbtn)
                val headerView: TextView = dialogView.findViewById(R.id.title_alamat)

                val adapter = ArrayAdapter(this@InputGamelanActivity, android.R.layout.simple_list_item_single_choice, instrumentNameList)
                listView.adapter = adapter

                listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                Log.d("IsiSelectedItem", selectedItem.toString())
                selectedItem.forEach {
                    listView.setItemChecked(it, true)
                }

                val builder = AlertDialog.Builder(this@InputGamelanActivity)
                builder
                    .setView(dialogView)

                val dialog = builder.create()
                dialog.show()
                headerView.setText(R.string.pilih_instrument_gamelan)
                buttonPilih.setOnClickListener {
                    flagsInstrument.clear()
                    selectedIdInstrument.clear()
                    selectedIdInstrumentBefore.clear()
                    selectedNamaInstrument.clear()
                    for (i in 0 until listView.count) {
                        if (listView.isItemChecked(i)) {
                            flagsInstrument.add(i)
                            selectedIdInstrument.add(instrumentIdList[i].replace("\"", ""))
                            selectedNamaInstrument.add(adapter.getItem(i))
                        }
                    }

                    if (selectedIdInstrument.isNotEmpty()) {
                        binding.inputInstrumentId.setText(R.string.instrument_added)
                    } else {
                        binding.inputInstrumentId.setText(R.string.pilih_instrument_gamelan)
                    }

                    dialog.dismiss()
                }

                buttonBatal.setOnClickListener { dialog.dismiss() }
            }

            binding.uploadGamelan.setOnClickListener {
                var namaGamelan = binding.edNamaGamelan.text.toString()
                var deskripsiGamelan = binding.edDeskripsiGamelan.text.toString()
                var golongan = selectedIdGolongan
                var instrumentId = selectedIdInstrument
                var upacaraList = upacaraInputList

                if (idGamelan == null) {
                    if (namaGamelan.isEmpty()) {
                        binding.edNamaGamelan.error = getString(R.string.cannot_empty)
                    } else if (deskripsiGamelan.isEmpty()) {
                        binding.edDeskripsiGamelan.error = getString(R.string.cannot_empty)
                    } else if (golongan == null) {
                        showToast(getString(R.string.pilih_golongan))
                    } else if (instrumentId.isEmpty()) {
                        showToast(getString(R.string.pilih_instrument_gamelan))
                    } else if (upacaraList.isEmpty()) {
                        showToast(getString(R.string.tambahkan_upacara))
                    } else {
                        inputGamelanBaliViewModel.createGamelanData(
                            namaGamelan,
                            golongan,
                            deskripsiGamelan,
                            upacaraList,
                            instrumentId
                        ).observe(this@InputGamelanActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        isLoading(true)
                                    }

                                    is Result.Success -> {
                                        showToast("Data Created Successfully")
                                        isLoading(false)
                                        val intentBack = Intent(
                                            this@InputGamelanActivity,
                                            InputAudioGamelanActivity::class.java
                                        )
                                        intentBack.putExtra(
                                            InputAudioGamelanActivity.IDGAMELAN,
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

                    if (namaGamelan == namaGamelanBefore) {
                        namaGamelan = ""
                    }

                    if (deskripsiGamelan == deskripsiGamelanBefore) {
                        deskripsiGamelan = ""
                    }

                    if (golongan == selectedGolonganBefore) {
                        golongan = ""
                    }

                    if (instrumentId == selectedIdInstrumentBefore) {
                        instrumentId = mutableListOf()
                    }

                    if (upacaraList == upacaraListBefore) {
                        upacaraList = mutableListOf()
                    }

                    inputGamelanBaliViewModel.updateDataGamelan(
                        idGamelan!!,
                        namaGamelan,
                        golongan,
                        deskripsiGamelan,
                        upacaraList,
                        instrumentId
                    ).observe(this@InputGamelanActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast(result.data)
                                    isLoading(false)

                                    val intentBack = Intent(
                                        this@InputGamelanActivity,
                                        InputAudioGamelanActivity::class.java
                                    )
                                    intentBack.putExtra(
                                        InputAudioGamelanActivity.IDGAMELAN,
                                        idGamelan
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
        adapterUpacara = InputUpacaraGamelanAdapter(this@InputGamelanActivity) {
            upacaraInputList = it
            adapterUpacara.notifyDataSetChanged()
            if (upacaraInputList.isEmpty()) {
                binding.listViewUpacara.visibility = View.GONE
            }
        }

        binding.listViewUpacara.layoutManager = LinearLayoutManager(this@InputGamelanActivity,  LinearLayoutManager.VERTICAL, false)
        binding.listViewUpacara.setHasFixedSize(true)
        binding.listViewUpacara.adapter = adapterUpacara
        adapterUpacara.setListUpacaraInput(items)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    companion object {
        const val IDGAMELAN = "idgamelan"
    }

}