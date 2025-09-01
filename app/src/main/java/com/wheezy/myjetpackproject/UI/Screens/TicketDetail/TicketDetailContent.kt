package com.wheezy.myjetpackproject.UI.Screens.TicketDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.ImageLoader
import coil.compose.AsyncImage
import com.wheezy.myjetpackproject.Data.Model.FlightModel
import com.wheezy.myjetpackproject.Data.Model.Seat
import com.wheezy.myjetpackproject.R
import java.math.BigDecimal

@Composable
fun TicketDetailContent(
    flightModel: FlightModel,
    selectedSeats: List<Seat>,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current).build()
) {
    val totalPrice = remember(selectedSeats.size) {
        flightModel.price.multiply(BigDecimal(selectedSeats.size))
    }


    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.lightPurple),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            val (logo, arrivalText, lineImg, fromCity, fromShort, toCity, toShort) = createRefs()

            AsyncImage(
                model = flightModel.fullLogoUrl,
                imageLoader = imageLoader,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(200.dp, 50.dp)
                    .constrainAs(logo) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = flightModel.arriveTime,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.darkPurple2),
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
                text = flightModel.departureCity,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.constrainAs(fromCity) {
                    top.linkTo(lineImg.top)
                    end.linkTo(lineImg.start)
                    start.linkTo(parent.start)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = flightModel.departureShort,
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
                text = flightModel.arrivalCity,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.constrainAs(toCity) {
                    top.linkTo(lineImg.top)
                    start.linkTo(lineImg.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = flightModel.arrivalShort,
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

        // 🔷 Основные параметры
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "From", color = Color.Black)
                Text(
                    text = flightModel.departureCity,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Date", color = Color.Black)
                Text(
                    text = flightModel.flightDate,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "To", color = Color.Black)
                Text(
                    text = flightModel.arrivalCity,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Time", color = Color.Black)
                Text(
                    text = flightModel.departureTime,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // 🔷 Разделительная линия
        Image(
            painter = painterResource(id = R.drawable.dash_line),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        // 🔷 Подробности билета
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Class", color = Color.Black)
                Text(
                    text = flightModel.classSeat,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Seats", color = Color.Black)
                Text(
                    text = selectedSeats.joinToString { it.name },
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Airline", color = Color.Black)
                Text(
                    text = flightModel.airlineName,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Price", color = Color.Black)
                Text(
                    text = String.format("$%.2f", totalPrice),
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

        // 🔷 Баркод снизу
        Image(
            painter = painterResource(id = R.drawable.barcode),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}



