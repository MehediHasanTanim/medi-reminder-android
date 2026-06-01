package com.example.medireminder.features.membermedicine.domain.model

data class MemberMedicine(
    val id: String,
    val familyMemberId: String,
    val medicineId: String,
    val familyMemberName: String? = null,
    val medicineName: String? = null,
    val medicineUnit: String? = null,
    val medicineType: String? = null,
    val dosageQuantity: Double,
    val frequencyPerDay: Int,
    val dailyTotalQuantity: Double,
    val instructions: String? = null,
    val startDate: Long,
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val autoReduceStock: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
