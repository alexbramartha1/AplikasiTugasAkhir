package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class KabupatenListResponse(

	@field:SerializedName("kabupaten-data")
	val kabupatenData: List<KabupatenDataItem>
)

data class KabupatenDataItem(

	@field:SerializedName("provinsi_id")
	val provinsiId: String,

	@field:SerializedName("nama_kabupaten")
	val namaKabupaten: String,

	@field:SerializedName("_id")
	val id: String
)
