package com.reviling.filamentandroid.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.reviling.filamentandroid.data.response.AudioArrayGamelanItem
import com.reviling.filamentandroid.data.response.AudioArrayItem
import com.reviling.filamentandroid.data.response.AudioGamelanItem
import com.reviling.filamentandroid.data.response.AudioInstrumenItem
import com.reviling.filamentandroid.data.response.DataUserItem
import com.reviling.filamentandroid.data.response.GamelanDataItem
import com.reviling.filamentandroid.data.response.InstrumentDataItem
import com.reviling.filamentandroid.data.response.SanggarDataItem

class GamelanBaliDiffCallback(private val oldGamelanList: List<GamelanDataItem>, private val newGamelanList: List<GamelanDataItem>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldGamelanList.size
    override fun getNewListSize(): Int = newGamelanList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldGamelanList[oldItemPosition].namaGamelan == newGamelanList[newItemPosition].namaGamelan
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldGamelanList[oldItemPosition]
        val newData = newGamelanList[newItemPosition]
        return oldData.namaGamelan == newData.namaGamelan && oldData.golongan == newData.golongan
    }
}

class InstrumentBaliDiffCallback(private val oldInstrumentList: List<InstrumentDataItem>, private val newInstrumentList: List<InstrumentDataItem>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldInstrumentList.size
    override fun getNewListSize(): Int = newInstrumentList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldInstrumentList[oldItemPosition].namaInstrument == newInstrumentList[newItemPosition].namaInstrument
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldInstrumentList[oldItemPosition]
        val newData = newInstrumentList[newItemPosition]
        return oldData.namaInstrument == newData.namaInstrument && oldData.fungsi == newData.fungsi
    }
}

class SanggarBaliDiffCallback(private val oldSanggarList: List<SanggarDataItem>, private val newSanggarList: List<SanggarDataItem>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldSanggarList.size
    override fun getNewListSize(): Int = newSanggarList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldSanggarList[oldItemPosition].namaSanggar == newSanggarList[newItemPosition].namaSanggar
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldSanggarList[oldItemPosition]
        val newData = newSanggarList[newItemPosition]
        return oldData.namaSanggar == newData.namaSanggar && oldData.namaJalan == newData.namaJalan
    }
}

class AudioBaliDiffCallback(private val olAudioList: List<AudioGamelanItem>, private val newAudioList: List<AudioGamelanItem>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = olAudioList.size
    override fun getNewListSize(): Int = newAudioList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return olAudioList[oldItemPosition].audioName == newAudioList[newItemPosition].audioName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = olAudioList[oldItemPosition]
        val newData = newAudioList[newItemPosition]
        return oldData.audioName == newData.audioName && oldData.audioPath == newData.audioPath
    }
}

class UpacaraDiffCallback(private val oldGamelanList: List<String>, private val newGamelanList: List<String>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldGamelanList.size
    override fun getNewListSize(): Int = newGamelanList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldGamelanList[oldItemPosition] == newGamelanList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldGamelanList[oldItemPosition]
        val newData = newGamelanList[newItemPosition]
        return oldData == newData && oldData == newData
    }
}

class MaterialDiffCallback(private val oldGamelanList: List<String>, private val newGamelanList: List<String>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldGamelanList.size
    override fun getNewListSize(): Int = newGamelanList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldGamelanList[oldItemPosition] == newGamelanList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldGamelanList[oldItemPosition]
        val newData = newGamelanList[newItemPosition]
        return oldData == newData && oldData == newData
    }
}

class materialInputDiffCallback(private val oldGamelanList: List<String>, private val newGamelanList: List<String>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldGamelanList.size
    override fun getNewListSize(): Int = newGamelanList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldGamelanList[oldItemPosition] == newGamelanList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldGamelanList[oldItemPosition]
        val newData = newGamelanList[newItemPosition]
        return oldData == newData && oldData == newData
    }
}

class AllUsersDataDiffCallback(private val oldUsersList: List<DataUserItem>, private val newUsersList: List<DataUserItem>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldUsersList.size
    override fun getNewListSize(): Int = newUsersList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUsersList[oldItemPosition] == newUsersList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldUsersList[oldItemPosition]
        val newData = newUsersList[newItemPosition]
        return oldData.id == newData.id && oldData.nama == newData.nama
    }
}

class AudioInstrumentDiffCallback(private val olAudioList: List<AudioInstrumenItem>, private val newAudioList: List<AudioInstrumenItem>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = olAudioList.size
    override fun getNewListSize(): Int = newAudioList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return olAudioList[oldItemPosition].audioName == newAudioList[newItemPosition].audioName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = olAudioList[oldItemPosition]
        val newData = newAudioList[newItemPosition]
        return oldData.audioName == newData.audioName && oldData.audioPath == newData.audioPath
    }
}

class AudioInstrumentInputDiffCallback(private val olAudioList: List<AudioArrayItem>, private val newAudioList: List<AudioArrayItem>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = olAudioList.size
    override fun getNewListSize(): Int = newAudioList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return olAudioList[oldItemPosition].audioName == newAudioList[newItemPosition].audioName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = olAudioList[oldItemPosition]
        val newData = newAudioList[newItemPosition]
        return oldData.audioName == newData.audioName && oldData.audioPath == newData.audioPath
    }
}

class AudioGamelanInputDiffCallback(private val olAudioList: List<AudioArrayGamelanItem>, private val newAudioList: List<AudioArrayGamelanItem>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = olAudioList.size
    override fun getNewListSize(): Int = newAudioList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return olAudioList[oldItemPosition].audioName == newAudioList[newItemPosition].audioName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = olAudioList[oldItemPosition]
        val newData = newAudioList[newItemPosition]
        return oldData.audioName == newData.audioName && oldData.audioPath == newData.audioPath
    }
}

