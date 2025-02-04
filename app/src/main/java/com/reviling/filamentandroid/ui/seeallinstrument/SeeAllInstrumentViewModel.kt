package com.reviling.filamentandroid.ui.seeallinstrument

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel
import kotlinx.coroutines.launch

class SeeAllInstrumentViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun getAllInstrumentData() = repositoryData.getAllInstrumentBaliData()
    fun getInstrumentByName(namaInstrument: String) = repositoryData.getInstrumentByName(namaInstrument)
    fun getSessionUser(): LiveData<UserModel> {
        return repositoryData.getSession().asLiveData()
    }
    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            repositoryData.saveSession(userModel)
        }
    }
    fun getListStatus() = repositoryData.getListStatus()
    fun getInstrumentByFilter(statusId: List<String>) = repositoryData.getInstrumentByFilter(statusId)
    fun getUserDatabyId(idUser: String) = repositoryData.getUserDatabyId(idUser)
}