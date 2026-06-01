package com.example.medireminder.features.reports.data.repository

import com.example.medireminder.core.database.dao.ReportsDao
import com.example.medireminder.features.reports.data.mapper.toComplianceReport
import com.example.medireminder.features.reports.data.mapper.toDailyReminderReport
import com.example.medireminder.features.reports.data.mapper.toDomain
import com.example.medireminder.features.reports.data.mapper.toMedicineWiseReports
import com.example.medireminder.features.reports.data.mapper.toMemberWiseReports
import com.example.medireminder.features.reports.domain.model.ComplianceReport
import com.example.medireminder.features.reports.domain.model.DailyReminderReport
import com.example.medireminder.features.reports.domain.model.ExpiredMedicineReport
import com.example.medireminder.features.reports.domain.model.LowStockReport
import com.example.medireminder.features.reports.domain.model.MedicineWiseReport
import com.example.medireminder.features.reports.domain.model.MemberWiseReport
import com.example.medireminder.features.reports.domain.model.StockUsageReport
import com.example.medireminder.features.reports.domain.repository.ReportsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
    private val reportsDao: ReportsDao
) : ReportsRepository {
    override fun getDailyReminderReport(startTime: Long, endTime: Long): Flow<DailyReminderReport> {
        return reportsDao.getReminderStatusCounts(startTime, endTime)
            .map { it.toDailyReminderReport(startTime) }
    }

    override fun getWeeklyComplianceReport(startTime: Long, endTime: Long): Flow<ComplianceReport> {
        return reportsDao.getReminderStatusCounts(startTime, endTime)
            .map { it.toComplianceReport("Weekly Compliance", startTime, endTime) }
    }

    override fun getMonthlyComplianceReport(startTime: Long, endTime: Long): Flow<ComplianceReport> {
        return reportsDao.getReminderStatusCounts(startTime, endTime)
            .map { it.toComplianceReport("Monthly Compliance", startTime, endTime) }
    }

    override fun getMemberWiseReport(startTime: Long, endTime: Long): Flow<List<MemberWiseReport>> {
        return reportsDao.getMemberWiseReport(startTime, endTime).map { it.toMemberWiseReports() }
    }

    override fun getMedicineWiseReport(startTime: Long, endTime: Long): Flow<List<MedicineWiseReport>> {
        return reportsDao.getMedicineWiseReport(startTime, endTime).map { it.toMedicineWiseReports() }
    }

    override fun getStockUsageReport(startTime: Long, endTime: Long): Flow<List<StockUsageReport>> {
        return reportsDao.getStockUsageReport(startTime, endTime).map { rows -> rows.map { it.toDomain() } }
    }

    override fun getLowStockReport(): Flow<List<LowStockReport>> {
        return reportsDao.getLowStockReport().map { rows -> rows.map { it.toDomain() } }
    }

    override fun getExpiredMedicineReport(currentTime: Long): Flow<List<ExpiredMedicineReport>> {
        return reportsDao.getExpiredMedicineReport(currentTime).map { rows -> rows.map { it.toDomain(currentTime) } }
    }
}
