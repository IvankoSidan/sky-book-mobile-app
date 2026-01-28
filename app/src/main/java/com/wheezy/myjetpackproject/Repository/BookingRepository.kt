package com.wheezy.myjetpackproject.Repository

import com.wheezy.myjetpackproject.Data.Dto.BookingDetailsDTO
import com.wheezy.myjetpackproject.Data.Dto.BookingRequestDto
import com.wheezy.myjetpackproject.Data.Dto.BookingResponseDTO
import com.wheezy.myjetpackproject.Data.Dto.BookingStatusUpdateRequest
import com.wheezy.myjetpackproject.Data.Dto.PaymentSheetRequest
import com.wheezy.myjetpackproject.Data.Dto.PaymentSheetResponseDTO
import com.wheezy.myjetpackproject.Data.Enums.BookingStatus
import com.wheezy.myjetpackproject.Network.ApiService
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
        return apiService.updateBookingStatus(bookingId, BookingStatusUpdateRequest(status))
    }

    suspend fun cancelBooking(bookingId: Long): Response<Unit> {
        return apiService.cancelBooking(bearer(), bookingId)
    }

    suspend fun deleteBooking(bookingId: Long): Response<Unit> {
        return apiService.deleteBooking(bearer(), bookingId)
    }
}