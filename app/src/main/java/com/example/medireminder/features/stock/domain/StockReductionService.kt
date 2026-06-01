package com.example.medireminder.features.stock.domain

interface StockReductionService {
    suspend fun reduceStockForTakenReminder(reminderId: String): Result<Unit>
}
