package com.reviling.filamentandroid.ui.regsiter

import androidx.lifecycle.ViewModel
import com.reviling.filamentandroid.data.RepositoryData

class RegisterViewModel(private val repository: RepositoryData): ViewModel() {

    fun registerUser(nama: String, email: String, password: String, role: String) = repository.registerUser(nama, email, password, role)
    fun getAllRoleList() = repository.getRoleList()
}