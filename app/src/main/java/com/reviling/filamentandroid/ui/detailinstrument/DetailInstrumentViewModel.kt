package com.reviling.filamentandroid.ui.detailinstrument

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel
import kotlinx.coroutines.launch

class DetailInstrumentViewModel(private val repository: RepositoryData): ViewModel() {
    fun getListStatus() = repository.getListStatus()
    fun geDetailGamelanByInstrumentId(id: String) = repository.geDetailGamelanByInstrumentId(id)
    fun getDetailInstrumentById(id: String) = repository.geDetailInstrumentById(id)
    fun deleteInstrumentById(id: String) = repository.deleteInstrumentById(id)
    fun deleteAudioByIdList(id: MutableList<String>) = repository.deleteAudioByIdList(id)
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
    fun logoutUser() {
        viewModelScope.launch {
            repository.logout()
        }
    }
    fun updateApprovalInstrument(id: String, note: String, statusId: String) = repository.updateApprovalInstrument(id, note, statusId)
}