package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class UpdateDataAudioInstrumentResponse(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("updated_data")
    val updatedData: UpdatedDataAudio? = null

)

data class UpdatedDataAudio(

    @field:SerializedName("audio_name")
    val audioName: String? = null,

    @field:SerializedName("audio_path")
    val audioPath: String? = null,

    @field:SerializedName("_id")
    val id: String? = null

)