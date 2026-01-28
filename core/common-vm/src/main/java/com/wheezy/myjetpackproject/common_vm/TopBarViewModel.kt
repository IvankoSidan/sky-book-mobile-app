package com.wheezy.myjetpackproject.core.common_vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wheezy.myjetpackproject.core.model.Notification
import com.wheezy.myjetpackproject.data.repository.NotificationRepository
import com.wheezy.myjetpackproject.core.common.utils.NotificationEventBus
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

    init {
        observeEventBus()
        loadNotifications()
    }

    private fun observeEventBus() {
        viewModelScope.launch {
            NotificationEventBus.notificationEvents.collect { event ->
                addNotification(event.message, event.isRead)
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = notificationRepository.loadNotifications()
            result?.let {
                _notifications.value = it
            }
            _isLoading.value = false
        }
    }

    fun addNotification(message: String, isRead: Boolean = false) {
        val newNotification = Notification(
            message = message,
            timestamp = LocalDateTime.now(),
            isRead = isRead
        )
        _notifications.value = listOf(newNotification) + _notifications.value
        viewModelScope.launch {
            notificationRepository.saveNotification(newNotification)
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            if (notificationRepository.deleteAllNotifications()) {
                _notifications.value = emptyList()
            }
            _isLoading.value = false
        }
    }
}