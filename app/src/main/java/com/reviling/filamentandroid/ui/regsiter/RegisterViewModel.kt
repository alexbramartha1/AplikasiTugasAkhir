package com.reviling.filamentandroid.ui.regsiter

import androidx.lifecycle.ViewModel
import com.reviling.filamentandroid.data.RepositoryData
import java.io.File

class RegisterViewModel(private val repository: RepositoryData): ViewModel() {

    fun registerUser(nama: String, email: String, password: String, role: String, supportDocument: File?) = repository.registerUser(nama, email, password, role, supportDocument)
    fun getAllRoleList() = repository.getRoleList()
}