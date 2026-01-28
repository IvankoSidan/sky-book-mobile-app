package com.wheezy.myjetpackproject.UI.Screens.SearchResult

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.wheezy.myjetpackproject.ViewModel.FlightViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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
import com.wheezy.myjetpackproject.UI.Components.CustomSnackbarHost
import com.wheezy.myjetpackproject.UI.Components.EmptyStateScreen
import com.wheezy.myjetpackproject.UI.Components.WorldBackground
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

    val flights by flightViewModel.flights.collectAsState()
    val isLoading by flightViewModel.flightsLoading.collectAsState()
    val error by flightViewModel.flightsError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    if (from.isBlank() || to.isBlank() || numPassenger <= 0) {
        EmptyStateScreen("Please enter search parameters on the main screen first.")
        return
    }

    LaunchedEffect(from, to) {
        flightViewModel.searchFlights(from, to)
    }

    BackHandler { navController.popBackStack() }

    Scaffold(
        snackbarHost = { CustomSnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // World image as background - positioned similarly to TopBar at top start
            WorldBackground(modifier = Modifier.align(Alignment.TopCenter))

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp) // Reduced top padding
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back button",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { navController.popBackStack() },
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Search Results",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    flights.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No flights found",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
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
        }
    }
}






