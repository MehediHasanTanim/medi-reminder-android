package com.example.medireminder.features.reports.domain.usecase

import com.example.medireminder.features.reports.data.dto.ExpiredMedicineReportDto
import com.example.medireminder.features.reports.data.dto.LowStockReportDto
import com.example.medireminder.features.reports.data.dto.StatusCountDto
import com.example.medireminder.features.reports.data.dto.StockUsageReportDto
import com.example.medireminder.features.reports.data.mapper.adherence
import com.example.medireminder.features.reports.data.mapper.toDailyReminderReport
import com.example.medireminder.features.reports.data.mapper.toDomain
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class ReportsMapperTest {
    @Test
    fun `daily report counts statuses and adherence`() {
        val report = listOf(
            StatusCountDto("TAKEN", 8),
            StatusCountDto("MISSED", 1),
            StatusCountDto("SKIPPED", 1),
            StatusCountDto("SNOOZED", 4)
        ).toDailyReminderReport(1L)

        assertEquals(14, report.totalReminders)
        assertEquals(8, report.takenCount)
        assertEquals(80.0, report.adherencePercentage, 0.0)
    }

    @Test
    fun `low stock severity is calculated`() {
        assertEquals("OUT_OF_STOCK", LowStockReportDto("m1", "A", 0.0, 10.0, "tab").toDomain().severity)
        assertEquals("CRITICAL", LowStockReportDto("m1", "A", 4.0, 10.0, "tab").toDomain().severity)
        assertEquals("LOW", LowStockReportDto("m1", "A", 8.0, 10.0, "tab").toDomain().severity)
    }

    @Test
    fun `stock usage remaining days is calculated`() {
        val report = StockUsageReportDto("m1", "A", 4.0, 10.0, 8.0, "tab", 2.0).toDomain()

        assertEquals(4.0, report.remainingDays!!, 0.0)
    }

    @Test
    fun `expired medicine days are calculated`() {
        val now = TimeUnit.DAYS.toMillis(10)
        val report = ExpiredMedicineReportDto("m1", "A", TimeUnit.DAYS.toMillis(7), 5.0, "tab").toDomain(now)

        assertEquals(3, report.daysExpired)
    }

    @Test
    fun `adherence handles divide by zero`() {
        assertEquals(0.0, adherence(0, 0), 0.0)
    }
}
