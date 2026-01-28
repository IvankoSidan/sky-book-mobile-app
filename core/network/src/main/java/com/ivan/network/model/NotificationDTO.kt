package com.wheezy.myjetpackproject.core.network.model

data class NotificationDTO(
    val message: String,
    val timestamp: String,
    val isRead: Boolean
)
