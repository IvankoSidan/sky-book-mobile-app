package com.wheezy.myjetpackproject.Data.Dto

import com.google.gson.annotations.SerializedName

data class GoogleAuthDto(
    @SerializedName("id_token")
    val idToken: String
)