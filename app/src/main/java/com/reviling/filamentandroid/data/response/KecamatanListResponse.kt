package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class KecamatanListResponse(

    @field:SerializedName("kecamatan-data")
    val kecamatanData: List<KecamatanDataItem>
)

data class KecamatanDataItem(

    @field:SerializedName("kabupaten_id")
    val kabupatenId: String,

    @field:SerializedName("nama_kecamatan")
    val namaKecamatan: String,

    @field:SerializedName("_id")
    val id: String
)