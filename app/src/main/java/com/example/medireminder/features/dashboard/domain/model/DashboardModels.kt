package com.example.medireminder.features.dashboard.domain.model

data class DashboardOverview(
    val todayReminderCount: Int,
    val upcomingReminderCount: Int,
    val missedReminderCount: Int,
    val lowStockCount: Int,
    val expiringMedicineCount: Int,
    val familyMemberCount: Int
)

data class TodayReminderItem(
    val reminderId: String,
    val memberName: String,
    val medicineName: String,
    val dosage: String,
    val reminderTime: Long,
    val status: String,
    val instructions: String?
)

data class UpcomingReminderItem(
    val reminderId: String,
    val memberName: String,
    val medicineName: String,
    val reminderTime: Long,
    val dosage: String?
)

data class MissedReminderItem(
    val reminderLogId: String,
    val memberName: String,
    val medicineName: String,
    val scheduledTime: Long,
    val notes: String?
)

data class LowStockItem(
    val medicineId: String,
    val medicineName: String,
    val currentQuantity: Double,
    val unit: String,
    val lowStockThreshold: Double
)

data class ExpiringMedicineItem(
    val medicineId: String,
    val medicineName: String,
    val expiryDate: Long,
    val daysRemaining: Int
)

data class FamilyOverviewItem(
    val memberId: String,
    val fullName: String,
    val relationship: String?,
    val activeMedicineCount: Int,
    val todayReminderCount: Int,
    val missedReminderCount: Int
)

data class MedicineDashboard(
    val medicineId: String,
    val medicineName: String,
    val currentStock: Double,
    val unit: String,
    val dailyConsumption: Double,
    val remainingDays: Double?,
    val assignedMembers: List<String>,
    val reminderCompliance: Double,
    val lowStockThreshold: Double,
    val expiryDate: Long?
)
