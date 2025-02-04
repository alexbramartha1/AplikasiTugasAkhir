package com.reviling.filamentandroid.ui.detailgamelan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel
import kotlinx.coroutines.launch

class DetailGamelanViewModel(private val repository: RepositoryData): ViewModel() {
    fun getListStatus() = repository.getListStatus()
    fun geDetailGamelanInstrument(id: String) = repository.geDetailGamelanInstrument(id)
    fun deleteManyAudioByItsGamelanId(idGamelan: String) = repository.deleteManyAudioByItsGamelanId(idGamelan)
    fun deleteGamelanDataByItsId(idGamelan: String) = repository.deleteGamelanDataByItsId(idGamelan)
    fun getSessionUser(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun getNoteData(id: String) = repository.getNoteData(id)
    fun getUserDatabyId(idUser: String) = repository.getUserDatabyId(idUser)
    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            repository.saveSession(userModel)
        }
    }
}