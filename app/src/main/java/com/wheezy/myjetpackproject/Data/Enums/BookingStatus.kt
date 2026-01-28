package com.wheezy.myjetpackproject.Data.Enums

enum class BookingStatus {
    PENDING_PAYMENT,
    CONFIRMED,
    FAILED,
    CANCELED,
    PAID,
    UNPAID
}

fun BookingStatus.canBePaid(): Boolean = this == BookingStatus.PENDING_PAYMENT
fun BookingStatus.canBeCancelled(): Boolean =
    this == BookingStatus.CONFIRMED || this == BookingStatus.PENDING_PAYMENT
fun BookingStatus.canBeDeleted(): Boolean =
    this == BookingStatus.PENDING_PAYMENT ||
            this == BookingStatus.FAILED ||
            this == BookingStatus.UNPAID ||
            this == BookingStatus.CANCELED

