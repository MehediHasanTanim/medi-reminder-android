package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.core.notification.NotificationHelper
import com.example.medireminder.features.stock.domain.model.StockTransaction
import com.example.medireminder.features.stock.domain.model.StockTransactionType
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import java.util.UUID
import javax.inject.Inject

class ReduceStockUseCase @Inject constructor(
    private val repository: MedicineStockRepository,
    private val notificationHelper: NotificationHelper
) {
    suspend operator fun invoke(
        medicineId: String,
        quantity: Double,
        reason: String? = null,
        transactionType: StockTransactionType = StockTransactionType.DOSE_TAKEN
    ): Result<Unit> {
        if (quantity <= 0) return Result.failure(Exception("Quantity must be greater than 0"))

        return try {
            val currentStock = repository.getStockByMedicineId(medicineId)
                ?: return Result.failure(Exception("Stock record not found for medicine"))

            if (currentStock.currentQuantity < quantity) {
                return Result.failure(Exception("Insufficient stock for ${currentStock.medicineName ?: "this medicine"}"))
            }

            val previousQuantity = currentStock.currentQuantity
            val newQuantity = previousQuantity - quantity
            val now = System.currentTimeMillis()

            repository.updateStockQuantity(medicineId, newQuantity)
            if (previousQuantity > currentStock.lowStockThreshold && newQuantity <= currentStock.lowStockThreshold) {
                notificationHelper.showLowStockNotification(
                    medicineId = medicineId,
                    medicineName = currentStock.medicineName ?: "Medicine",
                    quantity = newQuantity,
                    unit = currentStock.unit
                )
            }
            
            repository.addTransaction(
                StockTransaction(
                    id = UUID.randomUUID().toString(),
                    medicineId = medicineId,
                    transactionType = transactionType,
                    quantity = quantity,
                    previousQuantity = previousQuantity,
                    newQuantity = newQuantity,
                    reason = reason ?: "Dose taken",
                    createdAt = now
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
