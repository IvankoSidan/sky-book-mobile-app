package com.wheezy.myjetpackproject.ViewModel

import androidx.lifecycle.ViewModel
import com.wheezy.myjetpackproject.Data.Model.FlightModel
import com.wheezy.myjetpackproject.Data.Model.Seat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor() : ViewModel() {
    private val _selectedFlight = MutableStateFlow<FlightModel?>(null)
    val selectedFlight: StateFlow<FlightModel?> = _selectedFlight

    private val _selectedSeats = MutableStateFlow<List<Seat>>(emptyList())
    val selectedSeats: StateFlow<List<Seat>> = _selectedSeats

    private val _bookingId = MutableStateFlow<Long?>(null)
    val bookingId: StateFlow<Long?> = _bookingId

    fun setFlight(flight: FlightModel) {
        _selectedFlight.value = flight
    }

    fun setSelectedSeats(seats: List<Seat>) {
        _selectedSeats.value = seats
    }

    fun setBookingId(id: Long?) {
        _bookingId.value = id
    }

    fun clearBooking() {
        _selectedFlight.value = null
        _selectedSeats.value = emptyList()
        _bookingId.value = null
    }
}