package com.example.medireminder.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_stock")
data class MedicineStockEntity(
    @PrimaryKey
    val id: String,
    val medicineId: String,
    val currentQuantity: Double,
    val unit: String,
    val lowStockThreshold: Double,
    val expiryDate: Long?,
    val autoReduceEnabled: Boolean,
    val lastRefillDate: Long?,
    val createdAt: Long,
    val updatedAt: Long
)
