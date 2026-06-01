package com.example.medireminder.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey
    val id: String,
    val memberMedicineId: String,
    val reminderTime: String,
    val repeatType: String,
    val repeatDays: String?,
    val startDate: Long,
    val endDate: Long?,
    val alarmTone: String?,
    val vibrationEnabled: Boolean,
    val notificationEnabled: Boolean,
    val snoozeEnabled: Boolean,
    val snoozeDuration: Int,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
