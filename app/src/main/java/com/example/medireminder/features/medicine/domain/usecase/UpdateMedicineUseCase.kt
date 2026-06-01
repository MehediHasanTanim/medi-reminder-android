package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import javax.inject.Inject

class UpdateMedicineUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(
        id: String,
        name: String,
        genericName: String?,
        medicineType: String,
        strength: String?,
        unit: String,
        manufacturer: String?,
        notes: String?,
        isActive: Boolean
    ): Result<Unit> {
        if (name.isBlank()) {
            return Result.failure(Exception("Medicine name is required"))
        }
        if (medicineType.isBlank()) {
            return Result.failure(Exception("Medicine type is required"))
        }
        if (unit.isBlank()) {
            return Result.failure(Exception("Unit is required"))
        }

        val existingMedicine = repository.getMedicineById(id)
            ?: return Result.failure(Exception("Medicine not found"))

        val updatedMedicine = existingMedicine.copy(
            name = name.trim(),
            genericName = genericName?.trim(),
            medicineType = medicineType,
            strength = strength?.trim(),
            unit = unit,
            manufacturer = manufacturer?.trim(),
            notes = notes?.trim(),
            isActive = isActive,
            updatedAt = System.currentTimeMillis()
        )
        
        repository.updateMedicine(updatedMedicine)
        return Result.success(Unit)
    }
}
