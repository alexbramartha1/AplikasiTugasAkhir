package com.reviling.filamentandroid.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(private val repository: RepositoryData): ViewModel() {

    fun getAllGamelanBali() = repository.getAllGamelanBaliData()

    fun getAllInstrumentBali() = repository.getAllInstrumentBaliData()

    fun getAllSanggarBali() = repository.getAllSanggarBaliData()

    fun getSearchAllGamelanInstrument(namaGamelan: String) = repository.getSearchGamelanInstrument(namaGamelan)

    fun getUserDatabyId(idUser: String) = repository.getUserDatabyId(idUser)

    fun updateUsername(id: String, username: String) = repository.updateUsername(id, username)

    fun getSessionUser(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun getAllRoleList() = repository.getRoleList()
    fun uploadPhotoUser(id: String, files: File) = repository.uploadPhotoUser(id, files)

    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            repository.saveSession(userModel)
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}