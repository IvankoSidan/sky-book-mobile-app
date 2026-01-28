package com.wheezy.myjetpackproject.Data.Dto

import com.wheezy.myjetpackproject.Data.Enums.BookingStatus

data class BookingStatusUpdateRequest(
    val status: BookingStatus
)