package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class FetchGolonganListResponse(

	@field:SerializedName("golongan_list")
	val golonganList: List<GolonganListItem>
)

data class GolonganListItem(

	@field:SerializedName("golongan")
	val golongan: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("deskripsi")
	val deskripsi: String
)
