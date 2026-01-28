package com.wheezy.myjetpackproject.data.repository

import com.wheezy.myjetpackproject.core.model.BookingStatus
import com.wheezy.myjetpackproject.core.network.ApiService
import com.wheezy.myjetpackproject.core.network.model.BookingDetailsDTO
import com.wheezy.myjetpackproject.core.network.model.BookingStatusUpdateRequest
import retrofit2.Response
import javax.inject.Inject

class BookingRepository @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) {
    private suspend fun bearer() = "Bearer ${authRepository.getToken().orEmpty()}"

    suspend fun getMyBookings(): Response<List<BookingDetailsDTO>> {
        return apiService.getMyBookings(bearer())
    }

    suspend fun updateBookingStatus(bookingId: Long, status: BookingStatus): Response<Unit> {
        return apiService.updateBookingStatus(
            token = bearer(),
            bookingId = bookingId,
            request = BookingStatusUpdateRequest(status)
        )
    }

    suspend fun cancelBooking(bookingId: Long): Response<Unit> {
        return apiService.cancelBooking(bearer(), bookingId)
    }

    suspend fun deleteBooking(bookingId: Long): Response<Unit> {
        return apiService.deleteBooking(bearer(), bookingId)
    }
}