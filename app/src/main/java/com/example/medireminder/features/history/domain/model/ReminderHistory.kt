package com.example.medireminder.features.history.domain.model

data class ReminderHistory(
    val id: String,
    val reminderId: String,
    val memberId: String,
    val memberName: String? = null,
    val medicineId: String,
    val medicineName: String? = null,
    val medicineStrength: String? = null,
    val scheduledTime: Long,
    val status: ReminderHistoryStatus,
    val actionTime: Long?,
    val notes: String? = null,
    val createdAt: Long
)
