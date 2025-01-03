package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class CreateGamelanResponse(

    @field:SerializedName("nama_gamelan")
    val namaGamelan: String,

    @field:SerializedName("_id")
    val id: String,

    @field:SerializedName("message")
    val message: String

)
