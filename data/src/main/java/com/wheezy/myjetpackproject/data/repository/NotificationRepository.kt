package com.wheezy.myjetpackproject.data.repository

import javax.inject.Inject
import android.util.Log
import com.wheezy.myjetpackproject.core.model.Notification
import com.wheezy.myjetpackproject.core.network.ApiService
import com.wheezy.myjetpackproject.core.network.model.CreateNotificationRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NotificationRepository @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) {
    private val TAG = "NotificationRepository"

    private suspend fun authHeader(): String? {
        val token = authRepository.getToken()
        Log.d(TAG, "Token: $token")
        return token?.let { "Bearer $it" }
    }


    suspend fun loadNotifications(): List<Notification>? = authHeader()?.let { token ->
        runCatching {
            val response = apiService.getNotifications(token)
            Log.d(TAG, "Response code: ${response.code()}")
            if (response.isSuccessful) {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                response.body()?.map {
                    Notification(
                        message = it.message,
                        timestamp = LocalDateTime.parse(it.timestamp, formatter),
                        isRead = it.isRead
                    )
                }
            } else {
                Log.w(
                    TAG,
                    "Failed to load notifications. Error body: ${response.errorBody()?.string()}"
                )
                null
            }
        }.onFailure {
            Log.e(TAG, "Exception while loading notifications", it)
        }.getOrNull()
    }

    suspend fun deleteAllNotifications(): Boolean = authHeader()?.let { token ->
        runCatching {
            val success = apiService.deleteAllNotifications(token).isSuccessful
            Log.d(TAG, "Delete all notifications success: $success")
            success
        }.getOrDefault(false)
    } ?: false

    suspend fun saveNotification(notification: Notification): Boolean = authHeader()?.let { token ->
        runCatching {
            val request = CreateNotificationRequest(
                notification.message,
                notification.isRead
            )
            val response = apiService.saveNotification(token, request)
            val success = response.isSuccessful
            Log.d(TAG, "Save notification success: $success")
            success
        }.onFailure {
            Log.e(TAG, "Exception while saving notification", it)
        }.getOrDefault(false)
    } ?: false
}

