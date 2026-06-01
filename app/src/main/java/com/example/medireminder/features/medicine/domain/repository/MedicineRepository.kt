package com.example.medireminder.features.medicine.domain.repository

import com.example.medireminder.features.medicine.domain.model.Medicine
import kotlinx.coroutines.flow.Flow

interface MedicineRepository {
    fun observeAllMedicines(): Flow<List<Medicine>>
    fun observeActiveMedicines(): Flow<List<Medicine>>
    suspend fun getMedicineById(id: String): Medicine?
    suspend fun addMedicine(medicine: Medicine)
    suspend fun updateMedicine(medicine: Medicine)
    suspend fun deleteMedicine(medicine: Medicine)
    suspend fun toggleActiveStatus(id: String, isActive: Boolean)
    fun searchMedicines(query: String): Flow<List<Medicine>>
}
