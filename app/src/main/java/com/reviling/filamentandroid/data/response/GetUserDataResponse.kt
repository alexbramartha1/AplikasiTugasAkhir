package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class GetUserDataResponse(
	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
