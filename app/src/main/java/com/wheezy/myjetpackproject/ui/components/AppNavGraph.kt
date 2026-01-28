package com.wheezy.myjetpackproject.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wheezy.myjetpackproject.booking.ui.ticket_detail.TicketDetailScreen
import com.wheezy.myjetpackproject.booking.viewmodel.BookingViewModel
import com.wheezy.myjetpackproject.booking.viewmodel.PaymentViewModel
import com.wheezy.myjetpackproject.navigation.Screen
import com.wheezy.myjetpackproject.ui.activities.SplashScreen
import com.wheezy.myjetpackproject.core.model.ThemeOption
import com.wheezy.myjetpackproject.mylibrary.auth.LoginPageScreen
import com.wheezy.myjetpackproject.mylibrary.auth.RegisterPageScreen
import com.wheezy.myjetpackproject.mylibrary.auth.AuthViewModel
import com.wheezy.myjetpackproject.common_vm.FlightViewModel
import com.wheezy.myjetpackproject.core.common_vm.TopBarViewModel
import com.wheezy.myjetpackproject.core.ui.components.MyBottomBar
import com.wheezy.myjetpackproject.feature.booking.ui.history.BookingHistoryScreen
import com.wheezy.myjetpackproject.feature.booking.ui.seat_select.SeatListScreen
import com.wheezy.myjetpackproject.feature.search.SearchScreen
import com.wheezy.myjetpackproject.search.SearchParamsViewModel
import components.ItemListScreen

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
    val userState by authViewModel.user.collectAsState()
    val notifications by topBarViewModel.notifications.collectAsState()
    val isLoadingNotifications by topBarViewModel.isLoading.collectAsState()

    val startDestination = if (userState == null) Screen.Splash.route else Screen.Main.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                userState = userState,
                onGetStartedClick = {
                    val destination = if (userState != null) Screen.Main.route else Screen.Login.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onAutoNavigate = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterPageScreen(
                viewModel = authViewModel,
                onNavigateBack = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginPageScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            val locations by flightViewModel.locations.collectAsState()
            val classItems by flightViewModel.classSeats.collectAsState()

            SearchScreen(
                userState = userState,
                locations = locations,
                classItems = classItems,
                currentTheme = currentTheme,
                notifications = notifications,
                unreadCount = topBarViewModel.unreadCount,
                isLoadingNotifications = isLoadingNotifications,
                onThemeChanged = onThemeChanged,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHistory = { navController.navigate(Screen.BookingHistory.route) },
                onNavigateToResult = { from, to, passengers ->
                    searchParamsViewModel.setParams(from, to, passengers)
                    navController.navigate(Screen.SearchResult.route)
                },
                onRefreshNotifications = { topBarViewModel.loadNotifications() },
                onDeleteAllNotifications = { topBarViewModel.deleteAllNotifications() },
                onFetchInitialData = {
                    flightViewModel.fetchLocations()
                    flightViewModel.fetchClassSeats()
                },
                bottomBar = { MyBottomBar(navController) }
            )
        }

        composable(Screen.SearchResult.route) {
            ItemListScreen(
                navController = navController,
                flightViewModel = flightViewModel,
                searchParamsViewModel = searchParamsViewModel
            )
        }

        composable(Screen.SelectSeat.route) {
            SeatListScreen(
                navController = navController,
                flightViewModel = flightViewModel,
                bookingViewModel = bookingViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.TicketDetail.route) {
            TicketDetailScreen(
                navController = navController,
                bookingViewModel = bookingViewModel,
                paymentViewModel = paymentViewModel,
                topBarViewModel = topBarViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.BookingHistory.route) {
            BookingHistoryScreen(
                navController = navController,
                viewModel = bookingViewModel,
                paymentViewModel = paymentViewModel,
                topBarViewModel = topBarViewModel
            )
        }
    }
}