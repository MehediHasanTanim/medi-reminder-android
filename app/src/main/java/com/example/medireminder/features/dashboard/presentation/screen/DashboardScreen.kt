package com.example.medireminder.features.dashboard.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.dashboard.domain.model.DashboardOverview
import com.example.medireminder.features.dashboard.domain.model.ExpiringMedicineItem
import com.example.medireminder.features.dashboard.domain.model.FamilyOverviewItem
import com.example.medireminder.features.dashboard.domain.model.LowStockItem
import com.example.medireminder.features.dashboard.domain.model.MissedReminderItem
import com.example.medireminder.features.dashboard.domain.model.TodayReminderItem
import com.example.medireminder.features.dashboard.domain.model.UpcomingReminderItem
import com.example.medireminder.features.dashboard.presentation.components.DashboardStatCard
import com.example.medireminder.features.dashboard.presentation.components.ExpiringMedicineCard
import com.example.medireminder.features.dashboard.presentation.components.FamilyMemberOverviewCard
import com.example.medireminder.features.dashboard.presentation.components.LowStockMedicineCard
import com.example.medireminder.features.dashboard.presentation.components.MissedReminderCard
import com.example.medireminder.features.dashboard.presentation.components.TodayReminderCard
import com.example.medireminder.features.dashboard.presentation.components.UpcomingReminderCard
import com.example.medireminder.features.dashboard.presentation.state.DashboardUiState
import com.example.medireminder.features.dashboard.presentation.viewmodel.DashboardViewModel
import com.example.medireminder.features.settings.domain.model.TimeFormat
import com.example.medireminder.features.settings.presentation.viewmodel.SettingsViewModel
import com.example.medireminder.ui.theme.PrimaryGreen
import com.example.medireminder.ui.theme.SecondaryGreen

// ──────────────────────────────────────────────
// Color palette for stat cards
// ──────────────────────────────────────────────
private val ReminderColor = Color(0xFF1B5E20)
private val MedicineColor = Color(0xFF1565C0)
private val FamilyColor = Color(0xFF6A1B9A)
private val StockColor = Color(0xFFE65100)

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

    DashboardScreenContent(
        uiState = uiState,
        timeFormat = timeFormat,
        onRefresh = viewModel::refresh,
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

@Composable
internal fun DashboardScreenContent(
    uiState: DashboardUiState,
    timeFormat: TimeFormat = TimeFormat.HOUR_12,
    onRefresh: () -> Unit = {},
    onMemberClick: (String) -> Unit = {},
    onAddMember: () -> Unit = {},
    onViewAllMembers: () -> Unit = {},
    onAddMedicine: () -> Unit = {},
    onAddReminder: () -> Unit = {},
    onViewStock: () -> Unit = {},
    onViewReports: () -> Unit = {},
    onReminderClick: (String) -> Unit = {},
    onLowStockClick: () -> Unit = {},
    onMedicineStockClick: (String) -> Unit = {},
    onMissedClick: () -> Unit = {},
    onMedicineDashboardClick: (String) -> Unit = {}
) {
    val overview = uiState.overview

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Greeting Header ──
        item {
            GreetingHeader(onRefresh = onRefresh)
        }

        // ── Stat Cards ──
        item {
            StatsRow(overview = overview)
        }

        // ── Quick Actions ──
        item {
            SectionHeader(title = "Quick Actions")
            Spacer(modifier = Modifier.height(12.dp))
            QuickActions(onAddMedicine, onAddReminder, onViewStock, onViewReports)
        }

        // ── Today's Reminders ──
        item {
            SectionHeader(title = "Today's Schedule", actionText = "View All", onAction = { onReminderClick("") })
        }
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

        // ── Upcoming Reminders ──
        item {
            SectionHeader(title = "Upcoming")
        }
        if (uiState.upcomingReminders.isEmpty()) {
            item { EmptyText("No upcoming reminders") }
        } else {
            items(uiState.upcomingReminders.take(3)) { item ->
                UpcomingReminderCard(
                    item = item,
                    onClick = { onReminderClick(item.reminderId) },
                    timeFormat = timeFormat
                )
            }
        }

        // ── Missed Reminders ──
        if (uiState.missedReminders.isNotEmpty()) {
            item {
                SectionHeader(title = "Missed", actionText = "View History", onAction = onMissedClick)
            }
            items(uiState.missedReminders.take(3)) { item ->
                MissedReminderCard(
                    item = item,
                    onClick = onMissedClick,
                    timeFormat = timeFormat
                )
            }
        }

        // ── Low Stock ──
        if (uiState.lowStockMedicines.isNotEmpty()) {
            item {
                SectionHeader(title = "Low Stock", actionText = "View All", onAction = onLowStockClick)
            }
            items(uiState.lowStockMedicines.take(3)) { item ->
                LowStockMedicineCard(item = item, onClick = { onMedicineStockClick(item.medicineId) })
            }
        }

        // ── Expiring Medicines ──
        if (uiState.expiringMedicines.isNotEmpty()) {
            item {
                SectionHeader(title = "Expiring Soon")
            }
            items(uiState.expiringMedicines.take(3)) { item ->
                ExpiringMedicineCard(item = item, onClick = { onMedicineDashboardClick(item.medicineId) })
            }
        }

        // ── Family Members ──
        item {
            SectionHeader(title = "Family Members", actionText = "View All", onAction = onViewAllMembers)
        }
        if (uiState.familyMembers.isEmpty()) {
            item { EmptyText("No active family members") }
        } else {
            items(uiState.familyMembers.take(5)) { item ->
                FamilyMemberOverviewCard(item = item, onClick = { onMemberClick(item.memberId) })
            }
        }

        // Bottom spacer
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ──────────────────────────────────────────────
// Greeting Header
// ──────────────────────────────────────────────

@Composable
private fun GreetingHeader(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello, John 👋",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Have a nice day",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Notification bell
        FilledIconButton(
            onClick = onRefresh,
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = PrimaryGreen.copy(alpha = 0.1f)
            ),
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                Icons.Default.NotificationsNone,
                contentDescription = "Notifications",
                tint = PrimaryGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ──────────────────────────────────────────────
// Stats Row
// ──────────────────────────────────────────────

@Composable
private fun StatsRow(overview: DashboardOverview?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        DashboardStatCard(
            title = "Reminders",
            count = (overview?.todayReminderCount ?: 0) + (overview?.upcomingReminderCount ?: 0),
            icon = Icons.Default.NotificationsActive,
            color = ReminderColor,
            modifier = Modifier.weight(1f)
        )
        DashboardStatCard(
            title = "Medicines",
            count = (overview?.lowStockCount ?: 0) + (overview?.expiringMedicineCount ?: 0) + 5,
            icon = Icons.Default.Medication,
            color = MedicineColor,
            modifier = Modifier.weight(1f)
        )
        DashboardStatCard(
            title = "Family",
            count = overview?.familyMemberCount ?: 0,
            icon = Icons.Default.FamilyRestroom,
            color = FamilyColor,
            modifier = Modifier.weight(1f)
        )
        DashboardStatCard(
            title = "Low Stock",
            count = overview?.lowStockCount ?: 0,
            icon = Icons.Default.ShoppingCart,
            color = StockColor,
            modifier = Modifier.weight(1f)
        )
    }
}

// ──────────────────────────────────────────────
// Quick Actions
// ──────────────────────────────────────────────

@Composable
private fun QuickActions(
    onAddMedicine: () -> Unit,
    onAddReminder: () -> Unit,
    onViewStock: () -> Unit,
    onViewReports: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        QuickActionButton(
            icon = Icons.Default.Medication,
            label = "Medicine",
            onClick = onAddMedicine,
            color = MedicineColor,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.Add,
            label = "Reminder",
            onClick = onAddReminder,
            color = ReminderColor,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.ShoppingCart,
            label = "Stock",
            onClick = onViewStock,
            color = StockColor,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.CalendarMonth,
            label = "Reports",
            onClick = onViewReports,
            color = FamilyColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// ──────────────────────────────────────────────
// Section Header
// ──────────────────────────────────────────────

@Composable
internal fun SectionHeader(title: String, actionText: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionText,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// ──────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DashboardScreenPreview() {
    val mockUiState = DashboardUiState(
        isLoading = false,
        overview = DashboardOverview(
            todayReminderCount = 4,
            upcomingReminderCount = 6,
            missedReminderCount = 2,
            lowStockCount = 3,
            expiringMedicineCount = 1,
            familyMemberCount = 4
        ),
        todayReminders = listOf(
            TodayReminderItem("1", "Alice", "Napa 500mg", "500mg", 36000000, "PENDING", "Take after food"),
            TodayReminderItem("2", "Dad", "Metformin", "500mg", 43200000, "TAKEN", "With breakfast"),
            TodayReminderItem("3", "Mom", "Paracetamol", "650mg", 50400000, "PENDING", null),
            TodayReminderItem("4", "Alice", "Vitamin D", "1000IU", 57600000, "PENDING", null)
        ),
        upcomingReminders = listOf(
            UpcomingReminderItem("5", "Dad", "Aspirin", 64800000, "75mg"),
            UpcomingReminderItem("6", "Mom", "Omeprazole", 72000000, "20mg")
        ),
        missedReminders = listOf(
            MissedReminderItem("101", "Alice", "Napa 500mg", 25200000, null),
            MissedReminderItem("102", "Dad", "Metformin", 28800000, null)
        ),
        lowStockMedicines = listOf(
            LowStockItem("m1", "Napa 500mg", 3.0, "pills", 10.0),
            LowStockItem("m2", "Metformin", 2.0, "pills", 5.0),
            LowStockItem("m3", "Aspirin", 1.0, "pills", 5.0)
        ),
        expiringMedicines = listOf(
            ExpiringMedicineItem("m4", "Cetirizine", 1830000000000, 15)
        ),
        familyMembers = listOf(
            FamilyOverviewItem("f1", "Alice", "Daughter", 3, 2, 0),
            FamilyOverviewItem("f2", "Dad", "Father", 2, 1, 1),
            FamilyOverviewItem("f3", "Mom", "Mother", 2, 1, 0),
            FamilyOverviewItem("f4", "John", "Self", 1, 0, 0)
        )
    )

    MaterialTheme {
        DashboardScreenContent(uiState = mockUiState)
    }
}
