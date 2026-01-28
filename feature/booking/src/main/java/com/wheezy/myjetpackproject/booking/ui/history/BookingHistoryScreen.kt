package com.wheezy.myjetpackproject.feature.booking.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.wheezy.myjetpackproject.core.ui.R
import com.wheezy.myjetpackproject.core.model.BookingStatus
import com.wheezy.myjetpackproject.core.network.model.BookingDetailsDTO
import com.wheezy.myjetpackproject.core.network.model.toBookingEntity
import com.wheezy.myjetpackproject.core.network.model.toFlightModel
import com.wheezy.myjetpackproject.core.ui.components.WorldBackground
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import com.wheezy.myjetpackproject.booking.viewmodel.BookingViewModel
import com.wheezy.myjetpackproject.booking.viewmodel.PaymentViewModel
import com.wheezy.myjetpackproject.core.common_vm.TopBarViewModel
import com.wheezy.myjetpackproject.core.ui.components.CustomSnackBarHost
import kotlinx.coroutines.launch

@Composable
fun BookingHistoryScreen(
    navController: NavHostController,
    viewModel: BookingViewModel,
    paymentViewModel: PaymentViewModel,
    topBarViewModel: TopBarViewModel
) {
    val bookings: List<BookingDetailsDTO> by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val clientSecret by paymentViewModel.clientSecret.collectAsState()
    val customerId by paymentViewModel.customerId.collectAsState()
    val ephemeralKey by paymentViewModel.ephemeralKey.collectAsState()

    val paymentLoading by paymentViewModel.loading.collectAsState()
    val loadingBookingIds by viewModel.loadingBookingIds.collectAsState()

    var pendingBookingId by remember { mutableStateOf<Long?>(null) }
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    paymentViewModel.handleSuccess()
                    topBarViewModel.loadNotifications()
                    pendingBookingId?.let { viewModel.updateBookingStatus(it, BookingStatus.CONFIRMED) }
                    pendingBookingId = null
                    coroutineScope.launch { snackBarHostState.showSnackbar("Payment successful!") }
                }
                is PaymentSheetResult.Canceled -> {
                    paymentViewModel.handleCancel()
                    pendingBookingId = null
                }
                is PaymentSheetResult.Failed -> {
                    paymentViewModel.handleFailure(result.error.localizedMessage ?: "Payment failed")
                    coroutineScope.launch { snackBarHostState.showSnackbar("Error: ${result.error.localizedMessage}") }
                    pendingBookingId = null
                }
            }
        }
    )

    LaunchedEffect(clientSecret, customerId, ephemeralKey) {
        if (pendingBookingId != null && clientSecret != null && customerId != null && ephemeralKey != null) {
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
        }
    }

    LaunchedEffect(Unit) { viewModel.loadMyBookings() }

    Scaffold(
        snackbarHost = { CustomSnackBarHost(hostState = snackBarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            WorldBackground(modifier = Modifier.align(Alignment.TopCenter))

            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).clickable { navController.popBackStack() },
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Booking History", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
                }

                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }

                if (loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            items = bookings,
                            key = { it.bookingId }
                        ) { item: BookingDetailsDTO ->
                            val booking = item.toBookingEntity()
                            val flight = item.toFlightModel()

                            BookingHistoryItem(
                                flight = flight,
                                booking = booking,
                                onCancelClick = { b ->
                                    viewModel.cancelOrDeleteBooking(b.id, b.status)
                                    topBarViewModel.loadNotifications()
                                },
                                onPayClick = { b ->
                                    pendingBookingId = b.id
                                    paymentViewModel.initiatePaymentForBooking(b, flight)
                                },
                                onDeleteClick = { b ->
                                    viewModel.cancelOrDeleteBooking(b.id, b.status)
                                    topBarViewModel.loadNotifications()
                                },
                                isPaying = (pendingBookingId == booking.id) && paymentLoading,
                                isLoading = loadingBookingIds.contains(booking.id)
                            )
                        }
                    }
                }
            }
        }
    }
}