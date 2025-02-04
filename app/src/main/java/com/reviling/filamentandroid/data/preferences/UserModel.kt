package com.reviling.filamentandroid.data.preferences

data class UserModel(
    val nama: String,
    val access_token: String,
    val email: String,
    val user_id: String,
    val foto_profile: String,
    val role: String,
    val status: String,
    val isLogin: Boolean = false,
    val document: String
)