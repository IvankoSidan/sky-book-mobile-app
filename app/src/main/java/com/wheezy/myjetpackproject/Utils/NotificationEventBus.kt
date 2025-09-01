package com.wheezy.myjetpackproject.Utils

import com.wheezy.myjetpackproject.Data.Dto.NotificationEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationEventBus {
    private val _notificationEvents = MutableSharedFlow<NotificationEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val notificationEvents: SharedFlow<NotificationEvent> = _notificationEvents.asSharedFlow()

    suspend fun sendNotificationEvent(message: String, isRead: Boolean = false) {
        _notificationEvents.emit(NotificationEvent(message, isRead))
    }
}
