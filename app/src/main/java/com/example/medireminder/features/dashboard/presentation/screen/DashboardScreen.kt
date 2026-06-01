package com.example.medireminder.features.dashboard.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.dashboard.presentation.components.DashboardSummaryCard
import com.example.medireminder.features.dashboard.presentation.components.ExpiringMedicineCard
import com.example.medireminder.features.dashboard.presentation.components.FamilyMemberOverviewCard
import com.example.medireminder.features.dashboard.presentation.components.LowStockMedicineCard
import com.example.medireminder.features.dashboard.presentation.components.MissedReminderCard
import com.example.medireminder.features.dashboard.presentation.components.TodayReminderCard
import com.example.medireminder.features.dashboard.presentation.components.UpcomingReminderCard
import com.example.medireminder.features.dashboard.presentation.viewmodel.DashboardViewModel
import com.example.medireminder.features.settings.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onMemberClick: (String) -> Unit,
    onAddMember: () -> Unit,
    onViewAllMembers: () -> Unit,
    onAddMedicine: () -> Unit,
    onAddReminder: () -> Unit,
    onViewStock: () -> Unit,
    onViewReports: () -> Unit,
    onReminderClick: (String) -> Unit,
    onLowStockClick: () -> Unit,
    onMedicineStockClick: (String) -> Unit,
    onMissedClick: () -> Unit,
    onMedicineDashboardClick: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val timeFormat = settingsState.settings.timeFormat
    val overview = uiState.overview

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("MediReminder", fontWeight = FontWeight.Bold)
                        Text("Your care overview", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    DashboardSummaryCard("Today", overview?.todayReminderCount ?: 0, Modifier.weight(1f))
                    DashboardSummaryCard("Upcoming", overview?.upcomingReminderCount ?: 0, Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    DashboardSummaryCard("Missed", overview?.missedReminderCount ?: 0, Modifier.weight(1f), warning = true)
                    DashboardSummaryCard("Low Stock", overview?.lowStockCount ?: 0, Modifier.weight(1f), warning = true)
                }
            }

            item { QuickActions(onAddMedicine, onAddReminder, onViewStock, onViewReports) }

            item { SectionHeader("Today's Reminders") }
            if (uiState.todayReminders.isEmpty()) {
                item { EmptyText("No reminders scheduled for today") }
            } else {
                items(uiState.todayReminders.take(5)) { item ->
                    TodayReminderCard(
                        item = item,
                        onClick = { onReminderClick(item.reminderId) },
                        timeFormat = timeFormat
                    )
                }
            }

            item { SectionHeader("Upcoming Reminders") }
            if (uiState.upcomingReminders.isEmpty()) {
                item { EmptyText("No upcoming reminders") }
            } else {
                items(uiState.upcomingReminders.take(5)) { item ->
                    UpcomingReminderCard(
                        item = item,
                        onClick = { onReminderClick(item.reminderId) },
                        timeFormat = timeFormat
                    )
                }
            }

            item { SectionHeader("Missed Reminders", actionText = "View History", onAction = onMissedClick) }
            if (uiState.missedReminders.isEmpty()) {
                item { EmptyText("No missed reminders today") }
            } else {
                items(uiState.missedReminders.take(5)) { item ->
                    MissedReminderCard(
                        item = item,
                        onClick = onMissedClick,
                        timeFormat = timeFormat
                    )
                }
            }

            item { SectionHeader("Low Stock Medicines", actionText = "View All", onAction = onLowStockClick) }
            if (uiState.lowStockMedicines.isEmpty()) {
                item { EmptyText("No low stock alerts") }
            } else {
                items(uiState.lowStockMedicines.take(5)) { item ->
                    LowStockMedicineCard(item = item, onClick = { onMedicineStockClick(item.medicineId) })
                }
            }

            item { SectionHeader("Expiring Medicines") }
            if (uiState.expiringMedicines.isEmpty()) {
                item { EmptyText("No medicines expiring soon") }
            } else {
                items(uiState.expiringMedicines.take(5)) { item ->
                    ExpiringMedicineCard(item = item, onClick = { onMedicineDashboardClick(item.medicineId) })
                }
            }

            item { SectionHeader("Family Members", actionText = "View All", onAction = onViewAllMembers) }
            if (uiState.familyMembers.isEmpty()) {
                item { EmptyText("No active family members") }
            } else {
                items(uiState.familyMembers.take(5)) { item ->
                    FamilyMemberOverviewCard(item = item, onClick = { onMemberClick(item.memberId) })
                }
            }
        }
    }
}

@Composable
private fun QuickActions(
    onAddMedicine: () -> Unit,
    onAddReminder: () -> Unit,
    onViewStock: () -> Unit,
    onViewReports: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onAddMedicine, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("Medicine")
        }
        Button(onClick = onAddReminder, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Text("Reminder")
        }
        OutlinedButton(onClick = onViewStock, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
        }
        OutlinedButton(onClick = onViewReports, modifier = Modifier.weight(1f)) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
        }
    }
}

@Composable
private fun SectionHeader(title: String, actionText: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}
