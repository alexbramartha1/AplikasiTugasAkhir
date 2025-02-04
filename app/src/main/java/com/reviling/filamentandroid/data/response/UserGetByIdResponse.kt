package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class UserGetByIdResponse(

	@field:SerializedName("role_id")
	val role: String,

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

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("createdTime")
	val createdTime: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String,

	@field:SerializedName("status_id")
	val status: String,

	@field:SerializedName("support_document")
	val supportDocument: String
)