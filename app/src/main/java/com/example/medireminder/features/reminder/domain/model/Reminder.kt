package com.example.medireminder.features.reminder.domain.model

data class Reminder(
    val id: String,
    val memberMedicineId: String,
    val reminderTime: String,
    val repeatType: ReminderRepeatType,
    val repeatDays: List<Int>,
    val startDate: Long,
    val endDate: Long?,
    val alarmTone: String?,
    val vibrationEnabled: Boolean,
    val notificationEnabled: Boolean,
    val snoozeEnabled: Boolean,
    val snoozeDuration: Int,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    // Joined data for convenience and logging
    val familyMemberId: String? = null,
    val familyMemberName: String? = null,
    val medicineId: String? = null,
    val medicineName: String? = null,
    val dosageQuantity: Double? = null,
    val medicineUnit: String? = null,
    val instructions: String? = null,
    val autoReduceStock: Boolean = false
)
