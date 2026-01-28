package com.wheezy.myjetpackproject.UI.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.wheezy.myjetpackproject.Activities.MainScreen
import com.wheezy.myjetpackproject.Activities.SplashScreen
import com.wheezy.myjetpackproject.Data.Enums.ThemeOption
import com.wheezy.myjetpackproject.UI.Screens.Auth.LoginPageScreen
import com.wheezy.myjetpackproject.UI.Screens.Auth.RegisterPageScreen
import com.wheezy.myjetpackproject.UI.Screens.BookingHistrory.BookingHistoryScreen
import com.wheezy.myjetpackproject.UI.Screens.SearchResult.ItemListScreen
import com.wheezy.myjetpackproject.UI.Screens.SeatSelect.SeatListScreen
import com.wheezy.myjetpackproject.UI.Screens.TicketDetail.TicketDetailScreen
import com.wheezy.myjetpackproject.ViewModel.AuthViewModel
import com.wheezy.myjetpackproject.ViewModel.BookingViewModel
import com.wheezy.myjetpackproject.ViewModel.FlightViewModel
import com.wheezy.myjetpackproject.ViewModel.PaymentViewModel
import com.wheezy.myjetpackproject.ViewModel.SearchParamsViewModel
import com.wheezy.myjetpackproject.ViewModel.TopBarViewModel
import kotlinx.coroutines.delay

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    flightViewModel: FlightViewModel,
    searchParamsViewModel: SearchParamsViewModel,
    bookingViewModel: BookingViewModel,
    paymentViewModel: PaymentViewModel,
    topBarViewModel: TopBarViewModel,
    currentTheme: ThemeOption,
    onThemeChanged: (ThemeOption) -> Unit
) {
    val userState = authViewModel.user.collectAsState().value
    val startDestination = if (userState == null) "splash" else "main"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("splash") {
            SplashScreen(
                userState = userState,
                onGetStartedClick = {
                    navController.navigate(if (userState != null) "main" else "login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onAutoNavigate = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterPageScreen(
                onNavigateBack = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginPageScreen(
                onNavigateToRegister = {
                    navController.navigate("register") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                navController = navController,
                authViewModel = authViewModel,
                flightViewModel = flightViewModel,
                searchParamsViewModel = searchParamsViewModel,
                topBarViewModel = topBarViewModel,
                currentTheme = currentTheme,
                onThemeChanged = onThemeChanged
            )
        }

        composable("searchResult") {
            ItemListScreen(
                navController = navController,
                flightViewModel = flightViewModel,
                searchParamsViewModel = searchParamsViewModel
            )
        }

        composable("selectSeat") {
            SeatListScreen(
                navController = navController,
                flightViewModel = flightViewModel,
                bookingViewModel = bookingViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("ticketDetail") {
            TicketDetailScreen(
                navController = navController,
                bookingViewModel = bookingViewModel,
                paymentViewModel = paymentViewModel,
                topBarViewModel = topBarViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("booking_history") {
            BookingHistoryScreen(
                navController = navController,
                viewModel = bookingViewModel,
                paymentViewModel = paymentViewModel,
                topBarViewModel = topBarViewModel
            )
        }
    }
}

