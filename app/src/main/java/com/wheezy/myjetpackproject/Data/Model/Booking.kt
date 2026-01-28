package com.wheezy.myjetpackproject.Data.Model

import com.wheezy.myjetpackproject.Data.Dto.BookingDetailsDTO
import com.wheezy.myjetpackproject.Data.Enums.BookingStatus
import java.time.LocalDateTime


data class Booking(
    val id: Long = 0,
    val userId: Long,
    val flightId: Long,
    val seatCount: Int = 1,
    val seatNumbers: String,
    var status: BookingStatus,
    val bookingDate: LocalDateTime = LocalDateTime.now()
)
