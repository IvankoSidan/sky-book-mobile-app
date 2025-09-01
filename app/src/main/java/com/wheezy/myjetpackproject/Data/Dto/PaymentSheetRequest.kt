package com.wheezy.myjetpackproject.Data.Dto

data class PaymentSheetRequest(
    val bookingId: Long,
    val amount: Long,
    val currency: String
)
