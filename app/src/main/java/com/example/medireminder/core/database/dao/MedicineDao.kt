package com.example.medireminder.core.database.dao

import androidx.room.*
import com.example.medireminder.core.database.entities.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity)

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: String): MedicineEntity?

    @Query("SELECT * FROM medicines")
    fun observeAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE isActive = 1")
    fun observeActiveMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE name LIKE '%' || :query || '%' OR genericName LIKE '%' || :query || '%'")
    fun searchMedicines(query: String): Flow<List<MedicineEntity>>
}
