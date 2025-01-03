package com.reviling.filamentandroid.ui.inputinstrument

import androidx.lifecycle.ViewModel
import com.reviling.filamentandroid.data.RepositoryData
import java.io.File

class InputInstrumentViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun createInstrumentData(
        namaInstrumen: String,
        description: String,
        fungsi: String,
        filesImage: List<File>,
        fileTridi: File,
        bahan: List<String>
    ) = repositoryData.createInstrumentData(
        namaInstrumen,
        description,
        fungsi,
        filesImage,
        fileTridi,
        bahan
    )

    fun updateInstrumentData(
        idInstrument: String,
        flagImage: String?,
        namaInstrumen: String?,
        description: String?,
        fungsi: String?,
        filesImage: List<File>?,
        fileTridi: File?,
        bahan: List<String>?
    ) = repositoryData.updateDataInstrumen(
        idInstrument,
        flagImage,
        namaInstrumen,
        description,
        fungsi,
        filesImage,
        fileTridi,
        bahan
    )

    fun createAudioInstrument(
        instrumentId: String,
        namaAudio: String,
        filesAudio: File
    ) = repositoryData.createAudioInstrumentData(
        instrumentId,
        namaAudio,
        filesAudio
    )

    fun updateAudioInstrument(
        idAudio: String,
        namaAudio: String?,
        filesAudio: File?
    ) = repositoryData.updateAudioInstrumentData(
        idAudio,
        namaAudio,
        filesAudio
    )

    fun getDetailInstrumentById(id: String) = repositoryData.geDetailInstrumentById(id)
    fun deleteAudioByItsId(idAudioInstrument: String) = repositoryData.deleteAudioByItsId(idAudioInstrument)
    fun fetchAudioInstrumentById(id: String) = repositoryData.fetchAudioInstrumentById(id)

}