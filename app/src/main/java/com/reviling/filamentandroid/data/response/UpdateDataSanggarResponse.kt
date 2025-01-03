package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class UpdateDataSanggarResponse(

	@field:SerializedName("updated_data")
	val updatedData: UpdatedSanggar? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class UpdatedSanggar(

	@field:SerializedName("provinsi")
	val provinsi: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("desa")
	val desa: String? = null,

	@field:SerializedName("kode_pos")
	val kodePos: String? = null,

	@field:SerializedName("nama_sanggar")
	val namaSanggar: String? = null,

	@field:SerializedName("updateTime")
	val updateTime: String? = null,

	@field:SerializedName("updatedDate")
	val updatedDate: String? = null,

	@field:SerializedName("alamat_lengkap")
	val alamatLengkap: String? = null,

	@field:SerializedName("kabupaten")
	val kabupaten: String? = null,

	@field:SerializedName("id_creator")
	val idCreator: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("nama_jalan")
	val namaJalan: String? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("kecamatan")
	val kecamatan: String? = null,

	@field:SerializedName("createdTime")
	val createdTime: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("no_telepon")
	val noTelepon: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: Any? = null,

	@field:SerializedName("id_desa")
	val idDesa: String? = null
)
