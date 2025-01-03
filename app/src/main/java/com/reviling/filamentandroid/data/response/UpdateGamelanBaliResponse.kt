package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class UpdateGamelanBaliResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("updated_data")
    val updatedData: UpdatedDataGamelan? = null

)

data class UpdatedDataGamelan(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("nama_gamelan")
    val namaGamelan: String? = null,

    @SerializedName("golongan_id")
    val golonganId: String? = null,

    @SerializedName("golongan")
    val golongan: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("upacara")
    val upacara: List<String>? = null,

    @SerializedName("instrument_id")
    val instrumentId: List<String>? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("createdDate")
    val createdDate: String? = null,

    @SerializedName("createdTime")
    val createdTime: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("updatedDate")
    val updatedDate: String? = null,

    @SerializedName("updateTime")
    val updateTime: String? = null,

    @SerializedName("audio_gamelan")
    val audioGamelan: List<AudioGamelanEditResponse>? = null
)

data class AudioGamelanEditResponse(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("id_gamelan")
    val idGamelan: String? = null,

    @SerializedName("audio_name")
    val audioName: String? = null,

    @SerializedName("audio_path")
    val audioPath: String? = null,

    @SerializedName("deskripsi")
    val audioDesc: String? = null
)

