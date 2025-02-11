package com.reviling.filamentandroid.ui.seeallinstrument

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.databinding.ActivityDetailSeeAllInstrumentBinding
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.data.response.InstrumentDataItem
import com.reviling.filamentandroid.ui.BaseActivity
import com.reviling.filamentandroid.ui.CustomItemDecorationAbove
import com.reviling.filamentandroid.ui.CustomItemDecorationVertical
import com.reviling.filamentandroid.ui.CustomItemDecorationVerticalDouble
import com.reviling.filamentandroid.ui.adapter.HomeAdapter
import com.reviling.filamentandroid.ui.home.HomeActivity
import com.reviling.filamentandroid.ui.inputinstrument.InputInstrumentActivity

class DetailSeeAllInstrumentActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailSeeAllInstrumentBinding
    private lateinit var seeAllInstrumentViewModel: SeeAllInstrumentViewModel
    private lateinit var adapterInstrument: InstrumentAdapter
    private var statusIdList: MutableList<String> = mutableListOf()
    private var statusNamaList: MutableList<String> = mutableListOf()

    private var selectedIdStatus: MutableList<String> = mutableListOf()
    private var selectedNamaStatus: MutableList<String> = mutableListOf()

    private var fullShowOrNot: Boolean = true
    private var dataSetApprovedInstrument: MutableList<InstrumentDataItem> = mutableListOf()

    override fun repeatFunction() {
        lifecycleScope.launch {
            seeAllInstrumentViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailSeeAllInstrumentActivity)
                    .create(SeeAllInstrumentViewModel::class.java)
            }
            seeAllInstrumentViewModel.getSessionUser()
                .observe(this@DetailSeeAllInstrumentActivity) { user ->
                    if (user.isLogin) {
                        seeAllInstrumentViewModel.getUserDatabyId(user.user_id)
                            .observe(this@DetailSeeAllInstrumentActivity) { save ->
                                if (save != null) {
                                    when (save) {
                                        is Result.Loading -> {}

                                        is Result.Success -> {
                                            if (save.data.status != user.status) {
                                                seeAllInstrumentViewModel.saveSession(
                                                    UserModel(
                                                        nama = save.data.nama,
                                                        access_token = user.access_token,
                                                        email = save.data.email,
                                                        user_id = save.data.id,
                                                        foto_profile = save.data.fotoProfile,
                                                        role = save.data.role,
                                                        status = save.data.status,
                                                        isLogin = true,
                                                        document = save.data.supportDocument
                                                    )
                                                )
                                                binding.searchBarInstrument.menu.clear()
                                                if (user.role == "67619109cc4fa7bc6c0bdbc8" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                    binding.addDataInstrument.visibility =
                                                        View.VISIBLE
                                                    binding.searchBarInstrument.inflateMenu(R.menu.menu)
                                                } else {
                                                    binding.searchBarInstrument.menu.clear()
                                                    binding.addDataInstrument.visibility = View.GONE
                                                }
                                            }
                                        }

                                        is Result.Error -> {
                                            showToast(save.error)
                                        }
                                    }
                                }
                            }
                    }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSeeAllInstrumentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            seeAllInstrumentViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailSeeAllInstrumentActivity)
                    .create(SeeAllInstrumentViewModel::class.java)
            }

            loadStatusList()

            seeAllInstrumentViewModel.getSessionUser().observe(this@DetailSeeAllInstrumentActivity) { user ->
                if (user.isLogin) {
                    if (user.role == "676190f1cc4fa7bc6c0bdbc4" || user.status == "67618f9ecc4fa7bc6c0bdbbb" || user.status == "67618fe3cc4fa7bc6c0bdbc1") {
                        fullShowOrNot = true
                    } else {
                        fullShowOrNot = false
                    }

                    binding.searchBarInstrument.menu.clear()
                    if (user.role == "67619109cc4fa7bc6c0bdbc8" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                        binding.addDataInstrument.visibility = View.VISIBLE
                        binding.searchBarInstrument.inflateMenu(R.menu.menu)
                    } else if (user.role == "676190fdcc4fa7bc6c0bdbc6" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                        binding.addDataInstrument.visibility = View.GONE
                        binding.searchBarInstrument.inflateMenu(R.menu.menu)
                    } else {
                        binding.searchBarInstrument.menu.clear()
                        binding.addDataInstrument.visibility = View.GONE
                    }
                }
            }

            var selectedItemStatus: MutableList<Int> = mutableListOf()

            binding.searchBarInstrument.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_filter -> {

                        val dialogView = layoutInflater.inflate(R.layout.fragment_filter, null)
                        val listViewStatus: ListView = dialogView.findViewById(R.id.listViewStatus)
                        val buttonPilih: Button = dialogView.findViewById(R.id.pilihFilter)
                        val buttonBatal: Button = dialogView.findViewById(R.id.batalFilter)
                        val buttonClearFilter: Button = dialogView.findViewById(R.id.clearFilter)
                        val judulGolongan: TextView = dialogView.findViewById(R.id.title_golongan_filter)
                        val chooseKabupaten: MaterialButton = dialogView.findViewById(R.id.chooseKabupaten)

                        chooseKabupaten.visibility = View.GONE
                        judulGolongan.visibility = View.GONE
                        val adapterStatus = ArrayAdapter(
                            this@DetailSeeAllInstrumentActivity,
                            android.R.layout.simple_list_item_multiple_choice,
                            statusNamaList
                        )
                        listViewStatus.adapter = adapterStatus
                        listViewStatus.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                        selectedItemStatus.forEach {
                            listViewStatus.setItemChecked(it, true)
                        }

                        val builder = AlertDialog.Builder(this@DetailSeeAllInstrumentActivity)
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

                            if (selectedIdStatus.isNotEmpty()) {
                                loadAllInstrumentByFilter()
                            }

                            dialog.dismiss()
                        }

                        buttonClearFilter.setOnClickListener {
                            selectedNamaStatus.clear()
                            selectedIdStatus.clear()
                            selectedItemStatus.clear()

                            loadAllInstrument()
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

            binding.addDataInstrumentBtn.setOnClickListener {
                val intent = Intent(this@DetailSeeAllInstrumentActivity, InputInstrumentActivity::class.java)
                startActivity(intent)
            }

            seeAllInstrumentViewModel.getAllInstrumentData().observe(this@DetailSeeAllInstrumentActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            adapterInstrument = InstrumentAdapter(this@DetailSeeAllInstrumentActivity)
                            binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailSeeAllInstrumentActivity, 2)
                            val spacingInPixelsAbove = resources.getDimensionPixelSize(R.dimen.spacing_10dp)
                            binding.rvInstrumenHome.addItemDecoration(CustomItemDecorationAbove(spacingInPixelsAbove, false))
                            binding.rvInstrumenHome.setHasFixedSize(true)
                            binding.rvInstrumenHome.adapter = adapterInstrument

                            dataSetApprovedInstrument.clear()
                            if (fullShowOrNot) {
                                result.data.forEach {
                                    if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                        dataSetApprovedInstrument.add(it)
                                    }
                                }
                                adapterInstrument.setListInstrument(dataSetApprovedInstrument)
                            } else {
                                adapterInstrument.setListInstrument(result.data)
                            }
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            isLoading(false)
                        }
                    }
                }
            }

            binding.scrolling.setOnRefreshListener {
                selectedNamaStatus.clear()
                selectedIdStatus.clear()
                selectedItemStatus.clear()
                seeAllInstrumentViewModel.getAllInstrumentData().observe(this@DetailSeeAllInstrumentActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast("Data Loaded")
                                isLoading(false)

                                adapterInstrument = InstrumentAdapter(this@DetailSeeAllInstrumentActivity)
                                binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailSeeAllInstrumentActivity, 2)
                                binding.rvInstrumenHome.setHasFixedSize(true)
                                binding.rvInstrumenHome.adapter = adapterInstrument

                                dataSetApprovedInstrument.clear()
                                if (fullShowOrNot) {
                                    result.data.forEach {
                                        if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                            dataSetApprovedInstrument.add(it)
                                        }
                                    }
                                    adapterInstrument.setListInstrument(dataSetApprovedInstrument)
                                } else {
                                    adapterInstrument.setListInstrument(result.data)
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

            binding.searchViewGamelan.setupWithSearchBar(binding.searchBarInstrument)
            binding.searchViewGamelan
                .editText
                .setOnEditorActionListener{ textView, actionId, event ->
                    binding.searchViewGamelan.hide()
                    val value = binding.searchViewGamelan.text.toString()
                    seeAllInstrumentViewModel.getInstrumentByName(value).observe(this@DetailSeeAllInstrumentActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)
                                    adapterInstrument = InstrumentAdapter(this@DetailSeeAllInstrumentActivity)
                                    binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailSeeAllInstrumentActivity, 2)
                                    binding.rvInstrumenHome.setHasFixedSize(true)
                                    binding.rvInstrumenHome.adapter = adapterInstrument

                                    dataSetApprovedInstrument.clear()
                                    if (fullShowOrNot) {
                                        result.data.forEach {
                                            if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                dataSetApprovedInstrument.add(it)
                                            }
                                        }
                                        adapterInstrument.setListInstrument(dataSetApprovedInstrument)
                                    } else {
                                        adapterInstrument.setListInstrument(result.data)
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
        }
        
        supportActionBar?.hide()
    }

    private fun loadAllInstrument() {
        seeAllInstrumentViewModel.getAllInstrumentData().observe(this@DetailSeeAllInstrumentActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        adapterInstrument = InstrumentAdapter(this@DetailSeeAllInstrumentActivity)
                        binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailSeeAllInstrumentActivity, 2)
                        binding.rvInstrumenHome.setHasFixedSize(true)
                        binding.rvInstrumenHome.adapter = adapterInstrument

                        dataSetApprovedInstrument.clear()
                        if (fullShowOrNot) {
                            result.data.forEach {
                                if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                    dataSetApprovedInstrument.add(it)
                                }
                            }
                            adapterInstrument.setListInstrument(dataSetApprovedInstrument)
                        } else {
                            adapterInstrument.setListInstrument(result.data)
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

    private fun loadAllInstrumentByFilter() {
        seeAllInstrumentViewModel.getInstrumentByFilter(selectedIdStatus).observe(this@DetailSeeAllInstrumentActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        adapterInstrument = InstrumentAdapter(this@DetailSeeAllInstrumentActivity)
                        binding.rvInstrumenHome.layoutManager = GridLayoutManager(this@DetailSeeAllInstrumentActivity, 2)
                        binding.rvInstrumenHome.setHasFixedSize(true)
                        binding.rvInstrumenHome.adapter = adapterInstrument
                        adapterInstrument.setListInstrument(result.data)
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
        seeAllInstrumentViewModel.getListStatus().observe(this@DetailSeeAllInstrumentActivity) { statusData ->
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}