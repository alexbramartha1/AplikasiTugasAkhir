package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class CreateInstrumentResponse(

	@field:SerializedName("response")
	val response: InstrumentResponse,

	@field:SerializedName("message")
	val message: String
)

data class InstrumentResponse(

	@field:SerializedName("nama_instrument")
	val namaInstrument: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("message")
	val message: String
)
