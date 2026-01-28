package com.wheezy.myjetpackproject.core.network.model


data class AuthResponse(
    val user: UserResponseDto,
    val token: String
)


