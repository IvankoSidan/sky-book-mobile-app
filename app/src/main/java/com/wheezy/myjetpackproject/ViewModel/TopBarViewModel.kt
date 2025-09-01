package com.wheezy.myjetpackproject.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wheezy.myjetpackproject.Data.Model.Notification
import com.wheezy.myjetpackproject.Repository.NotificationRepository
import com.wheezy.myjetpackproject.Utils.NotificationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TopBarViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val unreadCount: Int get() = _notifications.value.count { !it.isRead }

    private val TAG = "TopBarViewModel"

    private fun observeEventBus() {
        viewModelScope.launch {
            NotificationEventBus.notificationEvents.collect { event ->
                Log.d(TAG, "Received event from EventBus: ${event.message}")
                addNotification(event.message, event.isRead)
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            Log.d(TAG, "Loading notifications...")
            _isLoading.value = true

            val result = notificationRepository.loadNotifications()
            result?.let {
                Log.d(TAG, "Loaded ${it.size} notifications")
                _notifications.value = it
            } ?: Log.w(TAG, "Failed to load notifications")

            _isLoading.value = false
        }
    }

    fun addNotification(message: String, isRead: Boolean = false) {
        val newNotification = Notification(
            message = message,
            timestamp = LocalDateTime.now(),
            isRead = isRead
        )

        Log.d(TAG, "Adding notification: $message")

        _notifications.value = listOf(newNotification) + _notifications.value

        viewModelScope.launch {
            val success = notificationRepository.saveNotification(newNotification)
            Log.d(TAG, "Notification saved: $success")
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            Log.d(TAG, "Deleting all notifications...")
            _isLoading.value = true

            if (notificationRepository.deleteAllNotifications()) {
                _notifications.value = emptyList()
                Log.d(TAG, "All notifications deleted")
            } else {
                Log.w(TAG, "Failed to delete notifications")
            }

            _isLoading.value = false
        }
    }
}




