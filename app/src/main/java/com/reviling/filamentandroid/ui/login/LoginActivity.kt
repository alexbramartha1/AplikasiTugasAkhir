package com.reviling.filamentandroid.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.databinding.ActivityLoginBinding
import com.reviling.filamentandroid.ui.home.HomeActivity
import com.reviling.filamentandroid.ui.home.HomeViewModel
import com.reviling.filamentandroid.ui.regsiter.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
            loginViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(this@LoginActivity).create(LoginViewModel::class.java)
            }

            loginViewModel.getSessionUser().observe(this@LoginActivity) { user ->
                if (user.isLogin) {
                    val intentMain = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentMain)
                    finish()
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            val valid: Boolean = email.matches(emailRegex.toRegex())

            if (email.isEmpty()) {
                binding.edEmail.error = getString(R.string.cannot_empty)
            } else if (!valid) {
                binding.edEmail.error = getString(R.string.email_not_valid)
            } else if (password.isEmpty()) {
                binding.edPassword.error = getString(R.string.cannot_empty)
            } else {
                loginViewModel.loginUser(email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast(result.data)
                                isLoading(false)

                                val intentMain = Intent(this, HomeActivity::class.java)
                                intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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

        binding.goToRegister.setOnClickListener {
            val intentRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentRegister)
        }

        supportActionBar?.hide()
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
        finishAffinity()
    }
}