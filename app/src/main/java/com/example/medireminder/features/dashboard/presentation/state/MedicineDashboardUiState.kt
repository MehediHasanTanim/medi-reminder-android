package com.example.medireminder.features.dashboard.presentation.state

import com.example.medireminder.features.dashboard.domain.model.MedicineDashboard

data class MedicineDashboardUiState(
    val isLoading: Boolean = false,
    val dashboard: MedicineDashboard? = null,
    val errorMessage: String? = null
)
