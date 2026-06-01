package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMedicineStockUseCase @Inject constructor(
    private val repository: MedicineStockRepository
) {
    operator fun invoke(): Flow<List<MedicineStock>> {
        return repository.observeAllStock()
    }
    
    suspend fun getById(medicineId: String): MedicineStock? {
        return repository.getStockByMedicineId(medicineId)
    }
}
