package com.example.medireminder.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medireminder.features.dashboard.presentation.screen.DashboardScreen
import com.example.medireminder.features.dashboard.presentation.screen.FamilyMemberDashboardScreen
import com.example.medireminder.features.splash.presentation.SplashScreen
import com.example.medireminder.features.dashboard.presentation.screen.MedicineDashboardScreen
import com.example.medireminder.features.family.presentation.screen.AddEditFamilyMemberScreen
import com.example.medireminder.features.family.presentation.screen.FamilyMemberListScreen
import com.example.medireminder.features.family.presentation.screen.FamilyMemberDetailsScreen
import com.example.medireminder.features.history.HistoryScreen
import com.example.medireminder.features.history.presentation.screen.ReminderHistoryDetailsScreen
import com.example.medireminder.features.medicine.presentation.screen.AddEditMedicineScreen
import com.example.medireminder.features.medicine.presentation.screen.MedicineDetailsScreen
import com.example.medireminder.features.medicine.presentation.screen.MedicineListScreen
import com.example.medireminder.features.membermedicine.presentation.screen.AssignMedicineToMemberScreen
import com.example.medireminder.features.membermedicine.presentation.screen.MemberMedicineDetailsScreen
import com.example.medireminder.features.membermedicine.presentation.screen.MemberMedicineEditScreen
import com.example.medireminder.features.membermedicine.presentation.screen.MemberMedicineListScreen
import com.example.medireminder.features.reminder.presentation.screen.AddEditReminderScreen
import com.example.medireminder.features.reminder.presentation.screen.ReminderDetailsScreen
import com.example.medireminder.features.reminder.presentation.screen.ReminderListScreen
import com.example.medireminder.features.reports.ReportsScreen
import com.example.medireminder.features.reports.presentation.screen.DailyReminderReportScreen
import com.example.medireminder.features.reports.presentation.screen.ExpiredMedicineReportScreen
import com.example.medireminder.features.reports.presentation.screen.LowStockReportScreen
import com.example.medireminder.features.reports.presentation.screen.MedicineWiseReportScreen
import com.example.medireminder.features.reports.presentation.screen.MemberWiseReportScreen
import com.example.medireminder.features.reports.presentation.screen.MonthlyComplianceReportScreen
import com.example.medireminder.features.reports.presentation.screen.StockUsageReportScreen
import com.example.medireminder.features.reports.presentation.screen.WeeklyComplianceReportScreen
import com.example.medireminder.features.settings.SettingsScreen
import com.example.medireminder.features.settings.presentation.screen.AppearanceSettingsScreen
import com.example.medireminder.features.settings.presentation.screen.NotificationSettingsScreen
import com.example.medireminder.features.settings.presentation.screen.ReminderSettingsScreen
import com.example.medireminder.features.settings.presentation.screen.StockSettingsScreen
import com.example.medireminder.features.stock.presentation.screen.LowStockScreen
import com.example.medireminder.features.stock.presentation.screen.AddStockScreen
import com.example.medireminder.features.stock.presentation.screen.MedicineStockDashboardScreen
import com.example.medireminder.features.stock.presentation.screen.RefillStockScreen
import com.example.medireminder.features.stock.presentation.screen.StockDetailsScreen
import com.example.medireminder.features.stock.presentation.screen.StockTransactionHistoryScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onMemberClick = { id -> navController.navigate(Screen.FamilyMemberDetails.createRoute(id)) },
                onAddMember = { navController.navigate(Screen.FamilyMemberAdd.route) },
                onViewAllMembers = { navController.navigate(Screen.FamilyMembers.route) },
                onAddMedicine = { navController.navigate(Screen.MedicineAdd.route) },
                onAddReminder = { navController.navigate(Screen.ReminderAdd.route) },
                onViewStock = { navController.navigate(Screen.StockDashboard.route) },
                onViewReports = { navController.navigate(Screen.Reports.route) },
                onReminderClick = { id -> navController.navigate(Screen.ReminderDetails.createRoute(id)) },
                onLowStockClick = { navController.navigate(Screen.LowStock.route) },
                onMedicineStockClick = { id -> navController.navigate(Screen.StockDetails.createRoute(id)) },
                onMissedClick = { navController.navigate(Screen.ReminderHistory.route) },
                onMedicineDashboardClick = { id -> navController.navigate(Screen.MedicineDashboard.createRoute(id)) }
            )
        }
        composable(
            route = Screen.MedicineDashboard.route,
            arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
        ) { backStackEntry ->
            MedicineDashboardScreen(
                medicineId = backStackEntry.arguments?.getString("medicineId") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onViewStockDetails = { id -> navController.navigate(Screen.StockDetails.createRoute(id)) },
                onViewTransactions = { id -> navController.navigate(Screen.StockTransactions.createRoute(id)) },
                onViewAssignedMembers = { navController.navigate(Screen.MemberMedicines.route) },
                onViewHistory = { navController.navigate(Screen.ReminderHistory.route) }
            )
        }
        composable(
            route = Screen.FamilyDashboard.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) { backStackEntry ->
            FamilyMemberDashboardScreen(
                memberId = backStackEntry.arguments?.getString("memberId") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onEditMember = { id -> navController.navigate(Screen.FamilyMemberEdit.createRoute(id)) }
            )
        }

        // Family
        composable(Screen.FamilyMembers.route) {
            FamilyMemberListScreen(
                onAddMember = { navController.navigate(Screen.FamilyMemberAdd.route) },
                onMemberClick = { id -> navController.navigate(Screen.FamilyMemberDetails.createRoute(id)) }
            )
        }
        composable(Screen.FamilyMemberAdd.route) {
            AddEditFamilyMemberScreen(
                memberId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.FamilyMemberEdit.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddEditFamilyMemberScreen(
                memberId = backStackEntry.arguments?.getString("memberId"),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.FamilyMemberDetails.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) { backStackEntry ->
            FamilyMemberDetailsScreen(
                memberId = backStackEntry.arguments?.getString("memberId") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onEditMember = { id -> navController.navigate(Screen.FamilyMemberEdit.createRoute(id)) },
                onAssignMedicine = { navController.navigate(Screen.MemberMedicineAdd.route) }
            )
        }

        // Medicine
        composable(Screen.Medicines.route) {
            MedicineListScreen(
                onAddMedicine = { navController.navigate(Screen.MedicineAdd.route) },
                onMedicineClick = { id -> navController.navigate(Screen.MedicineDetails.createRoute(id)) }
            )
        }
        composable(Screen.MedicineAdd.route) {
            AddEditMedicineScreen(
                medicineId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.MedicineEdit.route,
            arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddEditMedicineScreen(
                medicineId = backStackEntry.arguments?.getString("medicineId"),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.MedicineDetails.route,
            arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
        ) { backStackEntry ->
            MedicineDetailsScreen(
                medicineId = backStackEntry.arguments?.getString("medicineId") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onEditMedicine = { id -> navController.navigate(Screen.MedicineEdit.createRoute(id)) }
            )
        }

        // Member Medicine Assignment
        composable(Screen.MemberMedicines.route) {
            MemberMedicineListScreen(
                onAddAssignment = { navController.navigate(Screen.MemberMedicineAdd.route) },
                onAssignmentClick = { id -> navController.navigate(Screen.MemberMedicineDetails.createRoute(id)) }
            )
        }
        composable(Screen.MemberMedicineAdd.route) {
            AssignMedicineToMemberScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.MemberMedicineEdit.route,
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            MemberMedicineEditScreen(
                assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.MemberMedicineDetails.route,
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            MemberMedicineDetailsScreen(
                assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onEditAssignment = { id -> navController.navigate(Screen.MemberMedicineEdit.createRoute(id)) }
            )
        }

        // Reminders
        composable(Screen.Reminders.route) {
            ReminderListScreen(
                onAddReminder = { navController.navigate(Screen.ReminderAdd.route) },
                onReminderClick = { id -> navController.navigate(Screen.ReminderDetails.createRoute(id)) }
            )
        }
        composable(Screen.ReminderAdd.route) {
            AddEditReminderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.ReminderEdit.route,
            arguments = listOf(navArgument("reminderId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddEditReminderScreen(
                reminderId = backStackEntry.arguments?.getString("reminderId"),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.ReminderDetails.route,
            arguments = listOf(navArgument("reminderId") { type = NavType.StringType })
        ) { backStackEntry ->
            ReminderDetailsScreen(
                reminderId = backStackEntry.arguments?.getString("reminderId") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onEditReminder = { id -> navController.navigate(Screen.ReminderEdit.createRoute(id)) }
            )
        }

        // Stock
        composable(Screen.StockDashboard.route) {
            MedicineStockDashboardScreen(
                onAddStock = { navController.navigate(Screen.StockAdd.route) },
                onStockClick = { id -> navController.navigate(Screen.StockDetails.createRoute(id)) },
                onViewLowStock = { navController.navigate(Screen.LowStock.route) }
            )
        }
        composable(Screen.StockAdd.route) {
            AddStockScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.StockDetails.route,
            arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
        ) { backStackEntry ->
            StockDetailsScreen(
                medicineId = backStackEntry.arguments?.getString("medicineId") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onRefillStock = { id -> navController.navigate(Screen.StockRefill.createRoute(id)) },
                onViewHistory = { id -> navController.navigate(Screen.StockTransactions.createRoute(id)) }
            )
        }
        composable(
            route = Screen.StockRefill.route,
            arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
        ) { backStackEntry ->
            RefillStockScreen(
                medicineId = backStackEntry.arguments?.getString("medicineId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.StockTransactions.route,
            arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
        ) { backStackEntry ->
            StockTransactionHistoryScreen(
                medicineId = backStackEntry.arguments?.getString("medicineId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.LowStock.route) {
            LowStockScreen(
                onNavigateBack = { navController.popBackStack() },
                onStockClick = { id -> navController.navigate(Screen.StockDetails.createRoute(id)) },
                onRefillStock = { id -> navController.navigate(Screen.StockRefill.createRoute(id)) }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onHistoryClick = { id -> navController.navigate(Screen.ReminderHistoryDetails.createRoute(id)) }
            )
        }
        composable(Screen.ReminderHistory.route) {
            HistoryScreen(
                onHistoryClick = { id -> navController.navigate(Screen.ReminderHistoryDetails.createRoute(id)) }
            )
        }
        composable(
            route = Screen.ReminderHistoryDetails.route,
            arguments = listOf(navArgument("historyId") { type = NavType.StringType })
        ) { backStackEntry ->
            ReminderHistoryDetailsScreen(
                historyId = backStackEntry.arguments?.getString("historyId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Reports.route) {
            ReportsScreen(onReportClick = { route -> navController.navigate(route) })
        }
        composable(Screen.ReportsDaily.route) { DailyReminderReportScreen() }
        composable(Screen.ReportsWeekly.route) { WeeklyComplianceReportScreen() }
        composable(Screen.ReportsMonthly.route) { MonthlyComplianceReportScreen() }
        composable(Screen.ReportsMemberWise.route) { MemberWiseReportScreen() }
        composable(Screen.ReportsMedicineWise.route) { MedicineWiseReportScreen() }
        composable(Screen.ReportsStockUsage.route) { StockUsageReportScreen() }
        composable(Screen.ReportsLowStock.route) { LowStockReportScreen() }
        composable(Screen.ReportsExpired.route) { ExpiredMedicineReportScreen() }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onAppearanceClick = { navController.navigate(Screen.SettingsAppearance.route) },
                onReminderClick = { navController.navigate(Screen.SettingsReminder.route) },
                onNotificationClick = { navController.navigate(Screen.SettingsNotification.route) },
                onStockClick = { navController.navigate(Screen.SettingsStock.route) }
            )
        }
        composable(Screen.SettingsAppearance.route) {
            AppearanceSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.SettingsReminder.route) {
            ReminderSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.SettingsNotification.route) {
            NotificationSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.SettingsStock.route) {
            StockSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
