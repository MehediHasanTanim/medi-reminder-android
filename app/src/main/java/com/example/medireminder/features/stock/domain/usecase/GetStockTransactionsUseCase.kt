package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.features.stock.domain.model.StockTransaction
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStockTransactionsUseCase @Inject constructor(
    private val repository: MedicineStockRepository
) {
    operator fun invoke(medicineId: String): Flow<List<StockTransaction>> {
        return repository.observeTransactionsByMedicine(medicineId)
    }
}
