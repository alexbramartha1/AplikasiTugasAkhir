package com.reviling.filamentandroid.ui.detailsanggar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel

class DetailSanggarViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun deleteSanggarData(idSanggar: String) = repositoryData.deleteSanggarData(idSanggar)
    fun getDetailSanggarById(id: String) = repositoryData.geDetailSanggarbyId(id)
    fun getSessionUser(): LiveData<UserModel> {
        return repositoryData.getSession().asLiveData()
    }
    fun getNoteData(id: String) = repositoryData.getNoteData(id)
    fun getListStatus() = repositoryData.getListStatus()
    fun getGamelanByIdList(idList: MutableList<String>) = repositoryData.getGamelanByIdList(idList)
    fun updateApprovalSanggar(id: String, note: String, statusId: String) = repositoryData.updateApprovalSanggar(id, note, statusId)
}