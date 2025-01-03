package com.reviling.filamentandroid.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel

class LoginViewModel(private val repository: RepositoryData): ViewModel() {

    fun loginUser(email: String, password: String) = repository.loginUser(email, password)

    fun getSessionUser(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}