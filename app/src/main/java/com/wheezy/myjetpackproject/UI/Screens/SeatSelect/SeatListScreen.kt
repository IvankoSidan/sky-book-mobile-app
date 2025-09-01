package com.wheezy.myjetpackproject.UI.Screens.SeatSelect

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wheezy.myjetpackproject.Data.Enums.SeatStatus
import com.wheezy.myjetpackproject.R
import com.wheezy.myjetpackproject.ViewModel.BookingViewModel
import com.wheezy.myjetpackproject.ViewModel.FlightViewModel
import io.github.muddz.styleabletoast.StyleableToast


@Composable
fun SeatListScreen(
    navController: NavController,
    flightViewModel: FlightViewModel,
    bookingViewModel: BookingViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val flightModel by flightViewModel.selectedFlight.collectAsState()
    val seatList by flightViewModel.seatList.collectAsState()
    val selectedSeats by flightViewModel.selectedSeats.collectAsState()
    val totalPrice by flightViewModel.totalPrice.collectAsState()
    val reservedSeats by flightViewModel.reservedSeats.collectAsState()

    LaunchedEffect(flightModel?.flightId) {
        flightModel?.flightId?.let {
            flightViewModel.fetchReservedSeats(it)
        }
    }

    if (flightModel == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.darkPurple2)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.darkPurple2))
    ) {
        val (topSection, middleSection, bottomSection) = createRefs()

        TopSection(
            modifier = Modifier.constrainAs(topSection) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            onBackClick = onBackClick
        )

        ConstraintLayout(
            modifier = Modifier
                .padding(top = 100.dp)
                .constrainAs(middleSection) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            val (airplane, seatGrid) = createRefs()

            Image(
                painter = painterResource(R.drawable.airple_seat),
                contentDescription = null,
                modifier = Modifier.constrainAs(airplane) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .padding(top = 240.dp, start = 64.dp, end = 64.dp)
                    .constrainAs(seatGrid) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                items(seatList) { seat ->
                    val status = when {
                        reservedSeats.contains(seat.name) -> SeatStatus.UNAVAILABLE
                        selectedSeats.any { it.name == seat.name } -> SeatStatus.SELECTED
                        seat.status == SeatStatus.EMPTY -> SeatStatus.EMPTY
                        else -> SeatStatus.AVAILABLE
                    }

                    SeatItem(
                        seat = seat.copy(status = status),
                        onSeatClick = {
                            when (status) {
                                SeatStatus.AVAILABLE, SeatStatus.SELECTED -> {
                                    flightViewModel.selectSeat(seat, flightModel!!)
                                }
                                SeatStatus.UNAVAILABLE -> Toast.makeText(
                                    context,
                                    "Seat ${seat.name} is already booked",
                                    Toast.LENGTH_SHORT
                                ).show()
                                else -> {}
                            }
                        }
                    )
                }
            }
        }

        BottomSection(
            seatCount = selectedSeats.size,
            selectedSeats = selectedSeats.joinToString { it.name },
            totalPrice = totalPrice,
            onConfirmClick = {
                if (selectedSeats.isNotEmpty()) {
                    flightViewModel.createBooking(flightModel!!, selectedSeats) { success, bookingId ->
                        if (success) {
                            bookingViewModel.setFlight(flightModel!!)
                            bookingViewModel.setSelectedSeats(selectedSeats)
                            bookingViewModel.setBookingId(bookingId)
                            navController.navigate("ticketDetail") {
                                popUpTo("seatList") { saveState = true }
                            }
                        } else {
                            StyleableToast.makeText(
                                context,
                                "Booking error",
                                R.style.errorToast
                            ).show()
                        }
                    }
                } else {
                    StyleableToast.makeText(
                        context,
                        "Select seats",
                        R.style.errorToast
                    ).show()
                }
            },
            modifier = Modifier.constrainAs(bottomSection) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}






