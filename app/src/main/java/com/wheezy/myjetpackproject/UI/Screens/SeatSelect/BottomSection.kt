package com.wheezy.myjetpackproject.UI.Screens.SeatSelect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wheezy.myjetpackproject.R
import com.wheezy.myjetpackproject.UI.Components.GradientButton
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun BottomSection(
    seatCount: Int,
    selectedSeats: String,
    totalPrice: BigDecimal,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.darkPurple2))
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem("Available", colorResource(id = R.color.green))
            LegendItem("Selected", colorResource(id = R.color.orange))
            LegendItem("Unavailable", colorResource(id = R.color.grey))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$seatCount seats selected",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = if (selectedSeats.isBlank()) "-" else selectedSeats,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "$${totalPrice.setScale(0, RoundingMode.HALF_UP)}",
                color = colorResource(id = R.color.orange),
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        GradientButton(
            onClick = onConfirmClick,
            text = "Confirm seats"
        )
    }
}