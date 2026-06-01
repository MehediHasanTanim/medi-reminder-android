package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLowStockMedicinesUseCase @Inject constructor(
    private val repository: MedicineStockRepository
) {
    operator fun invoke(): Flow<List<MedicineStock>> {
        return repository.observeLowStockMedicines()
    }
}
