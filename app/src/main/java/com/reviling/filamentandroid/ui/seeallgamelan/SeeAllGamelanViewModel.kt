package com.reviling.filamentandroid.ui.seeallgamelan

import androidx.lifecycle.ViewModel
import com.reviling.filamentandroid.data.RepositoryData

class SeeAllGamelanViewModel(private val repositoryData: RepositoryData): ViewModel() {

    fun seeAllGamelanBali() = repositoryData.getAllGamelanBaliData()
    fun getGamelanByGolongan(golongan: String) = repositoryData.getGamelanByGolongan(golongan)
    fun getSearchAllGamelanInstrument(namaGamelan: String) = repositoryData.getSearchGamelanInstrument(namaGamelan)

}