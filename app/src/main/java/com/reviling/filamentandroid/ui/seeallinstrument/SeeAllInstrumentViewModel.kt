package com.reviling.filamentandroid.ui.seeallinstrument

import androidx.lifecycle.ViewModel
import com.reviling.filamentandroid.data.RepositoryData

class SeeAllInstrumentViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun getAllInstrumentData() = repositoryData.getAllInstrumentBaliData()
    fun getInstrumentByName(namaInstrument: String) = repositoryData.getInstrumentByName(namaInstrument)

}