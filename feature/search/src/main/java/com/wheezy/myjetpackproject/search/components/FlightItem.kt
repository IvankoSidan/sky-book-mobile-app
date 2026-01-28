package com.wheezy.myjetpackproject.search.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.wheezy.myjetpackproject.core.model.FlightModel
import com.wheezy.myjetpackproject.core.ui.R

@Composable
fun FlightItem(
    item: FlightModel,
    onFlightClick: (FlightModel) -> Unit,
    imageLoader: ImageLoader
) {
    Log.d("FlightItem", "${item.fullLogoUrl}")
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(15.dp)
            )
            .clickable { onFlightClick(item) }
    ) {
        val (
            logo, timeTxt, airplaneIcon,
            fromCity, fromShort, toCity, toShort,
            dashLine, seatIcon, priceTxt, classTxt
        ) = createRefs()

        AsyncImage(
            model = item.fullLogoUrl,
            contentDescription = null,
            imageLoader = imageLoader,
            modifier = Modifier
                .size(200.dp, 50.dp)
                .padding(top = 8.dp)
                .constrainAs(logo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    horizontalBias = 0.5f
                },
            onError = { error ->
                Log.e("AsyncImage", "Error loading image: ${error.result.throwable}")
            }
        )

        Text(
            text = item.arriveTime,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(timeTxt) {
                    top.linkTo(logo.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Image(
            painter = painterResource(id = R.drawable.line_airple_blue),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 8.dp)
                .size(width = 210.dp, height = 37.dp)
                .constrainAs(airplaneIcon) {
                    top.linkTo(timeTxt.bottom)
                    centerHorizontallyTo(parent)
                }
        )

        Text(
            text = item.departureCity,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            softWrap = true,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(fromCity) {
                    top.linkTo(airplaneIcon.top)
                    start.linkTo(parent.start)
                    end.linkTo(airplaneIcon.start)
                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = item.departureShort,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.constrainAs(fromShort) {
                top.linkTo(fromCity.bottom)
                bottom.linkTo(airplaneIcon.bottom)
                start.linkTo(fromCity.start)
                end.linkTo(fromCity.end)
            }
        )

        Text(
            text = item.arrivalCity,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            softWrap = true,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(toCity) {
                    top.linkTo(airplaneIcon.top)
                    start.linkTo(airplaneIcon.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = item.arrivalShort,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.constrainAs(toShort) {
                top.linkTo(toCity.bottom)
                bottom.linkTo(airplaneIcon.bottom)
                start.linkTo(toCity.start)
                end.linkTo(toCity.end)
            }
        )

        Image(
            painter = painterResource(id = R.drawable.dash_line),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(45.dp)
                .constrainAs(dashLine) {
                    top.linkTo(airplaneIcon.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Image(
            painter = painterResource(id = R.drawable.seat_black_ic),
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .constrainAs(seatIcon) {
                    top.linkTo(dashLine.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        )

        Text(
            text = "%.2f $".format(item.price),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .padding(end = 16.dp)
                .constrainAs(priceTxt) {
                    top.linkTo(seatIcon.top)
                    bottom.linkTo(seatIcon.bottom)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = item.classSeat,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 8.dp)
                .constrainAs(classTxt) {
                    top.linkTo(seatIcon.top)
                    bottom.linkTo(seatIcon.bottom)
                    start.linkTo(seatIcon.end)
                }
        )
    }
}


