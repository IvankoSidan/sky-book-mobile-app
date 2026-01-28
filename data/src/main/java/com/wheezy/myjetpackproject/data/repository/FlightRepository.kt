package com.wheezy.myjetpackproject.data.repository

import com.wheezy.myjetpackproject.core.model.FlightModel
import com.wheezy.myjetpackproject.core.network.ApiService
import com.wheezy.myjetpackproject.core.network.model.BookingRequestDto
import com.wheezy.myjetpackproject.core.network.model.BookingResponseDTO
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

class FlightRepository @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) {
    private suspend fun bearer() = "Bearer ${authRepository.getToken().orEmpty()}"

    suspend fun getAllFlights() = apiService.getAllFlights(bearer())

    suspend fun getFlightById(id: Long) = apiService.getFlightById(bearer(), id)

    suspend fun searchFlights(from: String, to: String, date: String?) =
        apiService.searchFlights(bearer(), from, to, date)

    suspend fun createFlight(flight: FlightModel) =
        apiService.createFlight(bearer(), flight)

    suspend fun updateFlight(id: Long, flight: FlightModel) =
        apiService.updateFlight(bearer(), id, flight)

    suspend fun deleteFlight(id: Long) =
        apiService.deleteFlight(bearer(), id)

    suspend fun getClassSeats() = apiService.getClassSeats(bearer())

    suspend fun createBooking(
        token: String,
        bookingDto: BookingRequestDto
    ): Response<BookingResponseDTO> {
        return try {
            apiService.createBooking(token, bookingDto)
        } catch (e: Exception) {
            Response.error(500, "Error creating booking".toResponseBody())
        }
    }

    suspend fun getReservedSeats(flightId: Long): List<String> {
        val token = authRepository.getToken() ?: return emptyList()
        return try {
            val response = apiService.getBookedSeats("Bearer $token", flightId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

