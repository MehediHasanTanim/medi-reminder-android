package com.example.medireminder.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_logs")
data class ReminderLogEntity(
    @PrimaryKey
    val id: String,
    val reminderId: String,
    val memberId: String,
    val medicineId: String,
    val scheduledTime: Long,
    val actionTaken: String,
    val actionTime: Long?,
    val notes: String?,
    val createdAt: Long
)
