package com.wheezy.myjetpackproject.Data.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Long?,
    val email: String,
    val name: String?,
    val profilePicture: String?
) : Parcelable