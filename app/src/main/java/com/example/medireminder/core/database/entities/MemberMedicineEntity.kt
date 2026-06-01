package com.example.medireminder.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_medicines")
data class MemberMedicineEntity(
    @PrimaryKey
    val id: String,
    val familyMemberId: String,
    val medicineId: String,
    val dosageQuantity: Double,
    val frequencyPerDay: Int,
    val dailyTotalQuantity: Double,
    val instructions: String?,
    val startDate: Long,
    val endDate: Long?,
    val isActive: Boolean,
    val autoReduceStock: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
