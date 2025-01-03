package com.reviling.filamentandroid.ui.regsiter

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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.databinding.ActivityRegisterBinding
import com.reviling.filamentandroid.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.reviling.filamentandroid.data.Result

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registViewModel: RegisterViewModel
    private val items = mutableListOf<String>()
    private val idRole = mutableListOf<String>()
    private var idRoleFlag: Int? = null
    private var idRoleSelect: String? = null
    private var roleSelectedName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            registViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@RegisterActivity).create(RegisterViewModel::class.java)
            }

            getAllRoleList()
        }



        binding.chooseRole.setOnClickListener {
            var selectedItem = 0

            if (idRoleFlag != null) {
                selectedItem = idRoleFlag as Int
            }

            Log.d("IsiDariItems", items.toString())

            val dialogView = layoutInflater.inflate(R.layout.dialog_list_alamat, null)
            val listView: ListView = dialogView.findViewById(R.id.listView)
            val buttonPilih: Button = dialogView.findViewById(R.id.pilihkabbtn)
            val buttonBatal: Button = dialogView.findViewById(R.id.batalkabbtn)
            val headerView: TextView = dialogView.findViewById(R.id.title_alamat)

            val adapter = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_list_item_single_choice, items)
            listView.adapter = adapter

            listView.choiceMode = ListView.CHOICE_MODE_SINGLE
            listView.setItemChecked(selectedItem, true)

            val builder = AlertDialog.Builder(this@RegisterActivity)
            builder
                .setView(dialogView)

            val dialog = builder.create()
            dialog.show()
            headerView.setText(R.string.pilih_role)
            buttonPilih.setOnClickListener {
                val selectedPosition = listView.checkedItemPosition
                binding.chooseRole.text = adapter.getItem(selectedPosition)
                idRoleFlag = selectedPosition
                idRoleSelect = idRole[selectedPosition]
                roleSelectedName = adapter.getItem(selectedPosition)
                dialog.dismiss()
            }

            buttonBatal.setOnClickListener { dialog.dismiss() }
        }

        binding.btnRegister.setOnClickListener {
            val nama = binding.edUsername.text.toString()
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            if (nama.isEmpty()) {
                binding.edUsername.error = getString(R.string.cannot_empty)
            } else if (email.isEmpty()) {
                binding.edEmail.error = getString(R.string.cannot_empty)
            } else if (password.isEmpty()) {
                binding.edPassword.error = getString(R.string.cannot_empty)
            } else if (idRoleSelect == null) {
                showToast(getString(R.string.role_cannot_empty))
            } else {
                registViewModel.registerUser(nama, email, password, idRoleSelect!!).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast(result.data)
                                isLoading(false)

                                val intentMain = Intent(this, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intentMain)
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
        }

        binding.goToLogin.setOnClickListener {
            val intentRegister = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentRegister)
            finish()
        }

        supportActionBar?.hide()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun getAllRoleList() {
        registViewModel.getAllRoleList().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        isLoading(false)
                        result.data.forEach {
                            items.add(it.role)
                            idRole.add(it.id)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}