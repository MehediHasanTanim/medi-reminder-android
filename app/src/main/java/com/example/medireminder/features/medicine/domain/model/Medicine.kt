package com.example.medireminder.features.medicine.domain.model

data class Medicine(
    val id: String,
    val name: String,
    val genericName: String?,
    val medicineType: String,
    val strength: String?,
    val unit: String,
    val manufacturer: String?,
    val notes: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
