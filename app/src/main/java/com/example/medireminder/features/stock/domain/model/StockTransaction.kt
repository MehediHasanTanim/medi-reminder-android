package com.example.medireminder.features.stock.domain.model

data class StockTransaction(
    val id: String,
    val medicineId: String,
    val medicineName: String? = null,
    val transactionType: StockTransactionType,
    val quantity: Double,
    val previousQuantity: Double,
    val newQuantity: Double,
    val reason: String? = null,
    val createdAt: Long
)
