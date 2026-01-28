package com.wheezy.myjetpackproject.ui.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.wheezy.myjetpackproject.booking.viewmodel.BookingViewModel
import com.wheezy.myjetpackproject.booking.viewmodel.PaymentViewModel
import com.wheezy.myjetpackproject.common_vm.FlightViewModel
import com.wheezy.myjetpackproject.common_vm.ThemeViewModel
import com.wheezy.myjetpackproject.core.common_vm.TopBarViewModel
import com.wheezy.myjetpackproject.core.ui.R
import com.wheezy.myjetpackproject.core.ui.theme.MyAppTheme
import com.wheezy.myjetpackproject.core.ui.components.GradientButton
import com.wheezy.myjetpackproject.core.model.User
import com.wheezy.myjetpackproject.mylibrary.auth.AuthViewModel
import com.wheezy.myjetpackproject.search.SearchParamsViewModel
import com.wheezy.myjetpackproject.ui.components.AppNavGraph

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            StatusTopBarColor()
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val currentTheme by themeViewModel.currentTheme.collectAsState()

            MyAppTheme(themeOption = currentTheme) {
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
                    topBarViewModel = topBarViewModel,
                    currentTheme = currentTheme,
                    onThemeChanged = { theme ->
                        themeViewModel.setTheme(theme)
                    }
                )
            }
        }
    }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(top = 80.dp)
            ) {
                val styleText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                        append("Your\n")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                        append("Perfect Flight")
                    }
                }

                Text(
                    text = styleText,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 48.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.subtitle_splash),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                GradientButton(
                    onClick = onGetStartedClick,
                    text = "Get Started",
                    padding = 32
                )
            }
        }
    }
}

@Composable
fun StatusTopBarColor() {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = Color.Transparent.toArgb()

        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }
}
