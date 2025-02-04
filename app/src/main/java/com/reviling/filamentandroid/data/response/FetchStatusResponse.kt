package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class FetchStatusResponse(

	@field:SerializedName("status_list")
	val statusList: List<StatusListItem>
)

data class StatusListItem(

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("status")
	val status: String
)
