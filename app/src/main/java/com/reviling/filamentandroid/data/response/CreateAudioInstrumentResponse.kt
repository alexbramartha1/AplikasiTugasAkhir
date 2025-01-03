package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class CreateAudioInstrumentResponse(

	@field:SerializedName("audio_data")
	val audioData: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("message")
	val message: String
)
