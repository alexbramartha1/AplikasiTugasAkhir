package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class NoteAdminResponse(

	@field:SerializedName("note")
	val note: String,

	@field:SerializedName("id_data")
	val idData: String,

	@field:SerializedName("id_status")
	val idStatus: String,

	@field:SerializedName("_id")
	val id: String
)
