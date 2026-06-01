package com.example.medireminder.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey
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
