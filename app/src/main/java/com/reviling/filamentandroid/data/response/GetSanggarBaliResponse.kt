package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class GetSanggarBaliResponse(

	@field:SerializedName("sanggar_data")
	val sanggarData: List<SanggarDataItem>
)

data class SanggarDataItem(

	@field:SerializedName("provinsi")
	val provinsi: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("desa")
	val desa: String,

	@field:SerializedName("kode_pos")
	val kodePos: String,

	@field:SerializedName("nama_sanggar")
	val namaSanggar: String,

	@field:SerializedName("updateTime")
	val updateTime: String,

	@field:SerializedName("updatedDate")
	val updatedDate: String,

	@field:SerializedName("alamat_lengkap")
	val alamatLengkap: String,

	@field:SerializedName("kabupaten")
	val kabupaten: String,

	@field:SerializedName("id_desa")
	val idDesa: String,

	@field:SerializedName("id_creator")
	val idCreator: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("nama_jalan")
	val namaJalan: String,

	@field:SerializedName("createdDate")
	val createdDate: String,

	@field:SerializedName("gamelan_id")
	val gamelanId: MutableList<String>,

	@field:SerializedName("kecamatan")
	val kecamatan: String,

	@field:SerializedName("createdTime")
	val createdTime: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("deskripsi")
	val deskripsi: String,

	@field:SerializedName("no_telepon")
	val noTelepon: String,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
