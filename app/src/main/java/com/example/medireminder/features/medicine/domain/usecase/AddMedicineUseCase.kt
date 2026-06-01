package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import java.util.UUID
import javax.inject.Inject

class AddMedicineUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(
        name: String,
        genericName: String?,
        medicineType: String,
        strength: String?,
        unit: String,
        manufacturer: String?,
        notes: String?
    ): Result<Unit> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Medicine name is required"))
        }
        if (medicineType.isBlank()) {
            return Result.failure(IllegalArgumentException("Medicine type is required"))
        }
        if (unit.isBlank()) {
            return Result.failure(IllegalArgumentException("Unit is required"))
        }

        val currentTime = System.currentTimeMillis()
        val medicine = Medicine(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            genericName = genericName?.trim(),
            medicineType = medicineType,
            strength = strength?.trim(),
            unit = unit,
            manufacturer = manufacturer?.trim(),
            notes = notes?.trim(),
            isActive = true,
            createdAt = currentTime,
            updatedAt = currentTime
        )
        repository.addMedicine(medicine)
        return Result.success(Unit)
    }
}
