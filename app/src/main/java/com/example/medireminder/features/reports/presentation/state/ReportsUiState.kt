package com.example.medireminder.features.reports.presentation.state

import com.example.medireminder.features.reports.domain.model.ComplianceReport
import com.example.medireminder.features.reports.domain.model.DailyReminderReport
import com.example.medireminder.features.reports.domain.model.ExpiredMedicineReport
import com.example.medireminder.features.reports.domain.model.LowStockReport
import com.example.medireminder.features.reports.domain.model.MedicineWiseReport
import com.example.medireminder.features.reports.domain.model.MemberWiseReport
import com.example.medireminder.features.reports.domain.model.StockUsageReport

data class ReportsUiState(
    val isLoading: Boolean = false,
    val dailyReport: DailyReminderReport? = null,
    val weeklyReport: ComplianceReport? = null,
    val monthlyReport: ComplianceReport? = null,
    val memberWiseReports: List<MemberWiseReport> = emptyList(),
    val medicineWiseReports: List<MedicineWiseReport> = emptyList(),
    val stockUsageReports: List<StockUsageReport> = emptyList(),
    val lowStockReports: List<LowStockReport> = emptyList(),
    val expiredMedicineReports: List<ExpiredMedicineReport> = emptyList(),
    val errorMessage: String? = null
)

data class ReportFilterState(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val selectedMemberId: String? = null,
    val selectedMedicineId: String? = null,
    val selectedReportType: String = "daily"
)
