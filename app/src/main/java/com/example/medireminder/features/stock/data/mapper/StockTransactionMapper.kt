package com.example.medireminder.features.stock.data.mapper

import com.example.medireminder.core.database.entities.StockTransactionEntity
import com.example.medireminder.features.stock.domain.model.StockTransaction
import com.example.medireminder.features.stock.domain.model.StockTransactionType

fun StockTransactionEntity.toDomain(medicineName: String? = null): StockTransaction {
    return StockTransaction(
        id = id,
        medicineId = medicineId,
        medicineName = medicineName,
        transactionType = StockTransactionType.valueOf(transactionType),
        quantity = quantity,
        previousQuantity = previousQuantity,
        newQuantity = newQuantity,
        reason = reason,
        createdAt = createdAt
    )
}

fun StockTransaction.toEntity(): StockTransactionEntity {
    return StockTransactionEntity(
        id = id,
        medicineId = medicineId,
        transactionType = transactionType.name,
        quantity = quantity,
        previousQuantity = previousQuantity,
        newQuantity = newQuantity,
        reason = reason,
        createdAt = createdAt
    )
}
