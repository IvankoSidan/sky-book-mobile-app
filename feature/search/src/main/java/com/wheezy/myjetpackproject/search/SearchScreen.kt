package com.wheezy.myjetpackproject.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wheezy.myjetpackproject.core.model.LocationModel
import com.wheezy.myjetpackproject.core.model.Notification
import com.wheezy.myjetpackproject.core.model.ThemeOption
import com.wheezy.myjetpackproject.core.model.User
import com.wheezy.myjetpackproject.core.ui.R
import com.wheezy.myjetpackproject.core.ui.components.*
import com.wheezy.myjetpackproject.search.DatePicker.DatePickerScreen
import com.wheezy.myjetpackproject.search.components.YellowTitle
import io.github.muddz.styleabletoast.StyleableToast

@Composable
fun SearchScreen(
    userState: User?,
    locations: List<LocationModel>,
    classItems: List<String>,
    currentTheme: ThemeOption,
    notifications: List<Notification>,
    unreadCount: Int,
    isLoadingNotifications: Boolean,
    onThemeChanged: (ThemeOption) -> Unit,
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToResult: (from: String, to: String, passengers: Int) -> Unit,
    onRefreshNotifications: () -> Unit,
    onDeleteAllNotifications: () -> Unit,
    onFetchInitialData: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val isLoadingLocations = locations.isEmpty()

    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }

    var classes by remember { mutableStateOf("") }
    var adultPassenger by remember { mutableStateOf("1") }
    var childPassenger by remember { mutableStateOf("0") }
    var isDrawerOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onFetchInitialData()
    }

    val locationNames = remember(locations) {
        locations.map { it.name }
    }

    SideDrawer(
        isOpen = isDrawerOpen,
        onClose = { isDrawerOpen = false },
        currentTheme = currentTheme,
        onThemeSelected = onThemeChanged,
        onLogout = onLogout,
        onOpenBookingHistory = onNavigateToHistory
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    user = userState,
                    notifications = notifications,
                    unreadCount = unreadCount,
                    isLoading = isLoadingNotifications,
                    onOpenDrawer = { isDrawerOpen = true },
                    onRefreshNotifications = onRefreshNotifications,
                    onDeleteAllNotifications = onDeleteAllNotifications
                )
            },
            bottomBar = bottomBar
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {

                    YellowTitle(text = "From")
                    SearchableDropDownMenu(
                        value = from,
                        onValueChange = { from = it },
                        items = locationNames,
                        loadingIcon = painterResource(id = R.drawable.from_ic),
                        hint = "Select departure location",
                        showLocationLoading = isLoadingLocations
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    YellowTitle(text = "To")
                    SearchableDropDownMenu(
                        value = to,
                        onValueChange = { to = it },
                        items = locationNames,
                        loadingIcon = painterResource(id = R.drawable.from_ic),
                        hint = "Select destination",
                        showLocationLoading = isLoadingLocations
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
                        YellowTitle(text = "Departure Date", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(16.dp))
                        YellowTitle(text = "Return Date", modifier = Modifier.weight(1f))
                    }
                    DatePickerScreen(modifier = Modifier.weight(1f))

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
                                onNavigateToResult(from, to, numPassenger)
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