package com.wheezy.myjetpackproject.UI.Screens.SearchResult

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.wheezy.myjetpackproject.ViewModel.FlightViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import com.wheezy.myjetpackproject.R
import com.wheezy.myjetpackproject.UI.Components.EmptyStateScreen
import com.wheezy.myjetpackproject.ViewModel.BookingViewModel
import com.wheezy.myjetpackproject.ViewModel.SearchParamsViewModel

@Composable
fun ItemListScreen(
    navController: NavHostController,
    flightViewModel: FlightViewModel,
    searchParamsViewModel: SearchParamsViewModel,
    imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current).build()
) {
    val from by searchParamsViewModel.from.collectAsState()
    val to by searchParamsViewModel.to.collectAsState()
    val numPassenger by searchParamsViewModel.numPassenger.collectAsState()

    if (from.isBlank() || to.isBlank() || numPassenger <= 0) {
        EmptyStateScreen("Please enter search parameters on the main screen first.")
        return
    }

    val flights by flightViewModel.flights.collectAsState()
    val isLoading by flightViewModel.flightsLoading.collectAsState()
    val error by flightViewModel.flightsError.collectAsState()

    LaunchedEffect(from, to) {
        flightViewModel.searchFlights(from, to)
    }

    BackHandler { navController.popBackStack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.darkPurple2))
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 16.dp, end = 16.dp)
        ) {
            val (backBtn, title, worldImg) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back button",
                modifier = Modifier
                    .size(50.dp)
                    .clickable { navController.popBackStack() }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top, margin = 8.dp)
                        start.linkTo(parent.start)
                    }
            )

            Text(
                text = "Search Results",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(title) {
                        start.linkTo(backBtn.end, margin = 8.dp)
                        top.linkTo(backBtn.top)
                        bottom.linkTo(backBtn.bottom)
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.world),
                contentDescription = "World icon",
                modifier = Modifier.constrainAs(worldImg) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            )
        }

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            error != null -> Text(
                text = error ?: "Unknown error",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp)
            ) {
                itemsIndexed(flights) { _, flight ->
                    FlightItem(
                        item = flight,
                        onFlightClick = {
                            flightViewModel.selectFlight(flight)
                            navController.navigate("selectSeat") {
                                popUpTo("searchResult") { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        imageLoader = imageLoader
                    )
                }
            }
        }
    }
}




