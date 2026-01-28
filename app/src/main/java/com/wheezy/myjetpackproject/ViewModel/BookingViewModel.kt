package com.wheezy.myjetpackproject.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wheezy.myjetpackproject.Data.Dto.BookingDetailsDTO
import com.wheezy.myjetpackproject.Data.Dto.BookingRequestDto
import com.wheezy.myjetpackproject.Data.Dto.PaymentSheetRequest
import com.wheezy.myjetpackproject.Data.Dto.PaymentSheetResponseDTO
import com.wheezy.myjetpackproject.Data.Enums.BookingStatus
import com.wheezy.myjetpackproject.Data.Enums.canBeCancelled
import com.wheezy.myjetpackproject.Data.Enums.canBeDeleted
import com.wheezy.myjetpackproject.Data.Model.FlightModel
import com.wheezy.myjetpackproject.Data.Model.Seat
import com.wheezy.myjetpackproject.Repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _selectedFlight = MutableStateFlow<FlightModel?>(null)
    val selectedFlight: StateFlow<FlightModel?> = _selectedFlight

    private val _selectedSeats = MutableStateFlow<List<Seat>>(emptyList())
    val selectedSeats: StateFlow<List<Seat>> = _selectedSeats

    private val _bookingId = MutableStateFlow<Long?>(null)
    val bookingId: StateFlow<Long?> = _bookingId

    private val _bookings = MutableStateFlow<List<BookingDetailsDTO>>(emptyList())
    val bookings: StateFlow<List<BookingDetailsDTO>> = _bookings

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loadingBookingIds = MutableStateFlow<Set<Long>>(emptySet())
    val loadingBookingIds: StateFlow<Set<Long>> = _loadingBookingIds


    fun loadMyBookings() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = bookingRepository.getMyBookings()
                if (response.isSuccessful) {
                    _bookings.value = response.body().orEmpty()
                } else {
                    _error.value = "Download error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelOrDeleteBooking(bookingId: Long, status: BookingStatus) {
        viewModelScope.launch {
            _loadingBookingIds.value = _loadingBookingIds.value + bookingId
            _error.value = null

            val original = _bookings.value
            try {
                when {
                    status.canBeDeleted() -> {
                        _bookings.value = original.filterNot { it.bookingId == bookingId }

                        val response = bookingRepository.deleteBooking(bookingId)
                        if (!response.isSuccessful) {
                            _bookings.value = original
                            _error.value = "Delete error: ${response.code()}"
                        }
                    }

                    status.canBeCancelled() -> {
                        _bookings.value = original.map {
                            if (it.bookingId == bookingId) it.copy(status = BookingStatus.CANCELED) else it
                        }

                        val response = bookingRepository.cancelBooking(bookingId)
                        if (!response.isSuccessful) {
                            _bookings.value = original
                            _error.value = "Cancel error: ${response.code()}"
                        }
                    }

                    else -> {
                        _error.value = "Action not allowed for status: $status"
                    }
                }
            } catch (e: Exception) {
                _bookings.value = original
                _error.value = e.message
            } finally {
                _loadingBookingIds.value = _loadingBookingIds.value - bookingId
            }
        }
    }

    fun updateBookingStatus(bookingId: Long, newStatus: BookingStatus) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val original = _bookings.value
            try {
                val response = bookingRepository.updateBookingStatus(bookingId, newStatus)
                if (response.isSuccessful) {
                    _bookings.value = original.map {
                        if (it.bookingId == bookingId) it.copy(status = newStatus) else it
                    }
                } else {
                    _error.value = "Status update error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

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

