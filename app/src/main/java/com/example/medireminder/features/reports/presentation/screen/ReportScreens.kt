package com.example.medireminder.features.reports.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medireminder.features.reports.domain.model.ComplianceReport
import com.example.medireminder.features.reports.domain.model.DailyReminderReport
import com.example.medireminder.features.reports.presentation.components.ComplianceProgressCard
import com.example.medireminder.features.reports.presentation.components.ReportFilterBar
import com.example.medireminder.features.reports.presentation.components.ReportMetricCard
import com.example.medireminder.features.reports.presentation.components.StockUsageCard
import com.example.medireminder.features.reports.presentation.viewmodel.ReportsViewModel

@Composable
fun DailyReminderReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadDaily() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold("Daily Reminder Report", { viewModel.loadDaily() }) {
        state.dailyReport?.let { DailyReportContent(it) } ?: item { EmptyReport() }
    }
}

@Composable
fun WeeklyComplianceReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadWeekly() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold("Weekly Compliance Report", { viewModel.loadWeekly() }) {
        state.weeklyReport?.let { ComplianceContent(it) } ?: item { EmptyReport() }
    }
}

@Composable
fun MonthlyComplianceReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadMonthly() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold("Monthly Compliance Report", { viewModel.loadMonthly() }) {
        state.monthlyReport?.let { ComplianceContent(it) } ?: item { EmptyReport() }
    }
}

@Composable
fun MemberWiseReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadMemberWise() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold("Member-wise Report", viewModel::loadMemberWise) {
        if (state.memberWiseReports.isEmpty()) item { EmptyReport() }
        items(state.memberWiseReports) {
            StockUsageCard(it.memberName, "Taken ${it.takenCount} - Missed ${it.missedCount} - Skipped ${it.skippedCount} - Medicines ${it.activeMedicineCount} - ${it.adherencePercentage}%")
        }
    }
}

@Composable
fun MedicineWiseReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadMedicineWise() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold("Medicine-wise Report", viewModel::loadMedicineWise) {
        if (state.medicineWiseReports.isEmpty()) item { EmptyReport() }
        items(state.medicineWiseReports) {
            StockUsageCard(it.medicineName, "Taken ${it.takenCount} - Missed ${it.missedCount} - Consumed ${it.totalConsumed} ${it.unit ?: ""} - ${it.adherencePercentage}%")
        }
    }
}

@Composable
fun StockUsageReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadStockUsage() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold("Stock Usage Report", viewModel::loadStockUsage) {
        if (state.stockUsageReports.isEmpty()) item { EmptyReport() }
        items(state.stockUsageReports) {
            StockUsageCard(it.medicineName, "Consumed ${it.totalConsumed} - Refilled ${it.totalRefilled} - Current ${it.currentStock} ${it.unit} - Remaining ${it.remainingDays ?: "N/A"} days")
        }
    }
}

@Composable
fun LowStockReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadLowStock() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold("Low Stock Report", viewModel::loadLowStock) {
        if (state.lowStockReports.isEmpty()) item { EmptyReport() }
        items(state.lowStockReports) {
            StockUsageCard(it.medicineName, "${it.currentQuantity} ${it.unit} left - threshold ${it.lowStockThreshold} - ${it.severity}")
        }
    }
}

@Composable
fun ExpiredMedicineReportScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadExpired() }
    val state by viewModel.uiState.collectAsState()
    ReportScaffold("Expired Medicine Report", viewModel::loadExpired) {
        if (state.expiredMedicineReports.isEmpty()) item { EmptyReport() }
        items(state.expiredMedicineReports) {
            StockUsageCard(it.medicineName, "Expired ${it.daysExpired} days ago - ${it.currentQuantity} ${it.unit} remaining")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportScaffold(
    title: String,
    onRefresh: () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text(title) }) }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { ReportFilterBar(title, onRefresh) }
            content()
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.DailyReportContent(report: DailyReminderReport) {
    item { ComplianceProgressCard("Adherence", report.adherencePercentage) }
    item { ReportMetricCard("Total reminders", report.totalReminders.toString()) }
    item { ReportMetricCard("Taken", report.takenCount.toString()) }
    item { ReportMetricCard("Missed", report.missedCount.toString()) }
    item { ReportMetricCard("Skipped", report.skippedCount.toString()) }
    item { ReportMetricCard("Snoozed", report.snoozedCount.toString()) }
    item { ReportMetricCard("Dismissed", report.dismissedCount.toString()) }
}

private fun androidx.compose.foundation.lazy.LazyListScope.ComplianceContent(report: ComplianceReport) {
    item { ComplianceProgressCard(report.periodLabel, report.adherencePercentage) }
    item { ReportMetricCard("Total reminders", report.totalReminders.toString()) }
    item { ReportMetricCard("Taken", report.takenCount.toString()) }
    item { ReportMetricCard("Missed", report.missedCount.toString()) }
    item { ReportMetricCard("Skipped", report.skippedCount.toString()) }
}

@Composable
private fun EmptyReport() {
    Text("No report data found")
}
