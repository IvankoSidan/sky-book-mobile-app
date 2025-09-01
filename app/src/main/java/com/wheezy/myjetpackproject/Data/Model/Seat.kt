package com.wheezy.myjetpackproject.Data.Model

import android.os.Parcelable
import com.wheezy.myjetpackproject.Data.Enums.SeatStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class Seat(
    var status: SeatStatus,
    var name: String
) : Parcelable
