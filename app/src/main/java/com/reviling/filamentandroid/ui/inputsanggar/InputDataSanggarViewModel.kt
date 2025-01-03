package com.reviling.filamentandroid.ui.inputsanggar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserModel
import java.io.File

class InputDataSanggarViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun createSanggarData(
        fileImage: File,
        namaSanggar: String,
        noTelepon: String,
        namaJalan: String,
        kodePost: String,
        deskripsi: String,
        idDesa: String,
        gamelanId: MutableList<String>
    ) = repositoryData.createDataSanggar(
        fileImage,
        namaSanggar,
        noTelepon,
        namaJalan,
        kodePost,
        deskripsi,
        idDesa,
        gamelanId
    )

    fun updateSanggarData(
        id: String,
        fileImage: File?,
        namaSanggar: String?,
        noTelepon: String?,
        namaJalan: String?,
        kodePost: String?,
        deskripsi: String?,
        idDesa: String?,
        gamelanId: MutableList<String>?
    ) = repositoryData.updateDataSanggar(
        id,
        fileImage,
        namaSanggar,
        noTelepon,
        namaJalan,
        kodePost,
        deskripsi,
        idDesa,
        gamelanId
    )

    fun getDetailSanggarById(id: String) = repositoryData.geDetailSanggarbyId(id)

    fun getSessionUser(): LiveData<UserModel> {
        return repositoryData.getSession().asLiveData()
    }

    fun seeAllGamelanBali() = repositoryData.getAllGamelanBaliData()
    fun getListKabupaten() = repositoryData.getListKabupaten()
    fun getListKecamatan(id: String) = repositoryData.getListKecamatan(id)
    fun getListDesa(id: String) = repositoryData.getListDesa(id)
    fun getListAllAlamatByIdDesa(id: String) = repositoryData.getAllListAlamatByDesaId(id)
}