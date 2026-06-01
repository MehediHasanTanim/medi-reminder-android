package com.example.medireminder.features.stock.presentation.state

import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.model.StockTransaction

data class MedicineStockUiState(
    val isLoading: Boolean = false,
    val stocks: List<MedicineStock> = emptyList(),
    val lowStockMedicines: List<MedicineStock> = emptyList(),
    val expiringMedicines: List<MedicineStock> = emptyList(),
    val selectedStock: MedicineStock? = null,
    val transactions: List<StockTransaction> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)
