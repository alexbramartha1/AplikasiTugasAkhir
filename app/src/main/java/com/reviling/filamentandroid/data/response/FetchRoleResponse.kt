package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class FetchRoleResponse(

	@field:SerializedName("role_list")
	val roleList: List<RoleListItem>
)

data class RoleListItem(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("default_status_id")
	val defaultStatusId: String,

	@field:SerializedName("_id")
	val id: String
)
