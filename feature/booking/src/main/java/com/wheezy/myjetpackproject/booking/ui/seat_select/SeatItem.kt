package com.wheezy.myjetpackproject.booking.ui.seat_select

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wheezy.myjetpackproject.core.model.Seat
import com.wheezy.myjetpackproject.core.model.SeatStatus

@Composable
fun SeatItem(
    seat: Seat,
    onSeatClick: () -> Unit
) {
    val isClickable = seat.status == SeatStatus.AVAILABLE || seat.status == SeatStatus.SELECTED
    val colors = MaterialTheme.colorScheme

    val (backgroundColor, textColor) = when (seat.status) {
        SeatStatus.AVAILABLE -> colors.primary to colors.onPrimary
        SeatStatus.UNAVAILABLE -> colors.surfaceVariant to colors.onSurfaceVariant
        SeatStatus.SELECTED -> colors.tertiary to colors.onTertiary
        SeatStatus.EMPTY -> Color.Transparent to Color.Transparent
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .then(if (isClickable) Modifier.clickable { onSeatClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (seat.status != SeatStatus.EMPTY) {
            Text(
                text = seat.name,
                color = textColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

