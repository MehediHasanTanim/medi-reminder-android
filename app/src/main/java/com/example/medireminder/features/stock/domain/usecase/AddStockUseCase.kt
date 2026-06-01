package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.model.StockTransaction
import com.example.medireminder.features.stock.domain.model.StockTransactionType
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import java.util.UUID
import javax.inject.Inject

class AddStockUseCase @Inject constructor(
    private val repository: MedicineStockRepository,
    private val medicineRepository: MedicineRepository
) {
    suspend operator fun invoke(
        medicineId: String,
        initialQuantity: Double,
        unit: String,
        lowStockThreshold: Double,
        expiryDate: Long? = null,
        autoReduceEnabled: Boolean = true
    ): Result<Unit> {
        if (medicineId.isBlank()) return Result.failure(Exception("Medicine is required"))
        if (initialQuantity < 0) return Result.failure(Exception("Initial quantity cannot be negative"))
        if (lowStockThreshold < 0) return Result.failure(Exception("Threshold cannot be negative"))
        if (medicineRepository.getMedicineById(medicineId) == null) {
            return Result.failure(Exception("Medicine not found"))
        }

        val now = System.currentTimeMillis()
        val stock = MedicineStock(
            id = UUID.randomUUID().toString(),
            medicineId = medicineId,
            currentQuantity = initialQuantity,
            unit = unit,
            lowStockThreshold = lowStockThreshold,
            expiryDate = expiryDate,
            autoReduceEnabled = autoReduceEnabled,
            lastRefillDate = now,
            createdAt = now,
            updatedAt = now
        )

        return try {
            repository.saveStock(stock)
            
            // Create INITIAL_STOCK transaction
            repository.addTransaction(
                StockTransaction(
                    id = UUID.randomUUID().toString(),
                    medicineId = medicineId,
                    transactionType = StockTransactionType.INITIAL_STOCK,
                    quantity = initialQuantity,
                    previousQuantity = 0.0,
                    newQuantity = initialQuantity,
                    reason = "Initial stock setup",
                    createdAt = now
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
