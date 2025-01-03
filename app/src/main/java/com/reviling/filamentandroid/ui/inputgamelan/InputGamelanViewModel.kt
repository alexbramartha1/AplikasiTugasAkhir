package com.reviling.filamentandroid.ui.inputgamelan

import androidx.lifecycle.ViewModel
import com.reviling.filamentandroid.data.RepositoryData
import java.io.File

class InputGamelanViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun createGamelanData(
        namaGamelan: String,
        golongan: String,
        description: String,
        upacara: List<String>,
        instrumentId: List<String>
    ) = repositoryData.createDataGamelan(
        namaGamelan,
        golongan,
        description,
        upacara,
        instrumentId
    )

    fun createAudioGamelanData(
        gamelanId: String,
        deskripsiAudio: String,
        namaAudioGamelan: String,
        files: File
    ) = repositoryData.createAudioGamelanData(
        gamelanId,
        deskripsiAudio,
        namaAudioGamelan,
        files
    )

    fun updateAudioGamelanData(
        idAudio: String,
        deskripsiAudio: String?,
        namaAudioGamelan: String?,
        files: File?
    ) = repositoryData.updateAudioGamelanData(
        idAudio,
        deskripsiAudio,
        namaAudioGamelan,
        files
    )

    fun updateDataGamelan(
        idGamelan: String,
        namaGamelan: String?,
        golongan: String?,
        description: String?,
        upacara: List<String>?,
        instrumentId: List<String>?
    ) = repositoryData.updateDataGamelan(
        idGamelan,
        namaGamelan,
        golongan,
        description,
        upacara,
        instrumentId
    )

    fun deleteAudioGamelanByItsId(idAudioGamelan: String) = repositoryData.deleteAudioGamelanByItsId(idAudioGamelan)
    fun fetchAudioGamelanByIdGamelan(id: String) = repositoryData.fetchAudioGamelanByIdGamelan(id)
    fun geDetailGamelanInstrument(id: String) = repositoryData.geDetailGamelanInstrument(id)
    fun fetchInstrumentOnlyNameId() = repositoryData.fetchInstrumentOnlyNameId()
    fun fetchGolonganGamelan() = repositoryData.fetchGolonganGamelan()

}