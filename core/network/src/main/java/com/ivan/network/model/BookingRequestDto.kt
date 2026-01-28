package com.wheezy.myjetpackproject.core.network.model

data class BookingRequestDto(
    val flightId: Long,
    val seatNumber: String
)