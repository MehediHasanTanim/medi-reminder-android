package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.core.notification.NotificationHelper
import com.example.medireminder.features.stock.domain.model.StockTransaction
import com.example.medireminder.features.stock.domain.model.StockTransactionType
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import java.util.UUID
import javax.inject.Inject

class AdjustStockUseCase @Inject constructor(
    private val repository: MedicineStockRepository,
    private val notificationHelper: NotificationHelper
) {
    suspend operator fun invoke(
        medicineId: String,
        adjustmentQuantity: Double, // can be positive or negative
        reason: String? = null
    ): Result<Unit> {
        return try {
            val currentStock = repository.getStockByMedicineId(medicineId)
                ?: return Result.failure(Exception("Stock record not found"))

            val previousQuantity = currentStock.currentQuantity
            val newQuantity = previousQuantity + adjustmentQuantity
            if (newQuantity < 0) {
                return Result.failure(Exception("Adjustment would make stock negative"))
            }
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
                    transactionType = StockTransactionType.MANUAL_ADJUSTMENT,
                    quantity = adjustmentQuantity,
                    previousQuantity = previousQuantity,
                    newQuantity = newQuantity,
                    reason = reason ?: "Manual adjustment",
                    createdAt = now
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
