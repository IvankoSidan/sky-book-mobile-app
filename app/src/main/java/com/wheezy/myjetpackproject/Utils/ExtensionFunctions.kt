package com.wheezy.myjetpackproject.Utils

import androidx.compose.ui.graphics.Color
import com.wheezy.myjetpackproject.Data.Enums.BookingStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.format(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    return this.format(formatter)
}

fun BookingStatus.statusColor(): Color = when (this) {
    BookingStatus.PENDING_PAYMENT -> Color(0xFFFFC107)
    BookingStatus.CONFIRMED -> Color(0xFF4CAF50)
    BookingStatus.FAILED -> Color(0xFFF44336)
    BookingStatus.PAID -> Color(0xFF2196F3)
    BookingStatus.CANCELED -> Color(0xFF9E9E9E)
    BookingStatus.UNPAID -> Color(0xFFFFC107)
}