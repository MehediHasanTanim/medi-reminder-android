package com.example.medireminder.features.reports

import androidx.compose.runtime.Composable
import com.example.medireminder.features.reports.presentation.screen.ReportsHomeScreen

@Composable
fun ReportsScreen(onReportClick: (String) -> Unit = {}) {
    ReportsHomeScreen(onReportClick = onReportClick)
}
