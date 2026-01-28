// FILE: feature/booking/src/main/java/com/wheezy/myjetpackproject/booking/ui/history/BookingHistoryItem.kt
package com.wheezy.myjetpackproject.feature.booking.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.wheezy.myjetpackproject.core.model.*
import com.wheezy.myjetpackproject.core.ui.R
import com.wheezy.myjetpackproject.core.common.utils.statusColor
import com.wheezy.myjetpackproject.core.ui.components.GradientButton
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Composable
fun BookingHistoryItem(
    flight: FlightModel,
    booking: Booking,
    onCancelClick: (Booking) -> Unit,
    onPayClick: (Booking) -> Unit,
    onDeleteClick: (Booking) -> Unit,
    isPaying: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current).build(),
) {
    var showPayDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showPayDialog && booking.status.canBePaid()) {
        AlertDialog(
            onDismissRequest = { showPayDialog = false },
            title = { Text("Booking Options") },
            text = { Text("Would you like to pay for or delete this booking?") },
            confirmButton = {
                TextButton(onClick = {
                    showPayDialog = false
                    onPayClick(booking)
                }) { Text("Pay") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPayDialog = false
                    onDeleteClick(booking)
                }) { Text("Delete") }
            }
        )
    }

    if (showCancelDialog && booking.status.canBeCancelled()) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Booking") },
            text = { Text("Are you sure you want to cancel this booking?") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    onCancelClick(booking)
                }) { Text("Yes, Cancel") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("No") }
            }
        )
    }

    if (showDeleteDialog && booking.status.canBeDeleted()) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Booking") },
            text = { Text("Do you really want to delete this booking permanently?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteClick(booking)
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    val totalPrice = remember(flight.price, booking.seatCount) {
        flight.price.multiply(BigDecimal(booking.seatCount))
    }

    val bookingDateText = remember(booking.bookingDate) {
        try {
            booking.bookingDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
        } catch (e: Exception) {
            booking.bookingDate.toString()
        }
    }

    val accent = booking.status.statusColor()
    val containerShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(containerShape)
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = containerShape)
            .clickable(enabled = booking.status.canBePaid()) {
                if (booking.status.canBePaid()) showPayDialog = true
            }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(accent)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    val (logo, arrivalText, lineImg, fromCity, fromShort, toCity, toShort) = createRefs()

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(flight.fullLogoUrl)
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(width = 200.dp, height = 50.dp)
                            .constrainAs(logo) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )

                    Text(
                        text = flight.arriveTime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.constrainAs(arrivalText) {
                            top.linkTo(logo.bottom, margin = 8.dp)
                            centerHorizontallyTo(parent)
                        }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.line_airple_blue),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 210.dp, height = 37.dp)
                            .constrainAs(lineImg) {
                                top.linkTo(arrivalText.bottom, margin = 8.dp)
                                centerHorizontallyTo(parent)
                            }
                    )

                    Text(
                        text = flight.departureCity,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.constrainAs(fromCity) {
                            top.linkTo(lineImg.top)
                            end.linkTo(lineImg.start)
                            start.linkTo(parent.start)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        }
                    )

                    Text(
                        text = flight.departureShort,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.constrainAs(fromShort) {
                            top.linkTo(fromCity.bottom)
                            bottom.linkTo(lineImg.bottom)
                            centerHorizontallyTo(fromCity)
                        }
                    )

                    Text(
                        text = flight.arrivalCity,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.constrainAs(toCity) {
                            top.linkTo(lineImg.top)
                            start.linkTo(lineImg.end)
                            end.linkTo(parent.end)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        }
                    )

                    Text(
                        text = flight.arrivalShort,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.constrainAs(toShort) {
                            top.linkTo(toCity.bottom)
                            bottom.linkTo(lineImg.bottom)
                            centerHorizontallyTo(toCity)
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "From", color = Color.Black)
                        Text(
                            text = flight.departureCity,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Booking date", color = Color.Black)
                        Text(
                            text = bookingDateText,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "To", color = Color.Black)
                        Text(
                            text = flight.arrivalCity,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Flight time", color = Color.Black)
                        Text(
                            text = flight.departureTime,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.dash_line),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Class", color = Color.Black)
                        Text(
                            text = flight.classSeat,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Seats", color = Color.Black)
                        Text(
                            text = booking.seatNumbers.ifEmpty { "${booking.seatCount} seats" },
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Airline", color = Color.Black)
                        Text(
                            text = flight.airlineName,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Price", color = Color.Black)
                        Text(
                            text = String.format("$%.2f", totalPrice.toDouble()),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.qrcode),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 8.dp)
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.dash_line),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Status", color = Color.Black)
                        Text(
                            text = booking.status.name,
                            fontWeight = FontWeight.Bold,
                            color = accent,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isLoading -> {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }

                            isPaying -> {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }

                            booking.status.canBePaid() -> {
                                GradientButton(
                                    onClick = { showPayDialog = true },
                                    text = "Pay",
                                    enabled = true,
                                    colors = listOf(Color.Green, Color(0xFF00796B))
                                )
                            }

                            booking.status.canBeCancelled() -> {
                                GradientButton(
                                    onClick = { showCancelDialog = true },
                                    text = "Cancel",
                                    enabled = true,
                                    colors = listOf(MaterialTheme.colorScheme.tertiary, Color(0xFFF57C00))
                                )
                            }

                            booking.status.canBeDeleted() -> {
                                GradientButton(
                                    onClick = { showDeleteDialog = true },
                                    text = "Delete",
                                    enabled = true,
                                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.barcode),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}