package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class GetGamelanBaliResponse(
	@SerializedName("gamelan_data")
	val gamelanData: List<GamelanDataItem>
)

data class GamelanDataItem(
	@SerializedName("_id")
	val id: String,

	@SerializedName("nama_gamelan")
	val namaGamelan: String,

	@SerializedName("golongan_id")
	val golonganId: String,

	@SerializedName("golongan")
	val golongan: String,

	@SerializedName("description")
	val description: String,

	@SerializedName("upacara")
	val upacara: List<String>,

	@SerializedName("instrument_id")
	val instrumentId: List<String>,

	@SerializedName("status_id")
	val status: String,

	@SerializedName("createdAt")
	val createdAt: String,

	@SerializedName("createdDate")
	val createdDate: String,

	@SerializedName("createdTime")
	val createdTime: String,

	@SerializedName("updatedAt")
	val updatedAt: String,

	@SerializedName("updatedDate")
	val updatedDate: String,

	@SerializedName("updateTime")
	val updateTime: String,

	@SerializedName("audio_gamelan")
	val audioGamelan: List<AudioGamelanItem>
)

data class AudioGamelanItem(
	@SerializedName("_id")
	val id: String,

	@SerializedName("id_gamelan")
	val idGamelan: String,

	@SerializedName("audio_name")
	val audioName: String,

	@SerializedName("audio_path")
	val audioPath: String,

	@SerializedName("deskripsi")
	val audioDesc: String
)

