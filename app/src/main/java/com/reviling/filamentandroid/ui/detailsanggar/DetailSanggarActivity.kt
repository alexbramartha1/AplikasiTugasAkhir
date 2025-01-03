package com.reviling.filamentandroid.ui.detailsanggar

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.databinding.ActivityDetailSanggarBinding
import com.reviling.filamentandroid.ui.CustomItemDecoration
import com.reviling.filamentandroid.ui.adapter.GamelanAdapter
import com.reviling.filamentandroid.ui.adapter.HomeAdapter
import com.reviling.filamentandroid.ui.detailgamelan.DetailGamelanViewModel
import com.reviling.filamentandroid.ui.inputsanggar.InputDataSanggarActivity
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailSanggarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSanggarBinding
    private lateinit var sanggarId: String
    private var flags: Boolean = true
    private lateinit var detailSanggarViewModel: DetailSanggarViewModel
    private var flagsEdit: Boolean = false
    private var idUser: String = "idUser"
    private lateinit var gamelanAdapter: GamelanAdapter
    private var idListGamelan: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSanggarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sanggarId = intent.getStringExtra(ID).toString()
        lifecycleScope.launch {
            detailSanggarViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@DetailSanggarActivity)
                    .create(DetailSanggarViewModel::class.java)
            }

            detailSanggarViewModel.getSessionUser().observe(this@DetailSanggarActivity) { user ->
                idUser = user.user_id
            }

            detailSanggarViewModel.getDetailSanggarById(sanggarId).observe(this@DetailSanggarActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)
                            Log.d("ISIDATA", result.data.toString())
                            binding.sanggarName.text = result.data.sanggarData[0].namaSanggar
                            binding.sanggarDesc.text = result.data.sanggarData[0].deskripsi
                            binding.sanggarDescFull.text = result.data.sanggarData[0].deskripsi
                            binding.alamatDesc.text = result.data.sanggarData[0].alamatLengkap
                            binding.phoneNumber.text = result.data.sanggarData[0].noTelepon
                            Glide.with(this@DetailSanggarActivity)
                                .load(result.data.sanggarData[0].image)
                                .transform(CenterCrop())
                                .into(binding.sanggarImage)

                            if (idUser == result.data.sanggarData[0].idCreator) {
                                flagsEdit = true
                            } else {
                                flagsEdit = false
                            }

                            result.data.sanggarData[0].gamelanId.forEach {
                                idListGamelan.add(it.replace("\"", ""))
                            }

                            if (idListGamelan.isNotEmpty()) {
                                detailSanggarViewModel.getGamelanByIdList(idListGamelan).observe(this@DetailSanggarActivity) { resultGamelan ->
                                    if (resultGamelan != null) {
                                        when (resultGamelan) {
                                            is Result.Loading -> {
                                                isLoading(true)
                                            }

                                            is Result.Success -> {
                                                gamelanAdapter = GamelanAdapter(this@DetailSanggarActivity)
                                                binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@DetailSanggarActivity,  LinearLayoutManager.VERTICAL, false)
                                                binding.rvGamelanBaliHome.setHasFixedSize(true)
                                                binding.rvGamelanBaliHome.adapter = gamelanAdapter

                                                gamelanAdapter.setListGamelan(resultGamelan.data)
                                                showToast("Data Loaded")
                                                isLoading(false)
                                            }

                                            is Result.Error -> {
                                                showToast(resultGamelan.error)
                                                isLoading(false)
                                            }
                                        }
                                    }

                                }
                            }

                            binding.gotomaps.setOnClickListener {
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${result.data.sanggarData[0].namaSanggar}"))
                                startActivity(mapIntent)
                            }

                            binding.chats.setOnClickListener {
                                val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${result.data.sanggarData[0].noTelepon}"))
                                startActivity(whatsappIntent)
                            }

                            binding.calls.setOnClickListener {
                                val phoneNumber = "tel:${result.data.sanggarData[0].noTelepon}"
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse(phoneNumber)
                                }
                                startActivity(dialIntent)
                            }

                            binding.editdatacard.setOnClickListener {
                                val intent = Intent(this@DetailSanggarActivity, InputDataSanggarActivity::class.java)
                                intent.putExtra(InputDataSanggarActivity.ID, result.data.sanggarData[0].id)
                                startActivity(intent)
                            }

                            binding.deletebtn.setOnClickListener {
                                val builder = AlertDialog.Builder(this@DetailSanggarActivity)
                                val inflater = layoutInflater
                                val dialogView = inflater.inflate(R.layout.fragment_choose, null)

                                val buttonDelete = dialogView.findViewById<Button>(R.id.deletebtnsanggar)
                                val buttonBatal = dialogView.findViewById<Button>(R.id.batalbtn)

                                builder.setView(dialogView)
                                val dialog = builder.create()
                                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.show()

                                buttonDelete.setOnClickListener {
                                    detailSanggarViewModel.deleteSanggarData(sanggarId).observe(this@DetailSanggarActivity) { result ->
                                        if (result != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    isLoading(true)
                                                }

                                                is Result.Success -> {
                                                    showToast(result.data)
                                                    isLoading(false)
                                                    dialog.dismiss()
                                                    val intent = Intent(this@DetailSanggarActivity, SeeAllSanggarActivity::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                    startActivity(intent)
                                                    finish()
                                                }

                                                is Result.Error -> {
                                                    showToast(result.error)
                                                    isLoading(false)
                                                }
                                            }
                                        }
                                    }
                                }

                                buttonBatal.setOnClickListener { dialog.dismiss() }
                            }

                            binding.buttonClickAll.setOnClickListener {
                                binding.sanggarDesc.visibility = View.GONE
                                binding.buttonClickAll.visibility = View.GONE
                                binding.buttonClickDown.visibility = View.VISIBLE
                                binding.sanggarDescFull.visibility = View.VISIBLE
                            }

                            binding.buttonClickDown.setOnClickListener {
                                binding.sanggarDesc.visibility = View.VISIBLE
                                binding.buttonClickAll.visibility = View.VISIBLE
                                binding.buttonClickDown.visibility = View.GONE
                                binding.sanggarDescFull.visibility = View.GONE
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

        binding.scrolling.setOnRefreshListener {
            detailSanggarViewModel.getSessionUser().observe(this@DetailSanggarActivity) { user ->
                idUser = user.user_id
            }

            detailSanggarViewModel.getDetailSanggarById(sanggarId).observe(this@DetailSanggarActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }

                        is Result.Success -> {
                            showToast("Data Loaded")
                            isLoading(false)
                            Log.d("ISIDATA", result.data.toString())
                            binding.sanggarName.text = result.data.sanggarData[0].namaSanggar
                            binding.sanggarDesc.text = result.data.sanggarData[0].deskripsi
                            binding.sanggarDescFull.text = result.data.sanggarData[0].deskripsi
                            binding.alamatDesc.text = result.data.sanggarData[0].alamatLengkap
                            binding.phoneNumber.text = result.data.sanggarData[0].noTelepon
                            Glide.with(this@DetailSanggarActivity)
                                .load(result.data.sanggarData[0].image)
                                .transform(CenterCrop())
                                .into(binding.sanggarImage)

                            if (idUser == result.data.sanggarData[0].idCreator) {
                                flagsEdit = true
                            } else {
                                flagsEdit = false
                            }
                            idListGamelan.clear()
                            idListGamelan = result.data.sanggarData[0].gamelanId

                            if (idListGamelan.isNotEmpty()) {
                                detailSanggarViewModel.getGamelanByIdList(idListGamelan).observe(this@DetailSanggarActivity) { resultGamelan ->
                                    if (resultGamelan != null) {
                                        when (resultGamelan) {
                                            is Result.Loading -> {
                                                isLoading(true)
                                            }

                                            is Result.Success -> {
                                                gamelanAdapter = GamelanAdapter(this@DetailSanggarActivity)
                                                binding.rvGamelanBaliHome.layoutManager = LinearLayoutManager(this@DetailSanggarActivity,  LinearLayoutManager.VERTICAL, false)
                                                binding.rvGamelanBaliHome.setHasFixedSize(true)
                                                binding.rvGamelanBaliHome.adapter = gamelanAdapter

                                                gamelanAdapter.setListGamelan(resultGamelan.data)
                                                showToast("Data Loaded")
                                                isLoading(false)
                                            }

                                            is Result.Error -> {
                                                showToast(resultGamelan.error)
                                                isLoading(false)
                                            }
                                        }
                                    }

                                }
                            }


                            binding.gotomaps.setOnClickListener {
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${result.data.sanggarData[0].namaSanggar}"))
                                startActivity(mapIntent)
                            }

                            binding.chats.setOnClickListener {
                                val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${result.data.sanggarData[0].noTelepon}"))
                                startActivity(whatsappIntent)
                            }

                            binding.calls.setOnClickListener {
                                val phoneNumber = "tel:${result.data.sanggarData[0].noTelepon}"
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse(phoneNumber)
                                }
                                startActivity(dialIntent)
                            }

                            binding.editdatacard.setOnClickListener {
                                val intent = Intent(this@DetailSanggarActivity, InputDataSanggarActivity::class.java)
                                intent.putExtra(InputDataSanggarActivity.ID, result.data.sanggarData[0].id)
                                startActivity(intent)
                            }

                            binding.deletebtn.setOnClickListener {
                                val builder = AlertDialog.Builder(this@DetailSanggarActivity)
                                val inflater = layoutInflater
                                val dialogView = inflater.inflate(R.layout.fragment_choose, null)

                                val buttonDelete = dialogView.findViewById<Button>(R.id.deletebtnsanggar)
                                val buttonBatal = dialogView.findViewById<Button>(R.id.batalbtn)

                                builder.setView(dialogView)
                                val dialog = builder.create()
                                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.show()

                                buttonDelete.setOnClickListener {
                                    detailSanggarViewModel.deleteSanggarData(sanggarId).observe(this@DetailSanggarActivity) { result ->
                                        if (result != null) {
                                            when (result) {
                                                is Result.Loading -> {
                                                    isLoading(true)
                                                }

                                                is Result.Success -> {
                                                    showToast(result.data)
                                                    isLoading(false)
                                                    dialog.dismiss()

                                                    val intent = Intent(this@DetailSanggarActivity, SeeAllSanggarActivity::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                    startActivity(intent)
                                                    finish()
                                                }

                                                is Result.Error -> {
                                                    showToast(result.error)
                                                    isLoading(false)
                                                }
                                            }
                                        }
                                    }
                                }

                                buttonBatal.setOnClickListener { dialog.dismiss() }
                            }

                            binding.buttonClickAll.setOnClickListener {
                                binding.sanggarDesc.visibility = View.GONE
                                binding.buttonClickAll.visibility = View.GONE
                                binding.buttonClickDown.visibility = View.VISIBLE
                                binding.sanggarDescFull.visibility = View.VISIBLE
                            }

                            binding.buttonClickDown.setOnClickListener {
                                binding.sanggarDesc.visibility = View.VISIBLE
                                binding.buttonClickAll.visibility = View.VISIBLE
                                binding.buttonClickDown.visibility = View.GONE
                                binding.sanggarDescFull.visibility = View.GONE
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

        binding.showEverything.setOnClickListener {
            if (flags) {
                binding.calls.visibility = View.VISIBLE
                binding.chats.visibility = View.VISIBLE
                binding.gotomaps.visibility = View.VISIBLE

                if (flagsEdit){
                    binding.editdatacard.visibility = View.VISIBLE
                    binding.deletebtn.visibility = View.VISIBLE
                } else {
                    binding.editdatacard.visibility = View.INVISIBLE
                    binding.deletebtn.visibility = View.INVISIBLE
                }

                flags = false
            } else {
                binding.calls.visibility = View.GONE
                binding.chats.visibility = View.GONE
                binding.gotomaps.visibility = View.GONE

                if (flagsEdit){
                    binding.editdatacard.visibility = View.GONE
                    binding.deletebtn.visibility = View.GONE
                } else {
                    binding.editdatacard.visibility = View.GONE
                    binding.deletebtn.visibility = View.GONE
                }

                flags = true
            }
        }

        supportActionBar?.hide()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
    companion object {
        const val ID = "sanggarId"
    }
}