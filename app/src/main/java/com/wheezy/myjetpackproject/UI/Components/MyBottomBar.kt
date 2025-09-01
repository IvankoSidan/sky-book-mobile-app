package com.wheezy.myjetpackproject.UI.Components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wheezy.myjetpackproject.R
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wheezy.myjetpackproject.Data.Dto.BottomMenuItem

@Composable
fun prepareBottomMenu(): List<BottomMenuItem> {
    return listOf(
        BottomMenuItem("Home", Icons.Default.Home, "main"),
        BottomMenuItem("Flights", Icons.Default.Flight, "searchResult"),
        BottomMenuItem("Seats", Icons.Default.EventSeat, "selectSeat"),
        BottomMenuItem("Ticket", Icons.Default.ConfirmationNumber, "ticketDetail")
    )
}

@Composable
fun MyBottomBar(navController: NavHostController) {
    val bottomMenuItemList = prepareBottomMenu()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        backgroundColor = colorResource(id = R.color.darkPurple),
        elevation = 4.dp,
        modifier = Modifier.height(48.dp)
    ) {
        bottomMenuItemList.forEach { item ->
            val isSelected = currentRoute == item.route
            val tint by animateColorAsState(
                targetValue = if (isSelected) colorResource(id = R.color.orange) else Color.Gray,
                animationSpec = tween(durationMillis = 300)
            )

            BottomNavigationItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.label,
                            fontSize = 9.sp,
                            color = tint
                        )
                    }
                },
                selectedContentColor = colorResource(id = R.color.orange),
                unselectedContentColor = Color.Gray,
                alwaysShowLabel = true
            )
        }
    }
}


