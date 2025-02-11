package com.reviling.filamentandroid.ui.seeallusers

import androidx.lifecycle.ViewModel
import com.reviling.filamentandroid.data.RepositoryData

class AllUsersViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun getAllUsersData() = repositoryData.getAllUsersData()
    fun getUsersByName(nameUser: String) = repositoryData.getUsersByName(nameUser)
    fun getAllRoleList() = repositoryData.getRoleList()
    fun getListStatus() = repositoryData.getListStatus()
    fun getUsersByFilter(idRole: List<String>, idStatus: List<String>) = repositoryData.getUsersByFilter(idRole, idStatus)
    fun deleteUserById(idUser: String) = repositoryData.deleteUserById(idUser)
    fun getNoteData(id: String) = repositoryData.getNoteData(id)
    fun updateApprovalUsers(id: String, note: String, statusId: String) = repositoryData.updateApprovalUsers(id, note, statusId)

}