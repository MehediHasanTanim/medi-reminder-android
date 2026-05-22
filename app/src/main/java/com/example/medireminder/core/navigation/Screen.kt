package com.example.medireminder.core.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Family : Screen("family")
    object Medicine : Screen("medicine")
    object Reminder : Screen("reminder")
    object Stock : Screen("stock")
    object History : Screen("history")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
