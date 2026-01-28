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
import androidx.compose.material3.MaterialTheme
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
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem("Available", colors.primary)
            LegendItem("Selected", colors.tertiary)
            LegendItem("Unavailable", colors.surfaceVariant)
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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = if (selectedSeats.isBlank()) "-" else selectedSeats,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.onSurface
                    )
                )
            }

            Text(
                text = "$${totalPrice.setScale(0, RoundingMode.HALF_UP)}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = colors.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        GradientButton(
            onClick = onConfirmClick,
            text = "Confirm seats"
        )
    }
}
