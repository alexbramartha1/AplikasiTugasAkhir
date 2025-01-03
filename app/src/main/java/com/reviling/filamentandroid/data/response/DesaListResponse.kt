package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class DesaListResponse(

    @field:SerializedName("desa-data")
    val desaData: List<DesaDataItem>
)

data class DesaDataItem(

    @field:SerializedName("kecamatan_id")
    val kecamatanId: String,

    @field:SerializedName("nama_desa")
    val namaDesa: String,

    @field:SerializedName("_id")
    val id: String
)