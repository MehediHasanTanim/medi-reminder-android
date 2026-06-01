package com.example.medireminder.features.membermedicine.presentation.state

data class MemberMedicineFormState(
    val familyMemberId: String = "",
    val medicineId: String = "",
    val dosageQuantity: String = "",
    val frequencyPerDay: String = "",
    val dailyTotalQuantity: Double = 0.0,
    val instructions: String = "",
    val startDate: Long? = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val autoReduceStock: Boolean = true,
    val validationErrors: Map<String, String> = emptyMap()
)
