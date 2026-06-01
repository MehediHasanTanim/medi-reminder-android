package com.example.medireminder.features.reports.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.medireminder.features.reports.presentation.components.ReportSummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsHomeScreen(onReportClick: (String) -> Unit) {
    val reports = listOf(
        "reports/daily" to "Daily Reminder Report",
        "reports/weekly" to "Weekly Compliance Report",
        "reports/monthly" to "Monthly Compliance Report",
        "reports/member-wise" to "Member-wise Report",
        "reports/medicine-wise" to "Medicine-wise Report",
        "reports/stock-usage" to "Stock Usage Report",
        "reports/low-stock" to "Low Stock Report",
        "reports/expired" to "Expired Medicine Report"
    )
    Scaffold(topBar = { TopAppBar(title = { Text("Reports") }) }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reports.size) { index ->
                val (route, title) = reports[index]
                ReportSummaryCard(title = title, subtitle = "View analytics", onClick = { onReportClick(route) })
            }
        }
    }
}
