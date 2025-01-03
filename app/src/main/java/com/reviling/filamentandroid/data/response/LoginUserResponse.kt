package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class LoginUserResponse(

	@field:SerializedName("access_token")
	val accessToken: String,

	@field:SerializedName("createdAtDate")
	val createdAtDate: String,

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("updatedAtDate")
	val updatedAtDate: String,

	@field:SerializedName("updatedAtTime")
	val updatedAtTime: String,

	@field:SerializedName("createdAtTime")
	val createdAtTime: String,

	@field:SerializedName("foto_profile")
	val fotoProfile: String,

	@field:SerializedName("token_type")
	val tokenType: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("status")
	val status: String
)
