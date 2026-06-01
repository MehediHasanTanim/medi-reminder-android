package com.example.medireminder.features.dashboard.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medireminder.core.common.DateTimeUtils
import com.example.medireminder.features.dashboard.domain.model.ExpiringMedicineItem
import com.example.medireminder.features.dashboard.domain.model.FamilyOverviewItem
import com.example.medireminder.features.dashboard.domain.model.LowStockItem
import com.example.medireminder.features.dashboard.domain.model.MedicineDashboard
import com.example.medireminder.features.dashboard.domain.model.MissedReminderItem
import com.example.medireminder.features.dashboard.domain.model.TodayReminderItem
import com.example.medireminder.features.dashboard.domain.model.UpcomingReminderItem
import com.example.medireminder.features.settings.domain.model.TimeFormat

@Composable
fun DashboardSummaryCard(title: String, count: Int, modifier: Modifier = Modifier, warning: Boolean = false) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (warning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(count.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TodayReminderCard(
    item: TodayReminderItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.HOUR_12
) {
    ReminderCardShell(
        title = item.medicineName,
        subtitle = "${item.memberName} • ${item.dosage}",
        meta = "${DateTimeUtils.formatTime(item.reminderTime, timeFormat)} • ${item.status}",
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun UpcomingReminderCard(
    item: UpcomingReminderItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.HOUR_12
) {
    ReminderCardShell(
        title = item.medicineName,
        subtitle = item.memberName,
        meta = "${DateTimeUtils.formatTime(item.reminderTime, timeFormat)}${item.dosage?.let { " • $it" } ?: ""}",
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun MissedReminderCard(
    item: MissedReminderItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.HOUR_12
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(item.medicineName, fontWeight = FontWeight.Bold)
                Text(
                    "${item.memberName} • ${DateTimeUtils.formatTime(item.scheduledTime, timeFormat)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun LowStockMedicineCard(item: LowStockItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AlertItemCard(
        title = item.medicineName,
        body = "${item.currentQuantity} ${item.unit} left • threshold ${item.lowStockThreshold}",
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun ExpiringMedicineCard(item: ExpiringMedicineItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AlertItemCard(
        title = item.medicineName,
        body = "Expires in ${item.daysRemaining} days",
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun FamilyMemberOverviewCard(item: FamilyOverviewItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(item.fullName, fontWeight = FontWeight.Bold)
            Text(item.relationship ?: "Family member", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text("${item.activeMedicineCount} active medicines • ${item.todayReminderCount} reminders today")
            if (item.missedReminderCount > 0) {
                Text("${item.missedReminderCount} missed today", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun MedicineDashboardCard(dashboard: MedicineDashboard, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(dashboard.medicineName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Current stock: ${dashboard.currentStock} ${dashboard.unit}")
            Text("Daily consumption: ${dashboard.dailyConsumption} ${dashboard.unit}/day")
            Text("Remaining days: ${dashboard.remainingDays?.toString() ?: "N/A"}")
            Text("Compliance: ${dashboard.reminderCompliance}%")
        }
    }
}

@Composable
private fun ReminderCardShell(title: String, subtitle: String, meta: String, onClick: () -> Unit, modifier: Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            Text(meta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AlertItemCard(title: String, body: String, onClick: () -> Unit, modifier: Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodySmall)
        }
    }
}
