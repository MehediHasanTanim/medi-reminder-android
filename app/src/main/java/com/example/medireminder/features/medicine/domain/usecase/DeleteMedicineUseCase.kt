package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import javax.inject.Inject

class DeleteMedicineUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(medicine: Medicine) {
        // Preference for soft delete as per requirement
        repository.toggleActiveStatus(medicine.id, false)
    }
}
