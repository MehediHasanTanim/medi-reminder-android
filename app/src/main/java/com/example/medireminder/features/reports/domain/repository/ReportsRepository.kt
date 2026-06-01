package com.example.medireminder.features.reports.domain.repository

import com.example.medireminder.features.reports.domain.model.ComplianceReport
import com.example.medireminder.features.reports.domain.model.DailyReminderReport
import com.example.medireminder.features.reports.domain.model.ExpiredMedicineReport
import com.example.medireminder.features.reports.domain.model.LowStockReport
import com.example.medireminder.features.reports.domain.model.MedicineWiseReport
import com.example.medireminder.features.reports.domain.model.MemberWiseReport
import com.example.medireminder.features.reports.domain.model.StockUsageReport
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {
    fun getDailyReminderReport(startTime: Long, endTime: Long): Flow<DailyReminderReport>
    fun getWeeklyComplianceReport(startTime: Long, endTime: Long): Flow<ComplianceReport>
    fun getMonthlyComplianceReport(startTime: Long, endTime: Long): Flow<ComplianceReport>
    fun getMemberWiseReport(startTime: Long, endTime: Long): Flow<List<MemberWiseReport>>
    fun getMedicineWiseReport(startTime: Long, endTime: Long): Flow<List<MedicineWiseReport>>
    fun getStockUsageReport(startTime: Long, endTime: Long): Flow<List<StockUsageReport>>
    fun getLowStockReport(): Flow<List<LowStockReport>>
    fun getExpiredMedicineReport(currentTime: Long): Flow<List<ExpiredMedicineReport>>
}
