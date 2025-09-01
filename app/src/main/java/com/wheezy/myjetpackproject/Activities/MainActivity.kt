package com.wheezy.myjetpackproject.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wheezy.myjetpackproject.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wheezy.myjetpackproject.Data.Model.User
import com.wheezy.myjetpackproject.UI.Components.AppNavGraph
import com.wheezy.myjetpackproject.UI.Components.DropDownMenu
import com.wheezy.myjetpackproject.UI.Components.GradientButton
import com.wheezy.myjetpackproject.UI.Components.PassengerCounter
import com.wheezy.myjetpackproject.UI.Components.TopBar
import com.wheezy.myjetpackproject.UI.Screens.DatePicker.DatePickerScreen
import com.wheezy.myjetpackproject.ViewModel.AuthViewModel
import com.wheezy.myjetpackproject.ViewModel.BookingViewModel
import com.wheezy.myjetpackproject.ViewModel.FlightViewModel
import com.wheezy.myjetpackproject.ViewModel.PaymentViewModel
import com.wheezy.myjetpackproject.ViewModel.SearchParamsViewModel
import com.wheezy.myjetpackproject.ViewModel.TopBarViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.muddz.styleabletoast.StyleableToast

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            StatusTopBarColor()
            val navController = rememberNavController()

            val authViewModel: AuthViewModel = hiltViewModel()
            val flightViewModel: FlightViewModel = hiltViewModel()
            val searchParamsViewModel: SearchParamsViewModel = hiltViewModel()
            val bookingViewModel: BookingViewModel = hiltViewModel()
            val paymentViewModel: PaymentViewModel = hiltViewModel()
            val topBarViewModel: TopBarViewModel = hiltViewModel()

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                flightViewModel = flightViewModel,
                searchParamsViewModel = searchParamsViewModel,
                bookingViewModel = bookingViewModel,
                paymentViewModel = paymentViewModel,
                topBarViewModel = topBarViewModel
            )
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    flightViewModel: FlightViewModel,
    searchParamsViewModel: SearchParamsViewModel,
    topBarViewModel: TopBarViewModel
) {
    val userState by authViewModel.user.collectAsState()
    val locations by flightViewModel.locations.collectAsState(initial = emptyList())
    val classItems by flightViewModel.classSeats.collectAsState()
    val isLoadingLocations = locations.isEmpty()

    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var classes by remember { mutableStateOf("") }
    var adultPassenger by remember { mutableStateOf("0") }
    var childPassenger by remember { mutableStateOf("0") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        flightViewModel.fetchLocations()
        flightViewModel.fetchClassSeats()
    }

    StatusTopBarColor()

    Scaffold(
        topBar = {
            TopBar(
                user = userState,
                viewModel = topBarViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.darkPurple2))
                .padding(paddingValues)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .background(
                            colorResource(id = R.color.darkPurple),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    val locationNames = locations.map { it.name }

                    YellowTitle(text = "From")
                    DropDownMenu(
                        items = locationNames,
                        loadingIcon = painterResource(id = R.drawable.from_ic),
                        hint = "Select departure location",
                        showLocationLoading = isLoadingLocations,
                        onItemSelected = { from = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    YellowTitle(text = "To")
                    DropDownMenu(
                        items = locationNames,
                        loadingIcon = painterResource(id = R.drawable.from_ic),
                        hint = "Select destination",
                        showLocationLoading = isLoadingLocations,
                        onItemSelected = { to = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    YellowTitle(text = "Passengers")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PassengerCounter(
                            title = "Adult",
                            modifier = Modifier.weight(1f),
                            onItemSelected = { adultPassenger = it }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        PassengerCounter(
                            title = "Child",
                            modifier = Modifier.weight(1f),
                            onItemSelected = { childPassenger = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        YellowTitle(text = "Departure Date", Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(16.dp))
                        YellowTitle(text = "Return Date", Modifier.weight(1f))
                    }
                    DatePickerScreen(Modifier.weight(1f))

                    Spacer(modifier = Modifier.height(16.dp))
                    YellowTitle(text = "Class")
                    DropDownMenu(
                        items = classItems,
                        loadingIcon = painterResource(id = R.drawable.seat_black_ic),
                        hint = "Select class",
                        showLocationLoading = isLoadingLocations,
                        onItemSelected = { classes = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        onClick = {
                            val numPassenger = (adultPassenger.toIntOrNull() ?: 0) +
                                    (childPassenger.toIntOrNull() ?: 0)

                            if (from.isNotBlank() && to.isNotBlank() && numPassenger > 0) {
                                searchParamsViewModel.setParams(from, to, numPassenger)
                                navController.navigate("searchResult")
                            } else {
                                StyleableToast.makeText(
                                    context,
                                    "Please select valid locations and at least one passenger",
                                    R.style.errorToast
                                ).show()
                            }
                        },
                        text = "Search"
                    )
                }
            }
        }
    }
}

@Composable
fun YellowTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        color = colorResource(id = R.color.orange),
        modifier = modifier
    )
}

@Composable
fun SplashScreen(
    userState: User?,
    onGetStartedClick: () -> Unit,
    onAutoNavigate: () -> Unit
) {
    var autoNavigationTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(userState) {
        if (userState != null && !autoNavigationTriggered) {
            autoNavigationTriggered = true
            onAutoNavigate()
        }
    }

    StatusTopBarColor()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val (backgroundImage, title, subtitle, startBtn) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(backgroundImage) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        val styleText = buildAnnotatedString {
            append("Your\n")
            withStyle(style = SpanStyle(color = colorResource(id = R.color.orange))) {
                append("Perfect Flight")
            }
        }

        Text(
            text = styleText,
            fontSize = 53.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 64.dp)
                    start.linkTo(parent.start)
                }
        )

        Text(
            text = stringResource(id = R.string.subtitle_splash),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.orange),
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
                .constrainAs(subtitle) {
                    top.linkTo(title.bottom)
                    start.linkTo(title.start)
                }
        )

        Box(
            modifier = Modifier
                .padding(bottom = 48.dp)
                .constrainAs(startBtn) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            GradientButton(onClick = onGetStartedClick, text = "Get Started", padding = 32)
        }
    }
}

@Composable
fun StatusTopBarColor() {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = false
        )
    }
}


