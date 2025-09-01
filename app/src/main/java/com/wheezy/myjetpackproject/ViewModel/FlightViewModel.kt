package com.wheezy.myjetpackproject.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wheezy.myjetpackproject.Data.Dto.BookingRequestDto
import com.wheezy.myjetpackproject.Data.Enums.SeatStatus
import com.wheezy.myjetpackproject.Data.Model.FlightModel
import com.wheezy.myjetpackproject.Data.Model.LocationModel
import com.wheezy.myjetpackproject.Data.Model.Seat
import com.wheezy.myjetpackproject.Repository.AuthRepository
import com.wheezy.myjetpackproject.Repository.FlightRepository
import com.wheezy.myjetpackproject.Repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class FlightViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val flightRepository: FlightRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _locations = MutableStateFlow<List<LocationModel>>(emptyList())
    val locations: StateFlow<List<LocationModel>> = _locations.asStateFlow()

    private val _locationsLoading = MutableStateFlow(false)
    val locationsLoading: StateFlow<Boolean> = _locationsLoading.asStateFlow()

    private val _locationsError = MutableStateFlow<String?>(null)
    val locationsError: StateFlow<String?> = _locationsError.asStateFlow()

    private val _flights = MutableStateFlow<List<FlightModel>>(emptyList())
    val flights: StateFlow<List<FlightModel>> = _flights.asStateFlow()

    private val _flightsLoading = MutableStateFlow(false)
    val flightsLoading: StateFlow<Boolean> = _flightsLoading.asStateFlow()

    private val _flightsError = MutableStateFlow<String?>(null)
    val flightsError: StateFlow<String?> = _flightsError.asStateFlow()

    private val _selectedSeats = MutableStateFlow<List<Seat>>(emptyList())
    val selectedSeats: StateFlow<List<Seat>> = _selectedSeats.asStateFlow()

    private val _totalPrice = MutableStateFlow(BigDecimal.ZERO)
    val totalPrice: StateFlow<BigDecimal> = _totalPrice.asStateFlow()

    private val _selectedFlight = MutableStateFlow<FlightModel?>(null)
    val selectedFlight: StateFlow<FlightModel?> = _selectedFlight.asStateFlow()

    private val _seatList = MutableStateFlow<List<Seat>>(emptyList())
    val seatList: StateFlow<List<Seat>> = _seatList.asStateFlow()

    private val _classSeats = MutableStateFlow<List<String>>(emptyList())
    val classSeats: StateFlow<List<String>> = _classSeats.asStateFlow()

    private val _classSeatsLoading = MutableStateFlow(false)
    val classSeatsLoading: StateFlow<Boolean> = _classSeatsLoading.asStateFlow()

    private val _classSeatsError = MutableStateFlow<String?>(null)
    val classSeatsError: StateFlow<String?> = _classSeatsError.asStateFlow()

    private val _reservedSeats = MutableStateFlow<List<String>>(emptyList())
    val reservedSeats: StateFlow<List<String>> = _reservedSeats.asStateFlow()

    fun fetchClassSeats() {
        viewModelScope.launch {
            _classSeatsLoading.value = true
            _classSeatsError.value = null
            try {
                val response = flightRepository.getClassSeats()
                if (response.isSuccessful) {
                    _classSeats.value = response.body() ?: emptyList()
                } else {
                    _classSeatsError.value = "Failed to load class seats: ${response.message()}"
                }
            } catch (e: Exception) {
                _classSeatsError.value = "Error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _classSeatsLoading.value = false
            }
        }
    }

    fun generateSeatList(flightModel: FlightModel) {
        val seatList = mutableListOf<Seat>()
        val numberSeat = flightModel.totalSeats + (flightModel.totalSeats / 7) + 1
        val seatAlphabetMap = mapOf(
            0 to "A", 1 to "B", 2 to "C", 4 to "D",
            5 to "E", 6 to "F"
        )

        var row = 0
        for (i in 0 until numberSeat) {
            if (i % 7 == 0) row++
            if (i % 7 == 3) {
                seatList.add(Seat(SeatStatus.EMPTY, row.toString()))
            } else {
                val seatName = seatAlphabetMap[i % 7]?.plus(row.toString()) ?: row.toString()
                val seatStatus = when {
                    _reservedSeats.value.contains(seatName) -> SeatStatus.UNAVAILABLE
                    flightModel.reservedSeats.contains(seatName) -> SeatStatus.UNAVAILABLE
                    else -> SeatStatus.AVAILABLE
                }
                seatList.add(Seat(seatStatus, seatName))
            }
        }
        _seatList.value = seatList
    }

    fun selectFlight(flight: FlightModel) {
        _selectedFlight.value = flight
        generateSeatList(flight)
        clearSelection()
    }

    fun fetchLocations() {
        viewModelScope.launch {
            _locationsLoading.value = true
            _locationsError.value = null
            try {
                val token = authRepository.getToken() ?: run {
                    _locationsError.value = "Authorization required"
                    _locationsLoading.value = false
                    return@launch
                }
                val response = locationRepository.getLocations(token)
                if (response.isSuccessful) {
                    _locations.value = response.body() ?: emptyList()
                } else {
                    _locationsError.value = "Failed to load locations: ${response.message()}"
                }
            } catch (e: Exception) {
                _locationsError.value = "Error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _locationsLoading.value = false
            }
        }
    }

    fun searchFlights(from: String, to: String, date: String? = null) {
        viewModelScope.launch {
            _flightsLoading.value = true
            _flightsError.value = null
            try {
                val response = flightRepository.searchFlights(from, to, date)
                if (response.isSuccessful) {
                    _flights.value = response.body()?.map { flight ->
                        FlightModel(
                            flightId = flight.flightId,
                            airlineLogo = flight.airlineLogo,
                            airlineName = flight.airlineName,
                            arriveTime = flight.arriveTime,
                            classSeat = flight.classSeat,
                            flightDate = flight.flightDate,
                            departureCity = flight.departureCity,
                            departureShort = flight.departureShort,
                            totalSeats = flight.totalSeats,
                            price = flight.price,
                            reservedSeats = flight.reservedSeats,
                            departureTime = flight.departureTime,
                            arrivalCity = flight.arrivalCity,
                            arrivalShort = flight.arrivalShort
                        )
                    } ?: emptyList()
                } else {
                    _flightsError.value = "Failed to load flights: ${response.message()}"
                }
            } catch (e: Exception) {
                _flightsError.value = "Error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _flightsLoading.value = false
            }
        }
    }

    fun selectSeat(seat: Seat, flight: FlightModel) {
        val currentSelected = _selectedSeats.value.toMutableList()
        val isSelected = currentSelected.any { it.name == seat.name }

        if (isSelected) {
            currentSelected.removeAll { it.name == seat.name }
        } else {
            currentSelected.add(seat)
        }
        _selectedSeats.value = currentSelected
        _totalPrice.value = flight.price.multiply(BigDecimal(currentSelected.size))
    }

    fun clearSelection() {
        val updatedSeats = _seatList.value.map { seat ->
            if (seat.status == SeatStatus.SELECTED) seat.copy(status = SeatStatus.AVAILABLE)
            else seat
        }
        _seatList.value = updatedSeats

        _selectedSeats.value = emptyList()
        _totalPrice.value = BigDecimal.ZERO
    }

    fun createBooking(
        flight: FlightModel,
        selectedSeats: List<Seat>,
        onComplete: (Boolean, Long?) -> Unit
    ) {
        viewModelScope.launch {
            val token = authRepository.getToken() ?: run {
                onComplete(false, null)
                return@launch
            }

            val response = flightRepository.createBooking(
                "Bearer $token",
                BookingRequestDto(
                    flightId = flight.flightId!!,
                    seatNumber = selectedSeats.joinToString(",") { it.name }
                )
            )

            onComplete(response.isSuccessful, response.body()?.bookingId)
        }
    }

    fun fetchReservedSeats(flightId: Long) {
        viewModelScope.launch {
            try {
                val reserved = flightRepository.getReservedSeats(flightId)
                _reservedSeats.value = reserved
            } catch (e: Exception) {
                _reservedSeats.value = emptyList()
            }
        }
    }
}
