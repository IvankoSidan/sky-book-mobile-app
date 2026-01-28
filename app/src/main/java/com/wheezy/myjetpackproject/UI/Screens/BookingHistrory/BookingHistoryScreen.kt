package com.wheezy.myjetpackproject.UI.Screens.BookingHistrory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.wheezy.myjetpackproject.R
import com.wheezy.myjetpackproject.ViewModel.BookingViewModel
import com.wheezy.myjetpackproject.ViewModel.PaymentViewModel
import com.wheezy.myjetpackproject.ViewModel.TopBarViewModel
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import com.wheezy.myjetpackproject.Data.Dto.toBookingEntity
import com.wheezy.myjetpackproject.Data.Dto.toFlightModel
import com.wheezy.myjetpackproject.Data.Enums.BookingStatus
import com.wheezy.myjetpackproject.UI.Components.CustomSnackbarHost
import com.wheezy.myjetpackproject.UI.Components.WorldBackground
import kotlinx.coroutines.launch

@Composable
fun BookingHistoryScreen(
    navController: NavHostController,
    viewModel: BookingViewModel,
    paymentViewModel: PaymentViewModel,
    topBarViewModel: TopBarViewModel
) {
    val bookings by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val clientSecret by paymentViewModel.clientSecret.collectAsState()
    val customerId by paymentViewModel.customerId.collectAsState()
    val ephemeralKey by paymentViewModel.ephemeralKey.collectAsState()
    val paymentLoading by paymentViewModel.loading.collectAsState()
    val loadingBookingIds by viewModel.loadingBookingIds.collectAsState()

    var pendingBookingId by remember { mutableStateOf<Long?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    paymentViewModel.handleSuccess()
                    topBarViewModel.loadNotifications()
                    pendingBookingId?.let { bookingId ->
                        viewModel.updateBookingStatus(bookingId, BookingStatus.CONFIRMED)
                    }
                    pendingBookingId = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Payment successful!", duration = SnackbarDuration.Short)
                    }
                }
                is PaymentSheetResult.Canceled -> {
                    paymentViewModel.handleCancel()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Payment cancelled", duration = SnackbarDuration.Short)
                    }
                }
                is PaymentSheetResult.Failed -> {
                    paymentViewModel.handleFailure(result.error.localizedMessage ?: "Payment failed")
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            "Payment failed: ${result.error.localizedMessage}",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    )

    LaunchedEffect(clientSecret, customerId, ephemeralKey, pendingBookingId) {
        if (pendingBookingId != null &&
            !clientSecret.isNullOrEmpty() &&
            !customerId.isNullOrEmpty() &&
            !ephemeralKey.isNullOrEmpty()
        ) {
            try {
                paymentSheet.presentWithPaymentIntent(
                    paymentIntentClientSecret = clientSecret!!,
                    configuration = PaymentSheet.Configuration(
                        merchantDisplayName = "Flight Booking Co.",
                        customer = PaymentSheet.CustomerConfiguration(
                            id = customerId!!,
                            ephemeralKeySecret = ephemeralKey!!
                        )
                    )
                )
            } finally {
                pendingBookingId = null
            }
        }
    }

    LaunchedEffect(Unit) { viewModel.loadMyBookings() }

    Scaffold(
        snackbarHost = { CustomSnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            WorldBackground(modifier = Modifier.align(Alignment.TopCenter))


            // Main content with Scaffold padding
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back button",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { navController.popBackStack() },
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Booking History",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                when {
                    loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    bookings.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No bookings found",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp)
                        ) {
                            items(bookings) { bookingDto ->
                                val booking = bookingDto.toBookingEntity()
                                val flight = bookingDto.toFlightModel()
                                BookingHistoryItem(
                                    flight = flight,
                                    booking = booking,
                                    onCancelClick = { bookingToCancel ->
                                        viewModel.cancelOrDeleteBooking(bookingToCancel.id, bookingToCancel.status)
                                    },
                                    onPayClick = { bookingToPay ->
                                        pendingBookingId = bookingToPay.id
                                        paymentViewModel.initiatePaymentForBooking(bookingToPay, flight)
                                    },
                                    onDeleteClick = { bookingToDelete ->
                                        viewModel.cancelOrDeleteBooking(bookingToDelete.id, bookingToDelete.status)
                                    },
                                    isPaying = pendingBookingId == booking.id && paymentLoading,
                                    isLoading = loadingBookingIds.contains(booking.id)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



