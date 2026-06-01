package com.example.medireminder.features.dashboard

import androidx.compose.runtime.Composable
import com.example.medireminder.features.dashboard.presentation.screen.DashboardScreen as DashboardScreenContent

@Composable
fun DashboardScreen(
    onMemberClick: (String) -> Unit,
    onAddMember: () -> Unit,
    onViewAllMembers: () -> Unit,
    onAddMedicine: () -> Unit,
    onAddReminder: () -> Unit,
    onViewStock: () -> Unit,
    onViewReports: () -> Unit,
    onReminderClick: (String) -> Unit = {},
    onLowStockClick: () -> Unit = onViewStock,
    onMedicineStockClick: (String) -> Unit = {},
    onMissedClick: () -> Unit = {},
    onMedicineDashboardClick: (String) -> Unit = {}
) {
    DashboardScreenContent(
        onMemberClick = onMemberClick,
        onAddMember = onAddMember,
        onViewAllMembers = onViewAllMembers,
        onAddMedicine = onAddMedicine,
        onAddReminder = onAddReminder,
        onViewStock = onViewStock,
        onViewReports = onViewReports,
        onReminderClick = onReminderClick,
        onLowStockClick = onLowStockClick,
        onMedicineStockClick = onMedicineStockClick,
        onMissedClick = onMissedClick,
        onMedicineDashboardClick = onMedicineDashboardClick
    )
}
