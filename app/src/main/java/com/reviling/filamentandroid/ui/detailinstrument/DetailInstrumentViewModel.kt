package com.reviling.filamentandroid.ui.detailinstrument

import androidx.lifecycle.ViewModel
import com.reviling.filamentandroid.data.RepositoryData

class DetailInstrumentViewModel(private val repository: RepositoryData): ViewModel() {

    fun geDetailGamelanByInstrumentId(id: String) = repository.geDetailGamelanByInstrumentId(id)
    fun getDetailInstrumentById(id: String) = repository.geDetailInstrumentById(id)
    fun deleteInstrumentById(id: String) = repository.deleteInstrumentById(id)
    fun deleteAudioByIdList(id: MutableList<String>) = repository.deleteAudioByIdList(id)
}