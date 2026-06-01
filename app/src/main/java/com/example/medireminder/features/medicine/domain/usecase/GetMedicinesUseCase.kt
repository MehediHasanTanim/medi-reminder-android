package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMedicinesUseCase @Inject constructor(
    private val repository: MedicineRepository
) {
    operator fun invoke(onlyActive: Boolean = false): Flow<List<Medicine>> {
        return if (onlyActive) {
            repository.observeActiveMedicines()
        } else {
            repository.observeAllMedicines()
        }
    }
}
