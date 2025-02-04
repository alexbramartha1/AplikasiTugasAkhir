package com.reviling.filamentandroid.ui.seeallsanggar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel

class SeeAllSanggarViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun getSessionUser(): LiveData<UserModel> {
        return repositoryData.getSession().asLiveData()
    }
    fun getListKabupaten() = repositoryData.getListKabupaten()
    fun getAllSanggar() =  repositoryData.getAllSanggarBaliData()
    fun getAllSanggarByUserId(userId: String) = repositoryData.getSanggarByIdCreator(userId)
    fun getAllSanggarByName(namaSanggar: String) = repositoryData.getSanggarByName(namaSanggar)
    fun getListStatus() = repositoryData.getListStatus()
    fun getSanggarByFilter(id: String, statusId: List<String>) = repositoryData.getSanggarByFilter(id, statusId)

}