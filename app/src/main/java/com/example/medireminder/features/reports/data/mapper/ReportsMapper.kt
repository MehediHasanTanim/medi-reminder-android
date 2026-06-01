package com.example.medireminder.features.reports.data.mapper

import com.example.medireminder.features.reports.data.dto.ExpiredMedicineReportDto
import com.example.medireminder.features.reports.data.dto.LowStockReportDto
import com.example.medireminder.features.reports.data.dto.MedicineWiseReportDto
import com.example.medireminder.features.reports.data.dto.MemberWiseReportDto
import com.example.medireminder.features.reports.data.dto.StatusCountDto
import com.example.medireminder.features.reports.data.dto.StockUsageReportDto
import com.example.medireminder.features.reports.domain.model.ComplianceReport
import com.example.medireminder.features.reports.domain.model.DailyReminderReport
import com.example.medireminder.features.reports.domain.model.ExpiredMedicineReport
import com.example.medireminder.features.reports.domain.model.LowStockReport
import com.example.medireminder.features.reports.domain.model.MedicineWiseReport
import com.example.medireminder.features.reports.domain.model.MemberWiseReport
import com.example.medireminder.features.reports.domain.model.StockUsageReport
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun List<StatusCountDto>.toDailyReminderReport(date: Long): DailyReminderReport {
    val counts = statusCounts()
    return DailyReminderReport(
        date = date,
        totalReminders = counts.total,
        takenCount = counts.taken,
        missedCount = counts.missed,
        skippedCount = counts.skipped,
        snoozedCount = counts.snoozed,
        dismissedCount = counts.dismissed,
        adherencePercentage = adherence(counts.taken, counts.actionable)
    )
}

fun List<StatusCountDto>.toComplianceReport(label: String, start: Long, end: Long): ComplianceReport {
    val counts = statusCounts()
    return ComplianceReport(
        periodLabel = label,
        startDate = start,
        endDate = end,
        totalReminders = counts.total,
        takenCount = counts.taken,
        missedCount = counts.missed,
        skippedCount = counts.skipped,
        adherencePercentage = adherence(counts.taken, counts.actionable)
    )
}

fun List<MemberWiseReportDto>.toMemberWiseReports(): List<MemberWiseReport> {
    return groupBy { it.memberId }.map { (memberId, rows) ->
        val counts = rows.statusCounts { it.actionTaken to it.count }
        MemberWiseReport(
            memberId = memberId,
            memberName = rows.first().memberName,
            totalReminders = counts.total,
            takenCount = counts.taken,
            missedCount = counts.missed,
            skippedCount = counts.skipped,
            activeMedicineCount = rows.maxOfOrNull { it.activeMedicineCount } ?: 0,
            adherencePercentage = adherence(counts.taken, counts.actionable)
        )
    }
}

fun List<MedicineWiseReportDto>.toMedicineWiseReports(): List<MedicineWiseReport> {
    return groupBy { it.medicineId }.map { (medicineId, rows) ->
        val counts = rows.statusCounts { it.actionTaken to it.count }
        MedicineWiseReport(
            medicineId = medicineId,
            medicineName = rows.first().medicineName,
            totalReminders = counts.total,
            takenCount = counts.taken,
            missedCount = counts.missed,
            skippedCount = counts.skipped,
            totalConsumed = rows.firstNotNullOfOrNull { it.totalConsumed } ?: 0.0,
            unit = rows.firstOrNull()?.unit,
            adherencePercentage = adherence(counts.taken, counts.actionable)
        )
    }
}

fun StockUsageReportDto.toDomain(): StockUsageReport {
    val consumption = dailyConsumption ?: 0.0
    return StockUsageReport(
        medicineId = medicineId,
        medicineName = medicineName,
        totalConsumed = totalConsumed ?: 0.0,
        totalRefilled = totalRefilled ?: 0.0,
        currentStock = currentStock ?: 0.0,
        unit = unit ?: "",
        remainingDays = if (consumption <= 0.0) null else roundOneDecimal((currentStock ?: 0.0) / consumption)
    )
}

fun LowStockReportDto.toDomain(): LowStockReport {
    val severity = when {
        currentQuantity <= 0.0 -> "OUT_OF_STOCK"
        currentQuantity <= lowStockThreshold / 2.0 -> "CRITICAL"
        else -> "LOW"
    }
    return LowStockReport(medicineId, medicineName, currentQuantity, lowStockThreshold, unit, severity)
}

fun ExpiredMedicineReportDto.toDomain(currentTime: Long): ExpiredMedicineReport {
    return ExpiredMedicineReport(
        medicineId = medicineId,
        medicineName = medicineName,
        expiryDate = expiryDate,
        currentQuantity = currentQuantity,
        unit = unit,
        daysExpired = TimeUnit.MILLISECONDS.toDays(currentTime - expiryDate).toInt().coerceAtLeast(0)
    )
}

fun adherence(taken: Int, actionable: Int): Double {
    if (actionable == 0) return 0.0
    return roundOneDecimal(taken.toDouble() / actionable * 100.0)
}

private data class Counts(
    val total: Int,
    val taken: Int,
    val missed: Int,
    val skipped: Int,
    val snoozed: Int,
    val dismissed: Int
) {
    val actionable: Int = taken + missed + skipped + dismissed
}

private fun List<StatusCountDto>.statusCounts(): Counts {
    return Counts(
        total = sumOf { it.count },
        taken = countFor("TAKEN"),
        missed = countFor("MISSED"),
        skipped = countFor("SKIPPED"),
        snoozed = countFor("SNOOZED"),
        dismissed = countFor("DISMISSED")
    )
}

private fun <T> List<T>.statusCounts(selector: (T) -> Pair<String?, Int>): Counts {
    fun status(name: String) = sumOf { item ->
        val (status, count) = selector(item)
        if (status == name) count else 0
    }
    return Counts(
        total = sumOf { selector(it).second },
        taken = status("TAKEN"),
        missed = status("MISSED"),
        skipped = status("SKIPPED"),
        snoozed = status("SNOOZED"),
        dismissed = status("DISMISSED")
    )
}

private fun List<StatusCountDto>.countFor(status: String): Int {
    return firstOrNull { it.actionTaken == status }?.count ?: 0
}

private fun roundOneDecimal(value: Double): Double = (value * 10).roundToInt() / 10.0
