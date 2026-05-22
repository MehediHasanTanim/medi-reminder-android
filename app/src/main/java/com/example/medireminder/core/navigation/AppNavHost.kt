package com.example.medireminder.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medireminder.features.dashboard.DashboardScreen
import com.example.medireminder.features.family.FamilyScreen
import com.example.medireminder.features.history.HistoryScreen
import com.example.medireminder.features.medicine.MedicineScreen
import com.example.medireminder.features.reminder.ReminderScreen
import com.example.medireminder.features.reports.ReportsScreen
import com.example.medireminder.features.settings.SettingsScreen
import com.example.medireminder.features.stock.StockScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.Family.route) {
            FamilyScreen()
        }
        composable(Screen.Medicine.route) {
            MedicineScreen()
        }
        composable(Screen.Reminder.route) {
            ReminderScreen()
        }
        composable(Screen.Stock.route) {
            StockScreen()
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Reports.route) {
            ReportsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
