package com.wheezy.myjetpackproject.core.network

import com.wheezy.myjetpackproject.core.network.model.PaymentSheetResponseDTO
import com.wheezy.myjetpackproject.core.model.FlightModel
import com.wheezy.myjetpackproject.core.model.LocationModel
import com.wheezy.myjetpackproject.core.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body userRegisterDto: UserRegisterDto): Response<AuthResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body userLoginDto: UserLoginDto): Response<AuthResponse>

    @POST("/api/auth/google")
    suspend fun googleAuth(@Body googleAuthDto: GoogleAuthDto): Response<AuthResponse>

    @GET("/api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<AuthResponse>

    @GET("/api/flights/locations")
    suspend fun getLocations(@Header("Authorization") bearer: String): Response<List<LocationModel>>

    @GET("/api/flights")
    suspend fun getAllFlights(@Header("Authorization") bearer: String): Response<List<FlightModel>>

    @GET("/api/flights/{id}")
    suspend fun getFlightById(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<FlightModel>

    @GET("/api/flights/search")
    suspend fun searchFlights(
        @Header("Authorization") bearer: String,
        @Query("departureCity") from: String,
        @Query("arrivalCity") to: String,
        @Query("flightDate") date: String? = null
    ): Response<List<FlightModel>>

    @POST("/api/flights")
    suspend fun createFlight(
        @Header("Authorization") bearer: String,
        @Body flight: FlightModel
    ): Response<FlightModel>

    @PUT("/api/flights/{id}")
    suspend fun updateFlight(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long,
        @Body flight: FlightModel
    ): Response<FlightModel>

    @DELETE("/api/flights/{id}")
    suspend fun deleteFlight(
        @Header("Authorization") bearer: String,
        @Path("id") id: Long
    ): Response<Unit>

    @GET("/api/flights/class-seats")
    suspend fun getClassSeats(@Header("Authorization") bearer: String): Response<List<String>>

    @POST("/api/bookings")
    suspend fun createBooking(
        @Header("Authorization") bearer: String,
        @Body bookingDto: BookingRequestDto
    ): Response<BookingResponseDTO>

    @GET("/api/bookings/flight/{flightId}")
    suspend fun getBookedSeats(
        @Header("Authorization") bearer: String,
        @Path("flightId") flightId: Long
    ): Response<List<String>>

    @GET("/api/notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): Response<List<NotificationDTO>>

    @POST("/api/notifications/delete-all")
    suspend fun deleteAllNotifications(@Header("Authorization") token: String): Response<Void>

    @POST("/api/notifications")
    suspend fun saveNotification(
        @Header("Authorization") token: String,
        @Body notification: CreateNotificationRequest
    ): Response<NotificationDTO>

    @POST("/api/payments/sheet")
    suspend fun createPaymentSheet(
        @Header("Authorization") bearer: String,
        @Body request: PaymentSheetRequest
    ): Response<PaymentSheetResponseDTO>

    @GET("/api/bookings/my")
    suspend fun getMyBookings(@Header("Authorization") bearer: String): Response<List<BookingDetailsDTO>>

    @PUT("bookings/{bookingId}/status")
    suspend fun updateBookingStatus(
        @Header("Authorization") token: String,
        @Path("bookingId") bookingId: Long,
        @Body request: BookingStatusUpdateRequest
    ): Response<Unit>

    @POST("bookings/{id}/cancel")
    suspend fun cancelBooking(@Header("Authorization") bearer: String, @Path("id") id: Long): Response<Unit>

    @DELETE("bookings/{id}")
    suspend fun deleteBooking(@Header("Authorization") bearer: String, @Path("id") id: Long): Response<Unit>
}