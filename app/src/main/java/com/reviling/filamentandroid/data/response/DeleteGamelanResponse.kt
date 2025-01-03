package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class DeleteGamelanResponse(

    @field:SerializedName("Message")
    val message: String,

    @field:SerializedName("_idGamelan")
    val idGamelan: String

)
