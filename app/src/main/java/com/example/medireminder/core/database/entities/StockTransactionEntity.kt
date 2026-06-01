package com.example.medireminder.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_transactions")
data class StockTransactionEntity(
    @PrimaryKey
    val id: String,
    val medicineId: String,
    val transactionType: String,
    val quantity: Double,
    val previousQuantity: Double,
    val newQuantity: Double,
    val reason: String?,
    val createdAt: Long
)
