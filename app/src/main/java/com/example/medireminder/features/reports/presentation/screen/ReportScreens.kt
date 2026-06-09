package com.example.medireminder.features.reports.presentation.screen

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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.reports.domain.model.ComplianceReport
import com.example.medireminder.features.reports.domain.model.DailyReminderReport
import com.example.medireminder.features.reports.presentation.components.ComplianceProgressCard
import com.example.medireminder.features.reports.presentation.components.ReportFilterBar
import com.example.medireminder.features.reports.presentation.components.ReportMetricCard
import com.example.medireminder.features.reports.presentation.components.StockUsageCard
import com.example.medireminder.features.reports.presentation.viewmodel.ReportsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val TakenGreen = Color(0xFF2E7D32)
private val MissedRed = Color(0xFFD32F2F)
private val SkippedOrange = Color(0xFFEF6C00)
private val SnoozedBlue = Color(0xFF1565C0)
private val DismissedGray = Color(0xFF757575)

// ─── Daily Reminder Report ───
@Composable
fun DailyReminderReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadDaily() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold(
        title = "Daily Reminder Report",
        subtitle = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date()),
        onRefresh = { viewModel.loadDaily() }
    ) {
        if (state.dailyReport != null) {
            DailyReportContent(state.dailyReport!!)
        } else {
            item { EmptyState() }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.DailyReportContent(report: DailyReminderReport) {
    item { ComplianceProgressCard("Today's Adherence", report.adherencePercentage) }
    // Metrics grid
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportMetricCard("Total", report.totalReminders.toString(), icon = Icons.Filled.Analytics, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
            ReportMetricCard("Taken", report.takenCount.toString(), icon = Icons.Filled.CheckCircle, color = TakenGreen, modifier = Modifier.weight(1f))
        }
    }
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportMetricCard("Missed", report.missedCount.toString(), icon = Icons.Filled.Cancel, color = MissedRed, modifier = Modifier.weight(1f))
            ReportMetricCard("Skipped", report.skippedCount.toString(), icon = Icons.Filled.SkipNext, color = SkippedOrange, modifier = Modifier.weight(1f))
        }
    }
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportMetricCard("Snoozed", report.snoozedCount.toString(), icon = Icons.Filled.Snooze, color = SnoozedBlue, modifier = Modifier.weight(1f))
            ReportMetricCard("Dismissed", report.dismissedCount.toString(), icon = Icons.Filled.NotInterested, color = DismissedGray, modifier = Modifier.weight(1f))
        }
    }
}

// ─── Weekly / Monthly Compliance Reports ───
@Composable
fun WeeklyComplianceReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadWeekly() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold(
        title = "Weekly Compliance Report",
        subtitle = "This week's adherence summary",
        onRefresh = { viewModel.loadWeekly() }
    ) {
        if (state.weeklyReport != null) {
            ComplianceContent(state.weeklyReport!!)
        } else {
            item { EmptyState() }
        }
    }
}

@Composable
fun MonthlyComplianceReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadMonthly() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold(
        title = "Monthly Compliance Report",
        subtitle = "This month's adherence summary",
        onRefresh = { viewModel.loadMonthly() }
    ) {
        if (state.monthlyReport != null) {
            ComplianceContent(state.monthlyReport!!)
        } else {
            item { EmptyState() }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.ComplianceContent(report: ComplianceReport) {
    item { ComplianceProgressCard(report.periodLabel, report.adherencePercentage) }
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportMetricCard("Total", report.totalReminders.toString(), icon = Icons.Filled.Analytics, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
            ReportMetricCard("Taken", report.takenCount.toString(), icon = Icons.Filled.CheckCircle, color = TakenGreen, modifier = Modifier.weight(1f))
        }
    }
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportMetricCard("Missed", report.missedCount.toString(), icon = Icons.Filled.Cancel, color = MissedRed, modifier = Modifier.weight(1f))
            ReportMetricCard("Skipped", report.skippedCount.toString(), icon = Icons.Filled.SkipNext, color = SkippedOrange, modifier = Modifier.weight(1f))
        }
    }
}

// ─── Member-wise Report ───
@Composable
fun MemberWiseReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadMemberWise() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold(
        title = "Member-wise Report",
        subtitle = "Adherence breakdown by family member",
        onRefresh = viewModel::loadMemberWise
    ) {
        if (state.memberWiseReports.isEmpty()) {
            item { EmptyState() }
        } else {
            items(state.memberWiseReports) { report ->
                val adherenceColor = when {
                    report.adherencePercentage >= 80 -> TakenGreen
                    report.adherencePercentage >= 50 -> SkippedOrange
                    else -> MissedRed
                }
                StockUsageCard(
                    title = report.memberName,
                    body = "Taken: ${report.takenCount}  •  Missed: ${report.missedCount}  •  Skipped: ${report.skippedCount}\n" +
                            "Medicines: ${report.activeMedicineCount}  •  Adherence: ${report.adherencePercentage}%",
                    severityColor = adherenceColor
                )
            }
        }
    }
}

// ─── Medicine-wise Report ───
@Composable
fun MedicineWiseReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadMedicineWise() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold(
        title = "Medicine-wise Report",
        subtitle = "Consumption and compliance per medicine",
        onRefresh = viewModel::loadMedicineWise
    ) {
        if (state.medicineWiseReports.isEmpty()) {
            item { EmptyState() }
        } else {
            items(state.medicineWiseReports) { report ->
                val adherenceColor = when {
                    report.adherencePercentage >= 80 -> TakenGreen
                    report.adherencePercentage >= 50 -> SkippedOrange
                    else -> MissedRed
                }
                StockUsageCard(
                    title = report.medicineName,
                    body = "Taken: ${report.takenCount}  •  Missed: ${report.missedCount}  •  Skipped: ${report.skippedCount}\n" +
                            "Consumed: ${report.totalConsumed} ${report.unit ?: ""}  •  Adherence: ${report.adherencePercentage}%",
                    severityColor = adherenceColor
                )
            }
        }
    }
}

// ─── Stock Usage Report ───
@Composable
fun StockUsageReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadStockUsage() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold(
        title = "Stock Usage Report",
        subtitle = "Medicine consumption and refill history",
        onRefresh = viewModel::loadStockUsage
    ) {
        if (state.stockUsageReports.isEmpty()) {
            item { EmptyState() }
        } else {
            items(state.stockUsageReports) { report ->
                val remainingColor = when {
                    report.remainingDays == null -> MissedRed
                    report.remainingDays!! <= 7 -> MissedRed
                    report.remainingDays!! <= 14 -> SkippedOrange
                    else -> TakenGreen
                }
                StockUsageCard(
                    title = report.medicineName,
                    body = "Consumed: ${report.totalConsumed} ${report.unit}  •  Refilled: ${report.totalRefilled} ${report.unit}\n" +
                            "Current Stock: ${report.currentStock} ${report.unit}  •  Remaining: ${report.remainingDays?.let { "${it.toInt()} days" } ?: "N/A"}",
                    severityColor = remainingColor
                )
            }
        }
    }
}

// ─── Low Stock Report ───
@Composable
fun LowStockReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadLowStock() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold(
        title = "Low Stock Report",
        subtitle = "Medicines below threshold levels",
        onRefresh = viewModel::loadLowStock
    ) {
        if (state.lowStockReports.isEmpty()) {
            item { EmptyState() }
        } else {
            items(state.lowStockReports) { report ->
                val severityColor = when (report.severity) {
                    "OUT_OF_STOCK" -> MissedRed
                    "CRITICAL" -> MissedRed
                    else -> SkippedOrange
                }
                StockUsageCard(
                    title = report.medicineName,
                    body = "${report.currentQuantity} ${report.unit} remaining  •  Threshold: ${report.lowStockThreshold} ${report.unit}\n" +
                            "Status: ${report.severity.replace("_", " ")}",
                    severityColor = severityColor
                )
            }
        }
    }
}

// ─── Expired Medicine Report ───
@Composable
fun ExpiredMedicineReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadExpired() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold(
        title = "Expired Medicine Report",
        subtitle = "Expired or near-expiry medicines",
        onRefresh = viewModel::loadExpired
    ) {
        if (state.expiredMedicineReports.isEmpty()) {
            item { EmptyState() }
        } else {
            items(state.expiredMedicineReports) { report ->
                StockUsageCard(
                    title = report.medicineName,
                    body = "Expired: ${report.daysExpired} days ago\n" +
                            "${report.currentQuantity} ${report.unit} remaining",
                    severityColor = MissedRed
                )
            }
        }
    }
}

// ─── Shared Scaffold ───
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportScaffold(
    title: String,
    subtitle: String,
    onRefresh: () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Analytics,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(title)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item { ReportFilterBar(title = "", onRefresh = onRefresh) }
            content()
        }
    }
}

@Composable
private fun EmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No report data available",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Data will appear here once there\nare records to analyze.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
