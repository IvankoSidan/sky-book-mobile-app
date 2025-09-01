package com.wheezy.myjetpackproject.Data.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(
    val id: Int = 0,
    val name: String =  ""
) : Parcelable