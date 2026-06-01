package com.example.medireminder.features.stock.domain.repository

import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.model.StockTransaction
import kotlinx.coroutines.flow.Flow

interface MedicineStockRepository {
    fun observeAllStock(): Flow<List<MedicineStock>>
    fun observeAllStocks(): Flow<List<MedicineStock>>
    fun observeLowStockMedicines(): Flow<List<MedicineStock>>
    fun observeExpiredMedicines(): Flow<List<MedicineStock>>
    fun observeExpiringMedicines(): Flow<List<MedicineStock>>
    fun observeExpiringSoonMedicines(thresholdDays: Int): Flow<List<MedicineStock>>
    suspend fun getStockByMedicineId(medicineId: String): MedicineStock?
    suspend fun addStock(stock: MedicineStock)
    suspend fun updateStockQuantity(medicineId: String, quantity: Double)
    suspend fun saveStock(stock: MedicineStock)
    suspend fun updateStock(stock: MedicineStock)
    suspend fun refillStock(medicineId: String, quantity: Double)
    suspend fun reduceStock(medicineId: String, quantity: Double)
    suspend fun adjustStock(medicineId: String, quantity: Double)
    
    // Stock Transactions
    fun observeTransactionsByMedicine(medicineId: String): Flow<List<StockTransaction>>
    suspend fun addTransaction(transaction: StockTransaction)
    suspend fun getTotalConsumedByMedicine(medicineId: String): Double?
}
