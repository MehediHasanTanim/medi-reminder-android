package com.example.medireminder.core.database.dao

import androidx.room.*
import com.example.medireminder.core.database.entities.MedicineStockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineStockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: MedicineStockEntity)

    suspend fun insertStock(stock: MedicineStockEntity) = insert(stock)

    @Update
    suspend fun update(stock: MedicineStockEntity)

    suspend fun updateStock(stock: MedicineStockEntity) = update(stock)

    @Delete
    suspend fun delete(stock: MedicineStockEntity)

    @Query("SELECT * FROM medicine_stock WHERE id = :id")
    suspend fun getById(id: String): MedicineStockEntity?

    @Query("SELECT * FROM medicine_stock WHERE medicineId = :medicineId")
    suspend fun getStockByMedicineId(medicineId: String): MedicineStockEntity?

    @Query("SELECT * FROM medicine_stock")
    fun getAll(): Flow<List<MedicineStockEntity>>

    fun observeAllStocks(): Flow<List<MedicineStockEntity>> = getAll()

    @Transaction
    @Query("""
        SELECT s.*, m.name as medicineName, m.medicineType as medicineType
        FROM medicine_stock s
        JOIN medicines m ON s.medicineId = m.id
    """)
    fun observeAllStockWithDetails(): Flow<List<MedicineStockWithDetails>>

    @Transaction
    @Query("""
        SELECT s.*, m.name as medicineName, m.medicineType as medicineType
        FROM medicine_stock s
        JOIN medicines m ON s.medicineId = m.id
        WHERE s.currentQuantity <= s.lowStockThreshold
    """)
    fun observeLowStockWithDetails(): Flow<List<MedicineStockWithDetails>>

    @Transaction
    @Query("""
        SELECT s.*, m.name as medicineName, m.medicineType as medicineType
        FROM medicine_stock s
        JOIN medicines m ON s.medicineId = m.id
        WHERE s.medicineId = :medicineId
    """)
    suspend fun getStockWithDetailsByMedicineId(medicineId: String): MedicineStockWithDetails?

    @Query("SELECT * FROM medicine_stock WHERE currentQuantity <= lowStockThreshold")
    fun observeLowStockMedicines(): Flow<List<MedicineStockEntity>>

    @Query("SELECT * FROM medicine_stock WHERE expiryDate IS NOT NULL AND expiryDate <= :currentTime")
    fun observeExpiredMedicines(currentTime: Long): Flow<List<MedicineStockEntity>>

    @Query("SELECT * FROM medicine_stock WHERE expiryDate IS NOT NULL AND expiryDate > :currentTime AND expiryDate <= :thresholdTime")
    fun observeExpiringSoonMedicines(currentTime: Long, thresholdTime: Long): Flow<List<MedicineStockEntity>>

    fun observeExpiringMedicines(currentTime: Long, thresholdTime: Long): Flow<List<MedicineStockEntity>> {
        return observeExpiringSoonMedicines(currentTime, thresholdTime)
    }

    @Query("UPDATE medicine_stock SET currentQuantity = :quantity, updatedAt = :updatedAt WHERE medicineId = :medicineId")
    suspend fun updateQuantity(medicineId: String, quantity: Double, updatedAt: Long)
}

data class MedicineStockWithDetails(
    val id: String,
    val medicineId: String,
    val currentQuantity: Double,
    val unit: String,
    val lowStockThreshold: Double,
    val expiryDate: Long?,
    val autoReduceEnabled: Boolean,
    val lastRefillDate: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    // Joined fields
    val medicineName: String,
    val medicineType: String
)
