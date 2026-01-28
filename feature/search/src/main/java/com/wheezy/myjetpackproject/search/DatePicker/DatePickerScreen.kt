package com.wheezy.myjetpackproject.search.DatePicker

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.wheezy.myjetpackproject.core.ui.R

@Composable
fun DatePickerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val departureCalendar = remember { Calendar.getInstance() }
    val returnCalendar = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_WEEK, 1) } }

    var departureDate by remember { mutableStateOf(dateFormat.format(departureCalendar.time)) }
    var returnDate by remember { mutableStateOf(dateFormat.format(returnCalendar.time)) }

    Row {
        DatePickerItem(
            modifier = modifier,
            dateText = departureDate,
            onDateSelected = { selectedDate ->
                departureCalendar.time = dateFormat.parse(selectedDate) ?: departureCalendar.time
                departureDate = selectedDate
            },
            dateFormat = dateFormat,
            calendar = departureCalendar,
            context = context
        )

        Spacer(modifier = Modifier.width(16.dp))

        DatePickerItem(
            modifier = modifier,
            dateText = returnDate,
            onDateSelected = { selectedDate ->
                returnCalendar.time = dateFormat.parse(selectedDate) ?: returnCalendar.time
                returnDate = selectedDate
            },
            dateFormat = dateFormat,
            calendar = returnCalendar,
            context = context
        )
    }
}

@Composable
fun DatePickerItem(
    modifier: Modifier = Modifier,
    dateText: String,
    onDateSelected: (String) -> Unit,
    dateFormat: SimpleDateFormat,
    calendar: Calendar,
    context: Context
) {
    Row(
        modifier = modifier
            .height(60.dp)
            .padding(top = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                showDatePickerDialog(context, calendar, dateFormat, onDateSelected)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.calendar_ic),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp)
        )
        Text(
            text = dateText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

fun showDatePickerDialog(
    context: Context,
    calendar: Calendar,
    dateFormat: SimpleDateFormat,
    onDateSelected: (String) -> Unit
) {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    android.app.DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        calendar.set(selectedYear, selectedMonth, selectedDay)
        val formattedDate = dateFormat.format(calendar.time)
        onDateSelected(formattedDate)
    }, year, month, day).show()
}
