package com.wheezy.myjetpackproject.Data.Dto


data class AuthResponse(
    val user: UserResponseDto,
    val token: String
)


