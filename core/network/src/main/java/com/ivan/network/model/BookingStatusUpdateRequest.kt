package com.wheezy.myjetpackproject.core.network.model

import com.wheezy.myjetpackproject.core.model.BookingStatus

data class BookingStatusUpdateRequest(
    val status: BookingStatus
)