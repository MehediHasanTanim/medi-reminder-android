package com.example.medireminder.features.stock.domain.model

data class MedicineStock(
    val id: String,
    val medicineId: String,
    val medicineName: String? = null,
    val medicineType: String? = null,
    val currentQuantity: Double,
    val unit: String,
    val lowStockThreshold: Double,
    val expiryDate: Long?,
    val autoReduceEnabled: Boolean,
    val lastRefillDate: Long?,
    val dailyConsumption: Double = 0.0,
    val estimatedRemainingDays: Double? = null,
    val isLowStock: Boolean = false,
    val isExpired: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
