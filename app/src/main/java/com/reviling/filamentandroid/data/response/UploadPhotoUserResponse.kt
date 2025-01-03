package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class UploadPhotoUserResponse(

	@field:SerializedName("files")
	val files: List<String?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)
