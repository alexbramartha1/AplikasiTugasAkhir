package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class CreateDataSanggarResponse(

	@field:SerializedName("response")
	val response: Response,

	@field:SerializedName("message")
	val message: String
)

data class Response(

	@field:SerializedName("nama_sanggar")
	val namaSanggar: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("message")
	val message: String
)
