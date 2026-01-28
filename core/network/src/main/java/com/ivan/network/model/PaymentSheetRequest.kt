package com.wheezy.myjetpackproject.core.network.model

data class PaymentSheetRequest(
    val bookingId: Long,
    val amount: Long,
    val currency: String
)
