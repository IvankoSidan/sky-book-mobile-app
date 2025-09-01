package com.wheezy.myjetpackproject.UI.Screens.SeatSelect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wheezy.myjetpackproject.Data.Enums.SeatStatus
import com.wheezy.myjetpackproject.Data.Model.Seat
import com.wheezy.myjetpackproject.R

@Composable
fun SeatItem(
    seat: Seat,
    onSeatClick: () -> Unit
) {
    val isClickable = seat.status == SeatStatus.AVAILABLE || seat.status == SeatStatus.SELECTED

    val (backgroundColor, textColor) = when (seat.status) {
        SeatStatus.AVAILABLE   -> colorResource(R.color.green) to Color.White
        SeatStatus.UNAVAILABLE -> colorResource(R.color.grey) to Color.LightGray
        SeatStatus.SELECTED    -> colorResource(R.color.orange) to Color.Black
        SeatStatus.EMPTY       -> Color.Transparent to Color.Transparent
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(28.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(backgroundColor)
            .then(
                if (isClickable) Modifier.clickable { onSeatClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = seat.name,
            color = textColor,
            fontSize = 12.sp
        )
    }
}
