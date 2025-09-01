package com.wheezy.myjetpackproject.UI.Screens.TicketDetail

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.ImageLoader
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import com.wheezy.myjetpackproject.R
import com.wheezy.myjetpackproject.UI.Components.EmptyStateScreen
import com.wheezy.myjetpackproject.UI.Components.GradientButton
import com.wheezy.myjetpackproject.UI.Screens.SeatSelect.TicketDetailHeader
import com.wheezy.myjetpackproject.ViewModel.BookingViewModel
import com.wheezy.myjetpackproject.ViewModel.PaymentViewModel
import com.wheezy.myjetpackproject.ViewModel.TopBarViewModel
import io.github.muddz.styleabletoast.StyleableToast

@Composable
fun TicketDetailScreen(
    navController: NavController,
    bookingViewModel: BookingViewModel,
    paymentViewModel: PaymentViewModel,
    topBarViewModel: TopBarViewModel,
    onBackClick: () -> Unit,
    imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current).build()
) {
    val flightState by bookingViewModel.selectedFlight.collectAsState()
    val selectedSeats by bookingViewModel.selectedSeats.collectAsState()
    val bookingId by bookingViewModel.bookingId.collectAsState()
    val clientSecret by paymentViewModel.clientSecret.collectAsState()
    val customerId by paymentViewModel.customerId.collectAsState()
    val ephemeralKey by paymentViewModel.ephemeralKey.collectAsState()
    val context = LocalContext.current

    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                PaymentSheetResult.Completed -> {
                    paymentViewModel.handleSuccess()
                    topBarViewModel.loadNotifications()
                    StyleableToast.Builder(context)
                        .text("Payment successful!")
                        .textColor(Color.WHITE)
                        .backgroundColor(ContextCompat.getColor(context, R.color.green))
                        .iconStart(R.drawable.success)
                        .show()
                    navController.navigate("main") {
                        popUpTo("ticketDetail") { inclusive = true }
                    }
                    bookingViewModel.clearBooking()
                }

                PaymentSheetResult.Canceled -> {
                    paymentViewModel.handleCancel()
                    StyleableToast.Builder(context)
                        .text("Payment cancelled")
                        .textColor(Color.WHITE)
                        .backgroundColor(ContextCompat.getColor(context, R.color.orange))
                        .iconStart(R.drawable.error)
                        .show()
                }

                is PaymentSheetResult.Failed -> {
                    paymentViewModel.handleFailure(
                        result.error.localizedMessage ?: "Payment failed"
                    )
                    StyleableToast.Builder(context)
                        .text("Payment failed: ${result.error.localizedMessage}")
                        .textColor(Color.WHITE)
                        .backgroundColor(ContextCompat.getColor(context, R.color.orange))
                        .iconStart(R.drawable.error)
                        .show()
                }
            }
        }
    )

    LaunchedEffect(key1 = bookingId, key2 = selectedSeats) {
        if (bookingId != null && selectedSeats.isNotEmpty()) {
            flightState?.let { flight ->
                paymentViewModel.processPayment(
                    flight = flight,
                    selectedSeats = selectedSeats,
                    bookingId = bookingId!!
                )
            }
        }
    }

    flightState?.let { flight ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.darkPurple2))
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .background(colorResource(id = R.color.darkPurple2))
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.darkPurple2))
                ) {
                    val (topSection, ticketDetail) = createRefs()

                    TicketDetailHeader(
                        onBackClick = onBackClick,
                        modifier = Modifier.constrainAs(topSection) {
                            top.linkTo(parent.top)
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
                        if (!clientSecret.isNullOrEmpty() && !customerId.isNullOrEmpty() && !ephemeralKey.isNullOrEmpty()) {
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
                            StyleableToast.Builder(context)
                                .text("Waiting for payment setup")
                                .textColor(Color.WHITE)
                                .backgroundColor(ContextCompat.getColor(context, R.color.orange))
                                .iconStart(R.drawable.error)
                                .show()
                        }
                    },
                    text = "Pay for ticket",
                    enabled = !paymentViewModel.loading.collectAsState().value
                )
            }
        }
    } ?: EmptyStateScreen("Ticket data is missing. Please select a flight.")
}


