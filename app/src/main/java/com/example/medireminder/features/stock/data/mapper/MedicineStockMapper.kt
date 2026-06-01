package com.example.medireminder.features.stock.data.mapper

import com.example.medireminder.core.database.entities.MedicineStockEntity
import com.example.medireminder.features.stock.domain.model.MedicineStock

fun MedicineStockEntity.toDomain(
    medicineName: String? = null,
    medicineType: String? = null,
    dailyConsumption: Double = 0.0,
    estimatedRemainingDays: Double? = null,
    isLowStock: Boolean = false,
    isExpired: Boolean = false
): MedicineStock {
    return MedicineStock(
        id = id,
        medicineId = medicineId,
        medicineName = medicineName,
        medicineType = medicineType,
        currentQuantity = currentQuantity,
        unit = unit,
        lowStockThreshold = lowStockThreshold,
        expiryDate = expiryDate,
        autoReduceEnabled = autoReduceEnabled,
        lastRefillDate = lastRefillDate,
        dailyConsumption = dailyConsumption,
        estimatedRemainingDays = estimatedRemainingDays,
        isLowStock = isLowStock,
        isExpired = isExpired,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun MedicineStock.toEntity(): MedicineStockEntity {
    return MedicineStockEntity(
        id = id,
        medicineId = medicineId,
        currentQuantity = currentQuantity,
        unit = unit,
        lowStockThreshold = lowStockThreshold,
        expiryDate = expiryDate,
        autoReduceEnabled = autoReduceEnabled,
        lastRefillDate = lastRefillDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
