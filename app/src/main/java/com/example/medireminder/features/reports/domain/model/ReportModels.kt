package com.example.medireminder.features.reports.domain.model

data class DailyReminderReport(
    val date: Long,
    val totalReminders: Int,
    val takenCount: Int,
    val missedCount: Int,
    val skippedCount: Int,
    val snoozedCount: Int,
    val dismissedCount: Int,
    val adherencePercentage: Double
)

data class ComplianceReport(
    val periodLabel: String,
    val startDate: Long,
    val endDate: Long,
    val totalReminders: Int,
    val takenCount: Int,
    val missedCount: Int,
    val skippedCount: Int,
    val adherencePercentage: Double
)

data class MemberWiseReport(
    val memberId: String,
    val memberName: String,
    val totalReminders: Int,
    val takenCount: Int,
    val missedCount: Int,
    val skippedCount: Int,
    val activeMedicineCount: Int,
    val adherencePercentage: Double
)

data class MedicineWiseReport(
    val medicineId: String,
    val medicineName: String,
    val totalReminders: Int,
    val takenCount: Int,
    val missedCount: Int,
    val skippedCount: Int,
    val totalConsumed: Double,
    val unit: String?,
    val adherencePercentage: Double
)

data class StockUsageReport(
    val medicineId: String,
    val medicineName: String,
    val totalConsumed: Double,
    val totalRefilled: Double,
    val currentStock: Double,
    val unit: String,
    val remainingDays: Double?
)

data class LowStockReport(
    val medicineId: String,
    val medicineName: String,
    val currentQuantity: Double,
    val lowStockThreshold: Double,
    val unit: String,
    val severity: String
)

data class ExpiredMedicineReport(
    val medicineId: String,
    val medicineName: String,
    val expiryDate: Long,
    val currentQuantity: Double,
    val unit: String,
    val daysExpired: Int
)
