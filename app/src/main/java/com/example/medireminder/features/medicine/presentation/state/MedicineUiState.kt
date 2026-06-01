package com.example.medireminder.features.medicine.presentation.state

import com.example.medireminder.features.medicine.domain.model.Medicine

data class MedicineUiState(
    val isLoading: Boolean = false,
    val medicines: List<Medicine> = emptyList(),
    val selectedMedicine: Medicine? = null,
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)
