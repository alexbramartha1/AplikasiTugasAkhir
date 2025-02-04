package com.reviling.filamentandroid.ui.seeallsanggar

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.databinding.FragmentAllAndYourSanggarBinding
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.seeallinstrument.SeeAllInstrumentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.response.GetSanggarBaliResponse
import com.reviling.filamentandroid.data.response.SanggarDataItem
import com.reviling.filamentandroid.ui.CustomItemDecoration
import com.reviling.filamentandroid.ui.CustomItemDecorationVertical
import com.reviling.filamentandroid.ui.adapter.SanggarAdapter
import com.reviling.filamentandroid.ui.adapter.SanggarSeeAllAdapter
import com.reviling.filamentandroid.ui.inputsanggar.InputDataSanggarActivity

class AllAndYourSanggarFragment : Fragment() {

    private var _binding: FragmentAllAndYourSanggarBinding? = null
    private val binding get() =_binding!!
    private lateinit var seeAllSanggarViewModel: SeeAllSanggarViewModel
    private var position: Int? = null
    private lateinit var sanggarAdapter: SanggarSeeAllAdapter
    private var userId: String? = null

    private var statusIdList: MutableList<String> = mutableListOf()
    private var statusNamaList: MutableList<String> = mutableListOf()

    private var selectedIdStatus: MutableList<String> = mutableListOf()
    private var selectedNamaStatus: MutableList<String> = mutableListOf()

    private lateinit var dataSanggar: GetSanggarBaliResponse

    private lateinit var listViewStatus: ListView
    private lateinit var titleApproval: TextView
    private lateinit var chooseKabupaten: MaterialButton
    private lateinit var listViewKabupaten: RecyclerView

    private var kabupatenName: MutableList<String> = mutableListOf()
    private var selectedKabupaten: MutableList<String> = mutableListOf()
    private var flagsSelectKabupaten: MutableList<Int> = mutableListOf()

    private var fullShowOrNot: Boolean = true
    private var dataSetApprovedSanggar: MutableList<SanggarDataItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllAndYourSanggarBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.showEverything.setOnClickListener {
            Log.d("buttonClick", "buttonClick")
            val intentAdd = Intent(requireContext(), InputDataSanggarActivity::class.java)
            startActivity(intentAdd)
        }

        lifecycleScope.launch {
            seeAllSanggarViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(requireContext())
                    .create(SeeAllSanggarViewModel::class.java)
            }

            seeAllSanggarViewModel.getSessionUser().observe(viewLifecycleOwner) { user ->
                if (user.isLogin) {
                    userId = user.user_id

                    if (user.role == "676190f1cc4fa7bc6c0bdbc4" || user.status == "67618f9ecc4fa7bc6c0bdbbb" || user.status == "67618fe3cc4fa7bc6c0bdbc1") {
                        fullShowOrNot = true
                    } else {
                        fullShowOrNot = false
                    }

                }
            }

            arguments?.let {
                position = it.getInt(ARG_POSITION)
            }

            loadKabupaten()
            loadStatusList()
            var selectedItemStatus: MutableList<Int> = mutableListOf()

            binding.searchBarGamelan.inflateMenu(R.menu.menu)
            binding.searchBarGamelan.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_filter -> {

                        val dialogView = layoutInflater.inflate(R.layout.fragment_filter, null)
                        listViewStatus = dialogView.findViewById(R.id.listViewStatus)
                        titleApproval = dialogView.findViewById(R.id.title_approval)
                        chooseKabupaten = dialogView.findViewById(R.id.chooseKabupaten)
                        listViewKabupaten = dialogView.findViewById(R.id.listViewKabupaten)
                        val buttonPilih: Button = dialogView.findViewById(R.id.pilihFilter)
                        val buttonBatal: Button = dialogView.findViewById(R.id.batalFilter)
                        val buttonClearFilter: Button = dialogView.findViewById(R.id.clearFilter)
                        val judulGolongan: TextView = dialogView.findViewById(R.id.title_golongan_filter)

                        judulGolongan.visibility = View.GONE

                        if (selectedKabupaten.isNotEmpty()) {
                            chooseKabupaten.setText(R.string.kabupaten_added)
                        } else {
                            chooseKabupaten.setText(R.string.pilih_kabupaten)
                        }

                        if (position == 1) {
                            listViewStatus.visibility = View.GONE
                            titleApproval.visibility = View.GONE
                        } else {
                            chooseKabupaten.visibility = View.GONE
                            listViewStatus.visibility = View.VISIBLE
                            titleApproval.visibility = View.VISIBLE
                        }

                        chooseKabupaten.setOnClickListener {
                            dialogBuildingPickKabupaten(kabupatenName)
                        }

                        if (kabupatenName.isNotEmpty()) {
                            listViewKabupaten.visibility = View.VISIBLE
                        } else {
                            listViewKabupaten.visibility = View.GONE
                        }

                        val adapterStatus = ArrayAdapter(
                            requireActivity(),
                            android.R.layout.simple_list_item_multiple_choice,
                            statusNamaList
                        )

                        listViewStatus.adapter = adapterStatus
                        listViewStatus.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                        selectedItemStatus.forEach {
                            listViewStatus.setItemChecked(it, true)
                        }

                        val builder = AlertDialog.Builder(requireActivity())
                        builder
                            .setView(dialogView)

                        val dialog = builder.create()
                        dialog.show()

                        buttonPilih.setOnClickListener {
                            if (position == 2) {
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

                                if (selectedIdStatus.isNotEmpty()) {
                                    loadAllSanggarByFilter()
                                }
                            } else {

                            }

                            dialog.dismiss()
                        }

                        buttonClearFilter.setOnClickListener {
                            if (position == 2) {
                                selectedNamaStatus.clear()
                                selectedIdStatus.clear()
                                selectedItemStatus.clear()
                                loadAllSanggar()
                                dialog.dismiss()
                            } else {
                                selectedKabupaten.clear()
                                flagsSelectKabupaten.clear()
                                loadAllSanggarWithoutId()
                                dialog.dismiss()
                            }
                        }

                        buttonBatal.setOnClickListener {
                            if (position == 2) {
                                selectedNamaStatus.clear()
                                selectedIdStatus.clear()
                                selectedItemStatus.clear()
                                dialog.dismiss()
                            } else {
                                selectedKabupaten.clear()
                                flagsSelectKabupaten.clear()
                                chooseKabupaten.setText(R.string.pilih_kabupaten)
                                sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext())
                                binding.rvSanggar.setHasFixedSize(true)
                                binding.rvSanggar.adapter = sanggarAdapter
                                sanggarAdapter.setListSanggarAll(dataSanggar.sanggarData)
                                dialog.dismiss()
                            }
                        }

                        true
                    }
                    else -> false
                }
            }

            binding.searchViewGamelan.setupWithSearchBar(binding.searchBarGamelan)
            binding.searchViewGamelan
                .editText
                .setOnEditorActionListener{ textView, actionId, event ->
                    binding.searchViewGamelan.hide()
                    val value = binding.searchViewGamelan.text.toString()
                    seeAllSanggarViewModel.getAllSanggarByName(value).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)

                                    sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                    binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                    binding.rvSanggar.setHasFixedSize(true)
                                    binding.rvSanggar.adapter = sanggarAdapter

                                    dataSetApprovedSanggar.clear()
                                    if (fullShowOrNot && position != 1) {
                                        result.data.forEach {
                                            if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                dataSetApprovedSanggar.add(it)
                                            }
                                        }
                                        sanggarAdapter.setListSanggarAll(dataSetApprovedSanggar)
                                    } else {
                                        sanggarAdapter.setListSanggarAll(result.data)
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

            if (position == 1) {
//                binding.searchBarGamelan.menu.clear()
                seeAllSanggarViewModel.getAllSanggar().observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast("Data Loaded")
                                isLoading(false)

                                sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                                binding.rvSanggar.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                                binding.rvSanggar.setHasFixedSize(true)
                                binding.rvSanggar.adapter = sanggarAdapter
                                dataSetApprovedSanggar.clear()
                                if (fullShowOrNot) {
                                    result.data.forEach {
                                        if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                            dataSetApprovedSanggar.add(it)
                                        }
                                    }
                                    sanggarAdapter.setListSanggarAll(dataSetApprovedSanggar)
                                    dataSanggar = GetSanggarBaliResponse(dataSetApprovedSanggar)
                                } else {
                                    sanggarAdapter.setListSanggarAll(result.data)
                                    dataSanggar = GetSanggarBaliResponse(result.data)
                                }
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                isLoading(false)
                            }
                        }
                    }
                }
                binding.addDataSanggar.visibility = View.GONE

            } else {
                seeAllSanggarViewModel.getSessionUser().observe(viewLifecycleOwner) { user ->
                    if (user.isLogin) {
                        userId = user.user_id
                    }

                    seeAllSanggarViewModel.getAllSanggarByUserId(userId.toString()).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)

                                    sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                    binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                    if (binding.rvSanggar.itemDecorationCount == 0) {
                                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                                        binding.rvSanggar.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                                    }
                                    binding.rvSanggar.setHasFixedSize(true)
                                    binding.rvSanggar.adapter = sanggarAdapter
                                    sanggarAdapter.setListSanggarAll(result.data.sanggarData)
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    isLoading(false)
                                }
                            }
                        }
                    }
                }

                binding.addDataSanggar.visibility = View.VISIBLE
            }

            binding.scrolling.setOnRefreshListener {
                arguments?.let {
                    position = it.getInt(ARG_POSITION)
                }

                if (position == 1) {
                    seeAllSanggarViewModel.getAllSanggar().observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)

                                    sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                    binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                    binding.rvSanggar.setHasFixedSize(true)
                                    binding.rvSanggar.adapter = sanggarAdapter
                                    dataSetApprovedSanggar.clear()
                                    if (fullShowOrNot) {
                                        result.data.forEach {
                                            if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                dataSetApprovedSanggar.add(it)
                                            }
                                        }
                                        sanggarAdapter.setListSanggarAll(dataSetApprovedSanggar)
                                        dataSanggar = GetSanggarBaliResponse(dataSetApprovedSanggar)
                                    } else {
                                        sanggarAdapter.setListSanggarAll(result.data)
                                        dataSanggar = GetSanggarBaliResponse(result.data)
                                    }
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    isLoading(false)
                                }
                            }
                        }
                    }

                    binding.addDataSanggar.visibility = View.GONE

                    binding.scrolling.isRefreshing = false
                } else {
                    selectedNamaStatus.clear()
                    selectedIdStatus.clear()
                    selectedItemStatus.clear()
                    seeAllSanggarViewModel.getSessionUser().observe(viewLifecycleOwner) { user ->
                        if (user.isLogin) {
                            userId = user.user_id
                        }

                        seeAllSanggarViewModel.getAllSanggarByUserId(userId.toString()).observe(viewLifecycleOwner) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        isLoading(true)
                                    }

                                    is Result.Success -> {
                                        showToast("Data Loaded")
                                        isLoading(false)

                                        sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                        binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                        binding.rvSanggar.setHasFixedSize(true)
                                        binding.rvSanggar.adapter = sanggarAdapter
                                        sanggarAdapter.setListSanggarAll(result.data.sanggarData)
                                    }

                                    is Result.Error -> {
                                        showToast(result.error)
                                        isLoading(false)
                                    }
                                }
                            }
                        }
                    }

                    binding.addDataSanggar.visibility = View.VISIBLE

                    binding.scrolling.isRefreshing = false
                }
            }
        }

        return view
    }

    private fun loadKabupaten() {
        seeAllSanggarViewModel.getListKabupaten().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        kabupatenName.clear()
                        result.data.forEach {
                            kabupatenName.add(it.namaKabupaten)
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

    private fun loadAllSanggar() {
        seeAllSanggarViewModel.getAllSanggarByUserId(userId.toString()).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                        binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.rvSanggar.setHasFixedSize(true)
                        binding.rvSanggar.adapter = sanggarAdapter
                        sanggarAdapter.setListSanggarAll(result.data.sanggarData)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun loadAllSanggarWithoutId() {
        seeAllSanggarViewModel.getAllSanggar().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                        binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.rvSanggar.setHasFixedSize(true)
                        binding.rvSanggar.adapter = sanggarAdapter
                        dataSetApprovedSanggar.clear()
                        if (fullShowOrNot) {
                            result.data.forEach {
                                if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                    dataSetApprovedSanggar.add(it)
                                }
                            }
                            sanggarAdapter.setListSanggarAll(dataSetApprovedSanggar)
                            dataSanggar = GetSanggarBaliResponse(dataSetApprovedSanggar)
                        } else {
                            sanggarAdapter.setListSanggarAll(result.data)
                            dataSanggar = GetSanggarBaliResponse(result.data)
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

    private fun loadAllSanggarByFilter() {
        seeAllSanggarViewModel.getSanggarByFilter(userId!!, selectedIdStatus).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                        binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.rvSanggar.setHasFixedSize(true)
                        binding.rvSanggar.adapter = sanggarAdapter
                        sanggarAdapter.setListSanggarAll(result.data)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun loadStatusList() {
        seeAllSanggarViewModel.getListStatus().observe(viewLifecycleOwner) { statusData ->
            if (statusData != null) {
                when (statusData) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        isLoading(false)

                        statusData.data.forEach {
                            statusIdList.add(it.id)
                            statusNamaList.add(it.status)
                        }
                    }

                    is Result.Error -> {
                        showToast(statusData.error)
                        isLoading(false)
                    }
                }
            }
        }
    }
    
    private fun dialogBuildingPickKabupaten(items: List<String>) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_list_alamat_search, null)
        val listView: ListView = dialogView.findViewById(R.id.listView)
        val buttonPilih: Button = dialogView.findViewById(R.id.pilihkabbtn)
        val buttonBatal: Button = dialogView.findViewById(R.id.batalkabbtn)
        val headerView: TextView = dialogView.findViewById(R.id.title_alamat)
        val searchView: TextInputEditText = dialogView.findViewById(R.id.searchKab)

        val filteredItems = items.toMutableList()
        val adapterDesa = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_list_item_multiple_choice,
            filteredItems
        )

        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                filteredItems.clear()
                if (query.isNotEmpty()) {
                    filteredItems.addAll(items.filter { it.contains(query, ignoreCase = true) })
                } else {
                    filteredItems.addAll(items)
                }
                adapterDesa.notifyDataSetChanged()

                for (i in filteredItems.indices) {
                    listView.setItemChecked(i, selectedKabupaten.contains(filteredItems[i]))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        listView.adapter = adapterDesa
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        for (i in filteredItems.indices) {
            listView.setItemChecked(i, selectedKabupaten.contains(filteredItems[i]))
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = filteredItems[position]
            val originalIndex = items.indexOf(item)

            if (listView.isItemChecked(position)) {
                if (originalIndex != -1 && !selectedKabupaten.contains(item)) {
                    selectedKabupaten.add(item)
                    flagsSelectKabupaten.add(originalIndex)
                    Log.d("IsiDariSelectedKabupaten", selectedKabupaten.toString())
                }
            } else {
                selectedKabupaten.remove(item)
                flagsSelectKabupaten.remove(originalIndex)
            }
        }

        val builder = AlertDialog.Builder(requireActivity()).setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        headerView.setText(R.string.pilih_kabupaten)

        buttonPilih.setOnClickListener {
            // Action to finalize selections (already updated dynamically)
            if (selectedKabupaten.isNotEmpty()) {
                chooseKabupaten.setText(R.string.kabupaten_added)

                val filteredData = dataSanggar.sanggarData.filter { data ->
                    selectedKabupaten.contains(data.kabupaten)
                }

                sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext())
                binding.rvSanggar.setHasFixedSize(true)
                binding.rvSanggar.adapter = sanggarAdapter
                sanggarAdapter.setListSanggarAll(filteredData)
            } else {
                chooseKabupaten.setText(R.string.pilih_kabupaten)

                sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext())
                binding.rvSanggar.setHasFixedSize(true)
                binding.rvSanggar.adapter = sanggarAdapter
                sanggarAdapter.setListSanggarAll(dataSanggar.sanggarData)
            }

            dialog.dismiss()
        }

        buttonBatal.setOnClickListener {
            selectedKabupaten.clear()
            flagsSelectKabupaten.clear()
            chooseKabupaten.setText(R.string.pilih_kabupaten)
            sanggarAdapter = SanggarSeeAllAdapter(requireContext())
            binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext())
            binding.rvSanggar.setHasFixedSize(true)
            binding.rvSanggar.adapter = sanggarAdapter
            sanggarAdapter.setListSanggarAll(dataSanggar.sanggarData)
            dialog.dismiss()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ARG_POSITION = "position"
    }
}