package com.wheezy.myjetpackproject.Data.Dto

import com.wheezy.myjetpackproject.Data.Model.User

data class UserResponseDto(
    val id: Long,
    val email: String,
    val name: String?,
    val profilePicture: String?
)