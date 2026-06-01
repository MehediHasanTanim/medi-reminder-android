package com.example.medireminder.core.database.dao

import androidx.room.*
import com.example.medireminder.core.database.entities.StockTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: StockTransactionEntity)

    suspend fun insertTransaction(transaction: StockTransactionEntity) = insert(transaction)

    @Update
    suspend fun update(transaction: StockTransactionEntity)

    @Delete
    suspend fun delete(transaction: StockTransactionEntity)

    @Query("SELECT * FROM stock_transactions WHERE id = :id")
    suspend fun getById(id: String): StockTransactionEntity?

    @Query("SELECT * FROM stock_transactions")
    fun getAll(): Flow<List<StockTransactionEntity>>

    fun observeAllTransactions(): Flow<List<StockTransactionEntity>> = getAll()

    @Query("SELECT * FROM stock_transactions WHERE medicineId = :medicineId")
    fun observeTransactionsByMedicine(medicineId: String): Flow<List<StockTransactionEntity>>

    @Query("SELECT SUM(quantity) FROM stock_transactions WHERE medicineId = :medicineId AND transactionType = 'DOSE_TAKEN'")
    suspend fun getTotalConsumedByMedicine(medicineId: String): Double?
}
