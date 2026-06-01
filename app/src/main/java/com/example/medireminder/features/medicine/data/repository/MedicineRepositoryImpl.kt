package com.example.medireminder.features.medicine.data.repository

import com.example.medireminder.core.database.dao.MedicineDao
import com.example.medireminder.features.medicine.data.mapper.toDomain
import com.example.medireminder.features.medicine.data.mapper.toEntity
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MedicineRepositoryImpl @Inject constructor(
    private val medicineDao: MedicineDao
) : MedicineRepository {

    override fun observeAllMedicines(): Flow<List<Medicine>> {
        return medicineDao.observeAllMedicines().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeActiveMedicines(): Flow<List<Medicine>> {
        return medicineDao.observeActiveMedicines().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMedicineById(id: String): Medicine? {
        return medicineDao.getMedicineById(id)?.toDomain()
    }

    override suspend fun addMedicine(medicine: Medicine) {
        medicineDao.insertMedicine(medicine.toEntity())
    }

    override suspend fun updateMedicine(medicine: Medicine) {
        medicineDao.updateMedicine(medicine.toEntity())
    }

    override suspend fun deleteMedicine(medicine: Medicine) {
        // Preference is soft delete, but repo should support delete.
        // Implementation of soft delete is usually in the use case or by calling update.
        medicineDao.deleteMedicine(medicine.toEntity())
    }

    override suspend fun toggleActiveStatus(id: String, isActive: Boolean) {
        val medicine = medicineDao.getMedicineById(id)
        medicine?.let {
            medicineDao.updateMedicine(it.copy(isActive = isActive, updatedAt = System.currentTimeMillis()))
        }
    }

    override fun searchMedicines(query: String): Flow<List<Medicine>> {
        return medicineDao.searchMedicines(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
