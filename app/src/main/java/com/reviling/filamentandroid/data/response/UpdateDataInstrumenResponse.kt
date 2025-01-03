package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class UpdateDataInstrumenResponse(

    @field:SerializedName("Updated_data")
    val updatedData: UpdatedInstrumentDataItem? = null,

    @field:SerializedName("message")
    val message: String? = null

)

data class UpdatedInstrumentDataItem(

    @field:SerializedName("nama_instrument")
    val namaInstrument: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("updateTime")
    val updateTime: String? = null,

    @field:SerializedName("updatedDate")
    val updatedDate: String? = null,

    @field:SerializedName("fungsi")
    val fungsi: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("createdDate")
    val createdDate: String? = null,

    @field:SerializedName("trid_image")
    val tridImage: String? = null,

    @field:SerializedName("createdTime")
    val createdTime: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("image_instrumen")
    val imageInstrumen: List<String>? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("bahan")
    val bahan: MutableList<String>? = null,

    @SerializedName("audio_data")
    val audioInstrumen: List<UpdateAudioInstrumenItem>? = null
)

data class UpdateAudioInstrumenItem(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("instrument_id")
    val idInstrument: String? = null,

    @SerializedName("audio_name")
    val audioName: String? = null,

    @SerializedName("audio_path")
    val audioPath: String? = null
)