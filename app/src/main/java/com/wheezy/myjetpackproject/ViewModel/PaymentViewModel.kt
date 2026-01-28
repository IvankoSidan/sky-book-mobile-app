package com.wheezy.myjetpackproject.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wheezy.myjetpackproject.Data.Model.Booking
import com.wheezy.myjetpackproject.Data.Model.FlightModel
import com.wheezy.myjetpackproject.Data.Model.Seat
import com.wheezy.myjetpackproject.Repository.PaymentRepository
import com.wheezy.myjetpackproject.Utils.NotificationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _clientSecret = MutableStateFlow<String?>(null)
    val clientSecret: StateFlow<String?> get() = _clientSecret

    private val _customerId = MutableStateFlow<String?>(null)
    val customerId: StateFlow<String?> get() = _customerId

    private val _ephemeralKey = MutableStateFlow<String?>(null)
    val ephemeralKey: StateFlow<String?> get() = _ephemeralKey

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    fun processPayment(
        flight: FlightModel,
        selectedSeats: List<Seat>,
        bookingId: Long
    ) {
        viewModelScope.launch {
            _loading.value = true
            val amount = calculateAmount(flight.price, selectedSeats.size)

            val response = paymentRepository.createPaymentSheet(
                bookingId = bookingId,
                amount = amount,
                currency = "USD"
            )

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    _clientSecret.value = body.paymentIntentClientSecret
                    _customerId.value = body.customerId
                    _ephemeralKey.value = body.ephemeralKey

                    NotificationEventBus.sendNotificationEvent(
                        message = "Payment prepared for booking $bookingId. Amount: \$${amount / 100} USD",
                        isRead = false
                    )
                }
            } else {
                NotificationEventBus.sendNotificationEvent(
                    message = "Failed to prepare payment sheet for booking $bookingId",
                    isRead = false
                )
            }
            _loading.value = false
        }
    }

    // Новый метод для BookingHistoryScreen
    fun initiatePaymentForBooking(booking: Booking, flight: FlightModel) {
        viewModelScope.launch {
            _loading.value = true
            val amount = calculateAmount(flight.price, booking.seatCount)

            val response = paymentRepository.createPaymentSheet(
                bookingId = booking.id,
                amount = amount,
                currency = "USD"
            )

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    _clientSecret.value = body.paymentIntentClientSecret
                    _customerId.value = body.customerId
                    _ephemeralKey.value = body.ephemeralKey
                }
            } else {
                NotificationEventBus.sendNotificationEvent(
                    message = "Failed to prepare payment sheet for booking ${booking.id}",
                    isRead = false
                )
            }

            _loading.value = false
        }
    }

    fun handleSuccess() {
        viewModelScope.launch {
            NotificationEventBus.sendNotificationEvent(
                message = "Payment completed successfully.",
                isRead = false
            )
        }
    }

    fun handleCancel() {
        viewModelScope.launch {
            NotificationEventBus.sendNotificationEvent(
                message = "Payment was canceled.",
                isRead = false
            )
        }
    }

    fun handleFailure(message: String) {
        viewModelScope.launch {
            NotificationEventBus.sendNotificationEvent(
                message = "Payment failed: $message",
                isRead = false
            )
        }
    }

    private fun calculateAmount(price: BigDecimal, seatCount: Int): Long =
        price.multiply(BigDecimal(seatCount)).multiply(BigDecimal(100)).longValueExact()
}
