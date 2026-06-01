package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import javax.inject.Inject

class GetMedicineByIdUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(id: String): Medicine? {
        return repository.getMedicineById(id)
    }
}
