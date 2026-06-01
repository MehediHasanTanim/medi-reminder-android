package com.example.medireminder.features.stock.data.repository

import com.example.medireminder.core.database.dao.MedicineStockDao
import com.example.medireminder.core.database.dao.StockTransactionDao
import com.example.medireminder.core.database.dao.MedicineStockWithDetails
import com.example.medireminder.features.stock.data.mapper.toDomain
import com.example.medireminder.features.stock.data.mapper.toEntity
import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.model.StockTransaction
import com.example.medireminder.features.stock.domain.model.StockTransactionType
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MedicineStockRepositoryImpl @Inject constructor(
    private val medicineStockDao: MedicineStockDao,
    private val stockTransactionDao: StockTransactionDao
) : MedicineStockRepository {

    override fun observeAllStock(): Flow<List<MedicineStock>> {
        return medicineStockDao.observeAllStockWithDetails().map { details ->
            details.map { it.toDomainModel() }
        }
    }

    override fun observeAllStocks(): Flow<List<MedicineStock>> = observeAllStock()

    override fun observeLowStockMedicines(): Flow<List<MedicineStock>> {
        return medicineStockDao.observeLowStockWithDetails().map { details ->
            details.map { it.toDomainModel() }
        }
    }

    override fun observeExpiredMedicines(): Flow<List<MedicineStock>> {
        return medicineStockDao.observeExpiredMedicines(System.currentTimeMillis()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeExpiringMedicines(): Flow<List<MedicineStock>> = observeExpiringSoonMedicines(7)

    override fun observeExpiringSoonMedicines(thresholdDays: Int): Flow<List<MedicineStock>> {
        val currentTime = System.currentTimeMillis()
        val thresholdTime = currentTime + (thresholdDays * 24 * 60 * 60 * 1000L)
        return medicineStockDao.observeExpiringSoonMedicines(currentTime, thresholdTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getStockByMedicineId(medicineId: String): MedicineStock? {
        return medicineStockDao.getStockWithDetailsByMedicineId(medicineId)?.toDomainModel()
    }

    override suspend fun updateStockQuantity(medicineId: String, quantity: Double) {
        medicineStockDao.updateQuantity(medicineId, quantity, System.currentTimeMillis())
    }

    override suspend fun addStock(stock: MedicineStock) {
        saveStock(stock)
    }

    override suspend fun saveStock(stock: MedicineStock) {
        medicineStockDao.insert(stock.toEntity())
    }

    override suspend fun updateStock(stock: MedicineStock) {
        medicineStockDao.update(stock.toEntity())
    }

    override suspend fun refillStock(medicineId: String, quantity: Double) {
        val stock = getStockByMedicineId(medicineId) ?: throw IllegalStateException("Stock record not found")
        val previousQuantity = stock.currentQuantity
        val newQuantity = previousQuantity + quantity
        val now = System.currentTimeMillis()
        updateStock(stock.copy(currentQuantity = newQuantity, lastRefillDate = now, updatedAt = now))
        addTransaction(
            StockTransaction(
                id = UUID.randomUUID().toString(),
                medicineId = medicineId,
                transactionType = StockTransactionType.REFILL,
                quantity = quantity,
                previousQuantity = previousQuantity,
                newQuantity = newQuantity,
                reason = "Repository refill",
                createdAt = now
            )
        )
    }

    override suspend fun reduceStock(medicineId: String, quantity: Double) {
        val stock = getStockByMedicineId(medicineId) ?: throw IllegalStateException("Stock record not found")
        if (quantity <= 0) throw IllegalArgumentException("Quantity must be greater than 0")
        if (stock.currentQuantity < quantity) throw IllegalStateException("Insufficient stock")
        val previousQuantity = stock.currentQuantity
        val newQuantity = previousQuantity - quantity
        val now = System.currentTimeMillis()
        updateStockQuantity(medicineId, newQuantity)
        addTransaction(
            StockTransaction(
                id = UUID.randomUUID().toString(),
                medicineId = medicineId,
                transactionType = StockTransactionType.DOSE_TAKEN,
                quantity = quantity,
                previousQuantity = previousQuantity,
                newQuantity = newQuantity,
                reason = "Repository reduction",
                createdAt = now
            )
        )
    }

    override suspend fun adjustStock(medicineId: String, quantity: Double) {
        val stock = getStockByMedicineId(medicineId) ?: throw IllegalStateException("Stock record not found")
        val previousQuantity = stock.currentQuantity
        val newQuantity = previousQuantity + quantity
        if (newQuantity < 0) throw IllegalArgumentException("Adjustment would make stock negative")
        val now = System.currentTimeMillis()
        updateStockQuantity(medicineId, newQuantity)
        addTransaction(
            StockTransaction(
                id = UUID.randomUUID().toString(),
                medicineId = medicineId,
                transactionType = StockTransactionType.MANUAL_ADJUSTMENT,
                quantity = quantity,
                previousQuantity = previousQuantity,
                newQuantity = newQuantity,
                reason = "Repository adjustment",
                createdAt = now
            )
        )
    }

    override fun observeTransactionsByMedicine(medicineId: String): Flow<List<StockTransaction>> {
        return stockTransactionDao.observeTransactionsByMedicine(medicineId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTransaction(transaction: StockTransaction) {
        stockTransactionDao.insert(transaction.toEntity())
    }

    override suspend fun getTotalConsumedByMedicine(medicineId: String): Double? {
        return stockTransactionDao.getTotalConsumedByMedicine(medicineId)
    }

    private fun MedicineStockWithDetails.toDomainModel(): MedicineStock {
        return toDomain(
            medicineName = medicineName,
            medicineType = medicineType
        )
    }

    private fun MedicineStockWithDetails.toDomain(
        medicineName: String? = null,
        medicineType: String? = null
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
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
