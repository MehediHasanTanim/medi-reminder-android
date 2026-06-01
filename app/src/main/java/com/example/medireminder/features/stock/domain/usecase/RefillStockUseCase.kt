package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.features.stock.domain.model.StockTransaction
import com.example.medireminder.features.stock.domain.model.StockTransactionType
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import java.util.UUID
import javax.inject.Inject

class RefillStockUseCase @Inject constructor(
    private val repository: MedicineStockRepository
) {
    suspend operator fun invoke(
        medicineId: String,
        refillQuantity: Double,
        reason: String? = null
    ): Result<Unit> {
        if (refillQuantity <= 0) return Result.failure(Exception("Refill quantity must be greater than 0"))

        return try {
            val currentStock = repository.getStockByMedicineId(medicineId) 
                ?: return Result.failure(Exception("Stock record not found for medicine"))

            val previousQuantity = currentStock.currentQuantity
            val newQuantity = previousQuantity + refillQuantity
            val now = System.currentTimeMillis()

            val updatedStock = currentStock.copy(
                currentQuantity = newQuantity,
                lastRefillDate = now,
                updatedAt = now
            )

            repository.updateStock(updatedStock)
            
            repository.addTransaction(
                StockTransaction(
                    id = UUID.randomUUID().toString(),
                    medicineId = medicineId,
                    transactionType = StockTransactionType.REFILL,
                    quantity = refillQuantity,
                    previousQuantity = previousQuantity,
                    newQuantity = newQuantity,
                    reason = reason ?: "Manual refill",
                    createdAt = now
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
