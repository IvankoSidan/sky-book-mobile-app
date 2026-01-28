package com.wheezy.myjetpackproject.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object SearchResult : Screen("searchResult")
    object SelectSeat : Screen("selectSeat")
    object TicketDetail : Screen("ticketDetail")
    object BookingHistory : Screen("booking_history")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}