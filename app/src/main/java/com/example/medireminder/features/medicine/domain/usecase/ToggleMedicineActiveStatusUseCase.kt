package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import javax.inject.Inject

class ToggleMedicineActiveStatusUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(id: String, isActive: Boolean) {
        repository.toggleActiveStatus(id, isActive)
    }
}
