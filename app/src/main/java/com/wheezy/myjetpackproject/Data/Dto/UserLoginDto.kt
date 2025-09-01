package com.wheezy.myjetpackproject.Data.Dto

import com.google.gson.annotations.SerializedName

data class UserLoginDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)