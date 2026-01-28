package com.wheezy.myjetpackproject.UI.Screens.TicketDetail

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.ImageLoader
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import com.wheezy.myjetpackproject.Data.Enums.SeatStatus
import com.wheezy.myjetpackproject.Data.Model.FlightModel
import com.wheezy.myjetpackproject.Data.Model.Seat
import com.wheezy.myjetpackproject.R
import com.wheezy.myjetpackproject.UI.Components.EmptyStateScreen
import com.wheezy.myjetpackproject.UI.Components.GradientButton
import com.wheezy.myjetpackproject.UI.Components.WorldBackground
import com.wheezy.myjetpackproject.UI.Screens.SeatSelect.TicketDetailHeader
import com.wheezy.myjetpackproject.ViewModel.BookingViewModel
import com.wheezy.myjetpackproject.ViewModel.PaymentViewModel
import com.wheezy.myjetpackproject.ViewModel.TopBarViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun TicketDetailScreen(
    navController: NavController,
    bookingViewModel: BookingViewModel,
    paymentViewModel: PaymentViewModel,
    topBarViewModel: TopBarViewModel,
    onBackClick: () -> Unit
) {
    val flightState by bookingViewModel.selectedFlight.collectAsState()
    val selectedSeats by bookingViewModel.selectedSeats.collectAsState()
    val bookingId by bookingViewModel.bookingId.collectAsState()
    val clientSecret by paymentViewModel.clientSecret.collectAsState()
    val customerId by paymentViewModel.customerId.collectAsState()
    val ephemeralKey by paymentViewModel.ephemeralKey.collectAsState()
    val loading by paymentViewModel.loading.collectAsState()

    val context = LocalContext.current
    val imageLoader = remember { ImageLoader.Builder(context).build() }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    paymentViewModel.handleSuccess()
                    topBarViewModel.loadNotifications()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            "Payment successful!",
                            duration = SnackbarDuration.Short
                        )
                    }
                    navController.navigate("main") {
                        popUpTo("ticketDetail") { inclusive = true }
                    }
                    bookingViewModel.clearBooking()
                }

                is PaymentSheetResult.Canceled -> {
                    paymentViewModel.handleCancel()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            "Payment cancelled",
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is PaymentSheetResult.Failed -> {
                    paymentViewModel.handleFailure(
                        result.error.localizedMessage ?: "Payment failed"
                    )
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

    LaunchedEffect(bookingId, selectedSeats) {
        if (bookingId != null && selectedSeats.isNotEmpty()) {
            flightState?.let { flight ->
                paymentViewModel.processPayment(flight, selectedSeats, bookingId!!)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        flightState?.let { flight ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                WorldBackground(modifier = Modifier.align(Alignment.TopCenter))

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .zIndex(1f)
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (topSection, ticketDetail) = createRefs()

                        TicketDetailHeader(
                            onBackClick = onBackClick,
                            modifier = Modifier.constrainAs(topSection) {
                                top.linkTo(parent.top, margin = 16.dp) // Added margin
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                        )

                        TicketDetailContent(
                            flightModel = flight,
                            selectedSeats = selectedSeats,
                            modifier = Modifier.constrainAs(ticketDetail) {
                                top.linkTo(parent.top, margin = 110.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                            imageLoader = imageLoader
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    GradientButton(
                        onClick = {
                            if (!clientSecret.isNullOrEmpty() &&
                                !customerId.isNullOrEmpty() &&
                                !ephemeralKey.isNullOrEmpty()
                            ) {
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
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Waiting for payment setup",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        text = "Pay for ticket",
                        enabled = !loading
                    )
                }
            }
        } ?: EmptyStateScreen("Ticket data is missing. Please select a flight.")
    }
}



