package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpiringMedicinesUseCase @Inject constructor(
    private val repository: MedicineStockRepository
) {
    operator fun invoke(thresholdDays: Int = 7): Flow<List<MedicineStock>> {
        return repository.observeExpiringSoonMedicines(thresholdDays)
    }
}
