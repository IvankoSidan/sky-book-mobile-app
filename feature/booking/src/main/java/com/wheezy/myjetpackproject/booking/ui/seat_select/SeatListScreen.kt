package com.wheezy.myjetpackproject.feature.booking.ui.seat_select

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.wheezy.myjetpackproject.booking.ui.seat_select.BottomSection
import com.wheezy.myjetpackproject.booking.ui.seat_select.SeatItem
import com.wheezy.myjetpackproject.booking.ui.seat_select.TopSection
import com.wheezy.myjetpackproject.booking.viewmodel.BookingViewModel
import com.wheezy.myjetpackproject.core.ui.R
import com.wheezy.myjetpackproject.common_vm.FlightViewModel
import com.wheezy.myjetpackproject.core.model.Seat
import com.wheezy.myjetpackproject.core.model.SeatStatus
import com.wheezy.myjetpackproject.core.ui.components.CustomSnackBarHost
import com.wheezy.myjetpackproject.core.ui.components.WorldBackground
import com.wheezy.myjetpackproject.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun SeatListScreen(
    navController: NavController,
    flightViewModel: FlightViewModel,
    bookingViewModel: BookingViewModel,
    onBackClick: () -> Unit
) {
    val flightModel by flightViewModel.selectedFlight.collectAsState()
    val seatList by flightViewModel.seatList.collectAsState()
    val selectedSeats by flightViewModel.selectedSeats.collectAsState()
    val totalPrice by flightViewModel.totalPrice.collectAsState()
    val reservedSeats by flightViewModel.reservedSeats.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val currentFlight = flightModel

    LaunchedEffect(currentFlight?.flightId) {
        currentFlight?.flightId?.let { flightViewModel.fetchReservedSeats(it) }
    }

    Scaffold(
        snackbarHost = { CustomSnackBarHost(hostState = snackBarHostState) }
    ) { padding ->
        if (currentFlight == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                WorldBackground(modifier = Modifier.align(Alignment.TopCenter))

                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
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
                            painter = painterResource(id = R.drawable.airple_seat),
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
                            items(items = seatList) { seatItem: Seat ->
                                val status = when {
                                    reservedSeats.contains(seatItem.name) -> SeatStatus.UNAVAILABLE
                                    selectedSeats.any { it.name == seatItem.name } -> SeatStatus.SELECTED
                                    seatItem.status == SeatStatus.EMPTY -> SeatStatus.EMPTY
                                    else -> SeatStatus.AVAILABLE
                                }
                                SeatItem(
                                    seat = seatItem.copy(status = status),
                                    onSeatClick = {
                                        if (status == SeatStatus.AVAILABLE || status == SeatStatus.SELECTED) {
                                            flightViewModel.selectSeat(seatItem, currentFlight)
                                        } else if (status == SeatStatus.UNAVAILABLE) {
                                            coroutineScope.launch {
                                                snackBarHostState.showSnackbar("Seat ${seatItem.name} is already booked")
                                            }
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
                                flightViewModel.createBooking(currentFlight, selectedSeats) { success, id ->
                                    if (success) {
                                        bookingViewModel.setFlight(currentFlight)
                                        bookingViewModel.setSelectedSeats(selectedSeats)
                                        bookingViewModel.setBookingId(id)
                                        navController.navigate(Screen.TicketDetail.route) {
                                            popUpTo("seatList") { saveState = true }
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("Booking failed")
                                        }
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar("Select seats")
                                }
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
        }
    }
}