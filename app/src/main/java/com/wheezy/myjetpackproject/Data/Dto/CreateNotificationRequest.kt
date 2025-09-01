package com.wheezy.myjetpackproject.Data.Dto

data class CreateNotificationRequest(
    val message: String,
    val isRead: Boolean? = false
)
