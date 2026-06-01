package com.example.medireminder.features.medicine.presentation.state

data class MedicineFormState(
    val name: String = "",
    val genericName: String = "",
    val medicineType: String = "",
    val strength: String = "",
    val unit: String = "",
    val manufacturer: String = "",
    val notes: String = "",
    val isActive: Boolean = true,
    val validationErrors: Map<String, String> = emptyMap()
)
