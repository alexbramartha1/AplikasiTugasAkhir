package com.reviling.filamentandroid.ui.seeallgamelan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isNotEmpty
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.databinding.ActivityDetailGamelanBinding
import com.reviling.filamentandroid.databinding.ActivitySeeAllGamelanBaliBinding
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.seeallinstrument.SeeAllInstrumentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.data.response.GamelanDataItem
import com.reviling.filamentandroid.ui.BaseActivity
import com.reviling.filamentandroid.ui.CustomItemDecorationVertical
import com.reviling.filamentandroid.ui.adapter.GamelanAdapter
import com.reviling.filamentandroid.ui.inputgamelan.InputGamelanActivity
import com.reviling.filamentandroid.ui.login.LoginActivity

class SeeAllGamelanBaliActivity : BaseActivity() {

    private lateinit var binding: ActivitySeeAllGamelanBaliBinding
    private lateinit var seeAllGamelanViewModel: SeeAllGamelanViewModel
    private lateinit var adapter: GamelanAdapter
    private var golonganIdList: MutableList<String> = mutableListOf()
    private var golonganNamaList: MutableList<String> = mutableListOf()

    private var statusIdList: MutableList<String> = mutableListOf()
    private var statusNamaList: MutableList<String> = mutableListOf()

    private var selectedIdStatus: MutableList<String> = mutableListOf()
    private var selectedNamaStatus: MutableList<String> = mutableListOf()

    private var selectedIdGolongan: MutableList<String> = mutableListOf()
    private var selectedNamaGolongan: MutableList<String> = mutableListOf()

    private var dataSetApproved: MutableList<GamelanDataItem> = mutableListOf()
    private var fullShowOrNot: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllGamelanBaliBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            seeAllGamelanViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@SeeAllGamelanBaliActivity)
                    .create(SeeAllGamelanViewModel::class.java)
            }

            val params = binding.searchBarGamelan.layoutParams as ConstraintLayout.LayoutParams
            seeAllGamelanViewModel.getSessionUser().observe(this@SeeAllGamelanBaliActivity) { user ->
                if (user.isLogin) {

                    if (user.role == "676190f1cc4fa7bc6c0bdbc4" || user.status == "67618f9ecc4fa7bc6c0bdbbb" || user.status == "67618fe3cc4fa7bc6c0bdbc1") {
                        fullShowOrNot = true
                    } else {
                        fullShowOrNot = false
                    }

                    binding.searchBarGamelan.menu.clear()
                    if (user.role == "67619109cc4fa7bc6c0bdbc8" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                        binding.addDataGamelan.visibility = View.VISIBLE
                        binding.searchBarGamelan.inflateMenu(R.menu.menu)
                        binding.scrollButton.visibility = View.GONE
                        params.topMargin = resources.getDimensionPixelSize(R.dimen.spacing_15dp)
                        binding.searchBarGamelan.layoutParams = params
                        binding.searchBarGamelan.requestLayout()
                    } else if (user.role == "676190fdcc4fa7bc6c0bdbc6" && user.status == "67618fc3cc4fa7bc6c0bdbbf") {
                        binding.addDataGamelan.visibility = View.GONE
                        binding.searchBarGamelan.inflateMenu(R.menu.menu)
                        binding.scrollButton.visibility = View.GONE
                        params.topMargin = resources.getDimensionPixelSize(R.dimen.spacing_15dp)
                        binding.searchBarGamelan.layoutParams = params
                        binding.searchBarGamelan.requestLayout()
                    } else {
                        binding.searchBarGamelan.menu.clear()
                        binding.scrollButton.visibility = View.VISIBLE
                        binding.addDataGamelan.visibility = View.GONE
                    }
                }
            }

            loadGolonganList()
            loadStatusList()

            var selectedItemStatus: MutableList<Int> = mutableListOf()
            var selectedItemGolongan: MutableList<Int> = mutableListOf()

            binding.searchBarGamelan.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_filter -> {

                        val dialogView = layoutInflater.inflate(R.layout.fragment_filter, null)
                        val listViewGolongan: ListView = dialogView.findViewById(R.id.listViewGolongan)
                        val listViewStatus: ListView = dialogView.findViewById(R.id.listViewStatus)
                        val buttonPilih: Button = dialogView.findViewById(R.id.pilihFilter)
                        val buttonBatal: Button = dialogView.findViewById(R.id.batalFilter)
                        val buttonClearFilter: Button = dialogView.findViewById(R.id.clearFilter)
                        val judulGolongan: TextView = dialogView.findViewById(R.id.title_golongan_filter)
                        val chooseKabupaten: MaterialButton = dialogView.findViewById(R.id.chooseKabupaten)
                        chooseKabupaten.visibility = View.GONE
                        judulGolongan.visibility = View.VISIBLE
                        val adapterGolongan = ArrayAdapter(
                            this@SeeAllGamelanBaliActivity,
                            android.R.layout.simple_list_item_multiple_choice,
                            golonganNamaList
                        )
                        listViewGolongan.adapter = adapterGolongan
                        listViewGolongan.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                        selectedItemGolongan.forEach {
                            listViewGolongan.setItemChecked(it, true)
                        }

                        val adapterStatus = ArrayAdapter(
                            this@SeeAllGamelanBaliActivity,
                            android.R.layout.simple_list_item_multiple_choice,
                            statusNamaList
                        )
                        listViewStatus.adapter = adapterStatus
                        listViewStatus.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                        selectedItemStatus.forEach {
                            listViewStatus.setItemChecked(it, true)
                        }

                        val builder = AlertDialog.Builder(this@SeeAllGamelanBaliActivity)
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

                            selectedIdGolongan.clear()
                            selectedNamaGolongan.clear()
                            selectedItemGolongan.clear()

                            for (i in 0 until listViewGolongan.count) {
                                if (listViewGolongan.isItemChecked(i)) {
                                    selectedItemGolongan.add(i)
                                    selectedIdGolongan.add(golonganIdList[i])
                                    adapterGolongan.getItem(i)
                                        ?.let { it1 -> selectedNamaGolongan.add(it1) }
                                }
                            }

                            if (selectedIdStatus.isNotEmpty() && selectedIdGolongan.isNotEmpty()) {
                                loadAllGamelanByFilter()
                            }

                            dialog.dismiss()
                        }

                        buttonClearFilter.setOnClickListener {
                            selectedNamaStatus.clear()
                            selectedIdStatus.clear()
                            selectedItemStatus.clear()

                            selectedNamaGolongan.clear()
                            selectedIdGolongan.clear()
                            selectedItemGolongan.clear()
                            loadAllGamelan()
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

            binding.searchViewGamelan.setupWithSearchBar(binding.searchBarGamelan)

            binding.searchViewGamelan
                .editText
                .setOnEditorActionListener{ textView, actionId, event ->
                    binding.searchViewGamelan.hide()

                    val value = binding.searchViewGamelan.text.toString()
                    loadGamelanByName(value)
                    false
                }

            seeAllGamelanViewModel.seeAllGamelanBali().observe(this@SeeAllGamelanBaliActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                            binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
                            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                            binding.rvGamelanBaliHome.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
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
                            isLoading(false)
                        }
                    }
                }
            }

            binding.addDataGamelanBtn.setOnClickListener {
                val intent = Intent(this@SeeAllGamelanBaliActivity, InputGamelanActivity::class.java)
                startActivity(intent)
            }

            binding.seeallgolongan.setOnClickListener {
                loadAllGamelan()
                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.gamelanDesc.visibility = View.GONE
            }

            binding.golonganTua.setOnClickListener {
                loadGamelanByGolongan("6761a2ddcc4fa7bc6c0bdbe6")
                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.gamelanDesc.visibility = View.VISIBLE
            }

            binding.golonganMadya.setOnClickListener {
                loadGamelanByGolongan("6761a2e8cc4fa7bc6c0bdbe8")
                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.gamelanDesc.visibility = View.VISIBLE
            }

            binding.golonganBaru.setOnClickListener {
                loadGamelanByGolongan("6761a2f1cc4fa7bc6c0bdbea")
                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
                binding.gamelanDesc.visibility = View.VISIBLE
            }

            binding.scrolling.setOnRefreshListener {
                selectedNamaStatus.clear()
                selectedIdStatus.clear()
                selectedItemStatus.clear()

                selectedNamaGolongan.clear()
                selectedIdGolongan.clear()
                selectedItemGolongan.clear()
                loadAllGamelan()

                binding.seeallgolongan.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.colorPrimary))
                binding.golonganTua.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganMadya.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))
                binding.golonganBaru.setBackgroundColor(ContextCompat.getColor(this@SeeAllGamelanBaliActivity, R.color.gray))

                binding.scrolling.isRefreshing = false
            }
        }

        supportActionBar?.hide()
    }

    private fun loadGamelanByName(gamelan: String) {
        seeAllGamelanViewModel.getSearchAllGamelanInstrument(gamelan).observe(this@SeeAllGamelanBaliActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                        binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                        binding.rvGamelanBaliHome.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                        binding.rvGamelanBaliHome.setHasFixedSize(true)
                        binding.rvGamelanBaliHome.adapter = adapter

                        dataSetApproved.clear()
                        if (fullShowOrNot) {
                            result.data.gamelanData.forEach {
                                if (it.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                    dataSetApproved.add(it)
                                }
                            }
                            adapter.setListGamelan(dataSetApproved)
                        } else {
                            adapter.setListGamelan(result.data.gamelanData)
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

    private fun loadAllGamelan() {
        seeAllGamelanViewModel.seeAllGamelanBali().observe(this@SeeAllGamelanBaliActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                        binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
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
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun loadAllGamelanByFilter() {
        seeAllGamelanViewModel.getGamelanDataByFilter(selectedIdStatus, selectedIdGolongan).observe(this@SeeAllGamelanBaliActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        showToast("Data Loaded")
                        isLoading(false)

                        adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                        binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
                        binding.rvGamelanBaliHome.setHasFixedSize(true)
                        binding.rvGamelanBaliHome.adapter = adapter

                        adapter.setListGamelan(result.data)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun loadGamelanByGolongan(golongan: String) {
        if (golongan != "lihatsemua") {
            seeAllGamelanViewModel.getGamelanByGolongan(golongan).observe(this@SeeAllGamelanBaliActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)

                            seeAllGamelanViewModel.fetchGolonganGamelan().observe(this@SeeAllGamelanBaliActivity) { golonganData ->
                                if (golonganData != null) {
                                    when (golonganData) {
                                        is Result.Loading -> {
                                            isLoading(true)
                                        }

                                        is Result.Success -> {
                                            isLoading(false)

                                            golonganData.data.forEach {
                                                if (it.id == golongan) {
                                                    binding.gamelanDesc.text = it.deskripsi
                                                }
                                            }
                                        }

                                        is Result.Error -> {
                                            showToast(golonganData.error)
                                            isLoading(false)
                                        }
                                    }
                                }
                            }

                            adapter = GamelanAdapter(this@SeeAllGamelanBaliActivity)
                            binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@SeeAllGamelanBaliActivity,  LinearLayoutManager.VERTICAL, false)
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
                            isLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun loadGolonganList() {
        seeAllGamelanViewModel.fetchGolonganGamelan().observe(this@SeeAllGamelanBaliActivity) { golonganData ->
            if (golonganData != null) {
                when (golonganData) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        isLoading(false)

                        golonganData.data.forEach {
                            golonganIdList.add(it.id)
                            golonganNamaList.add(it.golongan)
                        }
                    }

                    is Result.Error -> {
                        showToast(golonganData.error)
                        isLoading(false)
                    }
                }
            }
        }
    }

    private fun loadStatusList() {
        seeAllGamelanViewModel.getListStatus().observe(this@SeeAllGamelanBaliActivity) { statusData ->
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

    override fun repeatFunction() {
        lifecycleScope.launch {
            seeAllGamelanViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@SeeAllGamelanBaliActivity)
                    .create(SeeAllGamelanViewModel::class.java)
            }
            val params = binding.searchBarGamelan.layoutParams as ConstraintLayout.LayoutParams

            seeAllGamelanViewModel.getSessionUser()
                .observe(this@SeeAllGamelanBaliActivity) { user ->
                    if (user.isLogin) {
                        seeAllGamelanViewModel.getUserDatabyId(user.user_id)
                            .observe(this@SeeAllGamelanBaliActivity) { save ->
                                if (save != null) {
                                    when (save) {
                                        is Result.Loading -> {}

                                        is Result.Success -> {
                                            if (save.data.status != user.status) {
                                                seeAllGamelanViewModel.saveSession(
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
                                                binding.searchBarGamelan.menu.clear()
                                                if (save.data.role == "67619109cc4fa7bc6c0bdbc8" && save.data.status == "67618fc3cc4fa7bc6c0bdbbf") {
                                                    binding.addDataGamelan.visibility = View.VISIBLE
                                                    binding.searchBarGamelan.inflateMenu(R.menu.menu)
                                                    binding.scrollButton.visibility = View.GONE
                                                    params.topMargin =
                                                        resources.getDimensionPixelSize(R.dimen.spacing_15dp)
                                                    binding.searchBarGamelan.layoutParams = params
                                                    binding.searchBarGamelan.requestLayout()
                                                } else {
                                                    binding.searchBarGamelan.menu.clear()
                                                    binding.addDataGamelan.visibility = View.GONE
                                                    binding.scrollButton.visibility = View.VISIBLE
                                                }
                                            }
                                        }

                                        is Result.Error -> {
                                            showToast(save.error)
                                            Log.d("IsiDariSaveError", save.error)
                                            if (save.error == "Sorry, There is no user with this name ${user.user_id}" || save.error == "Sorry, Token has expired, please login again!" || save.error == "Sorry, Token Invalid!") {
                                                seeAllGamelanViewModel.logoutUser()
                                                val intentMain = Intent(this@SeeAllGamelanBaliActivity, LoginActivity::class.java)
                                                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(intentMain)
                                                finish()
                                            }
                                        }
                                    }
                                }
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