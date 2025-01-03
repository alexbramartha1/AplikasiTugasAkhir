package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class FetchInstrumentAudioResponse(

	@field:SerializedName("audio_array")
	val audioArray: List<AudioArrayItem>
)

data class AudioArrayItem(

	@field:SerializedName("audio_name")
	val audioName: String,

	@field:SerializedName("audio_path")
	val audioPath: String,

	@field:SerializedName("_id")
	val id: String,

	var flags: String? = null
)

data class FetchGamelanAudioResponse(

	@field:SerializedName("audio_array")
	val audioArray: List<AudioArrayGamelanItem>
)


data class AudioArrayGamelanItem(

	@field:SerializedName("audio_name")
	val audioName: String,

	@field:SerializedName("audio_path")
	val audioPath: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("deskripsi")
	val deskripsi: String,

	@field:SerializedName("id_gamelan")
	val idGamelan: String,

	var flags: String? = null
)
