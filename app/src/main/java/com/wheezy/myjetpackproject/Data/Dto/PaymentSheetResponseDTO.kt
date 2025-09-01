package com.wheezy.myjetpackproject.Data.Dto

data class PaymentSheetResponseDTO(
    val paymentIntentClientSecret: String,
    val ephemeralKey: String?,
    val customerId: String?,
    val publishableKey: String
)
