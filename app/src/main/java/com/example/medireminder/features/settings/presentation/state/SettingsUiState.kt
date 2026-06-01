package com.example.medireminder.features.settings.presentation.state

import com.example.medireminder.features.settings.domain.model.AppSettings

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: AppSettings = AppSettings(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)
