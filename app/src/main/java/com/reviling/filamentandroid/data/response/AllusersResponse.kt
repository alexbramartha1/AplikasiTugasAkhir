package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class AllusersResponse(

	@field:SerializedName("data_user")
	val dataUser: List<DataUserItem>
)

data class DataUserItem(

	@field:SerializedName("updateTime")
	val updateTime: String,

	@field:SerializedName("updatedDate")
	val updatedDate: String,

	@field:SerializedName("foto_profile")
	val fotoProfile: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("createdDate")
	val createdDate: String,

	@field:SerializedName("status_id")
	val statusId: String,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("role_id")
	val roleId: String,

	@field:SerializedName("createdTime")
	val createdTime: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("support_document")
	val supportDocument: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String,

)
