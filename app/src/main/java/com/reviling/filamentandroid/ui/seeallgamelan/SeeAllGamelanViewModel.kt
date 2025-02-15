package com.reviling.filamentandroid.ui.seeallgamelan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel
import kotlinx.coroutines.launch

class SeeAllGamelanViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun seeAllGamelanBali() = repositoryData.getAllGamelanBaliData()
    fun getGamelanByGolongan(golongan: String) = repositoryData.getGamelanByGolongan(golongan)
    fun getSearchAllGamelanInstrument(namaGamelan: String) = repositoryData.getSearchGamelanInstrument(namaGamelan)
    fun fetchGolonganGamelan() = repositoryData.fetchGolonganGamelan()
    fun getListStatus() = repositoryData.getListStatus()
    fun getSessionUser(): LiveData<UserModel> {
        return repositoryData.getSession().asLiveData()
    }
    fun getUserDatabyId(idUser: String) = repositoryData.getUserDatabyId(idUser)
    fun getGamelanDataByFilter(statusId: List<String>, golonganId: List<String>) = repositoryData.getGamelanDataByFilter(statusId, golonganId)
    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            repositoryData.saveSession(userModel)
        }
    }
    fun logoutUser() {
        viewModelScope.launch {
            repositoryData.logout()
        }
    }
}