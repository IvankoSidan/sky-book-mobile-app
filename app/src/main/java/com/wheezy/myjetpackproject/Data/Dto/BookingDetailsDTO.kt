package com.wheezy.myjetpackproject.Data.Dto

import com.wheezy.myjetpackproject.Data.Enums.BookingStatus
import com.wheezy.myjetpackproject.Data.Model.Booking
import com.wheezy.myjetpackproject.Data.Model.FlightModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class BookingDetailsDTO(
    val bookingId: Long,
    val seatNumbers: String,
    val seatCount: Int,
    val status: BookingStatus,
    val bookingDate: String,
    val flightId: Long,
    val airlineName: String,
    val airlineLogo: String,
    val departureCity: String,
    val arrivalCity: String,
    val departureTime: String,
    val arriveTime: String,
    val flightDate: String,
    val classSeat: String,
    val price: BigDecimal
)

fun BookingDetailsDTO.toBookingEntity(): Booking {
    return Booking(
        id = bookingId,
        userId = 0L,
        flightId = flightId,
        seatCount = seatCount,
        seatNumbers = seatNumbers,
        status = status,
        bookingDate = LocalDateTime.parse(bookingDate)
    )
}


fun BookingDetailsDTO.toFlightModel(): FlightModel {
    return FlightModel(
        flightId = flightId,
        airlineName = airlineName,
        airlineLogo = airlineLogo,
        departureCity = departureCity,
        arrivalCity = arrivalCity,
        departureTime = departureTime,
        arriveTime = arriveTime,
        flightDate = flightDate,
        classSeat = classSeat,
        price = price,
        departureShort = departureCity.take(3).uppercase(),
        arrivalShort = arrivalCity.take(3).uppercase()
    )
}
