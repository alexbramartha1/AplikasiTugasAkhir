package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class UpdateUsernameResponse(

	@field:SerializedName("updated_data")
	val updatedData: UpdatedData? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class UpdatedData(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: Any? = null
)
