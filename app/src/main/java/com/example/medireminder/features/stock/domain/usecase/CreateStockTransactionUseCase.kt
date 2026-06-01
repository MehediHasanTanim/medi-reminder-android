package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.features.stock.domain.model.StockTransaction
import com.example.medireminder.features.stock.domain.model.StockTransactionType
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import java.util.UUID
import javax.inject.Inject

class CreateStockTransactionUseCase @Inject constructor(
    private val repository: MedicineStockRepository
) {
    suspend operator fun invoke(
        medicineId: String,
        type: StockTransactionType,
        quantity: Double,
        previousQuantity: Double,
        newQuantity: Double,
        reason: String? = null
    ): Result<Unit> {
        val transaction = StockTransaction(
            id = UUID.randomUUID().toString(),
            medicineId = medicineId,
            transactionType = type,
            quantity = quantity,
            previousQuantity = previousQuantity,
            newQuantity = newQuantity,
            reason = reason,
            createdAt = System.currentTimeMillis()
        )
        return try {
            repository.addTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
