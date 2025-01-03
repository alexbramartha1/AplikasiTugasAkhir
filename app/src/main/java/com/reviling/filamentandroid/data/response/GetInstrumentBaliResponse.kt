package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class GetInstrumentBaliResponse(

	@field:SerializedName("instrument_data")
	val instrumentData: List<InstrumentDataItem>
)

data class InstrumentDataItem(

	@field:SerializedName("nama_instrument")
	val namaInstrument: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("updateTime")
	val updateTime: String,

	@field:SerializedName("updatedDate")
	val updatedDate: String,

	@field:SerializedName("fungsi")
	val fungsi: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("createdDate")
	val createdDate: String,

	@field:SerializedName("trid_image")
	val tridImage: String,

	@field:SerializedName("createdTime")
	val createdTime: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("image_instrumen")
	val imageInstrumen: List<String>,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String,

	@field:SerializedName("bahan")
	val bahan: MutableList<String>,

	@SerializedName("audio_data")
	val audioInstrumen: List<AudioInstrumenItem>
)

data class AudioInstrumenItem(
	@SerializedName("_id")
	val id: String,

	@SerializedName("instrument_id")
	val idInstrument: String,

	@SerializedName("audio_name")
	val audioName: String,

	@SerializedName("audio_path")
	val audioPath: String
)
