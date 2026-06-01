package com.example.medireminder.features.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import com.example.medireminder.features.settings.domain.usecase.*
import com.example.medireminder.features.settings.presentation.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateTimeFormatUseCase: UpdateTimeFormatUseCase,
    private val updateDefaultSnoozeDurationUseCase: UpdateDefaultSnoozeDurationUseCase,
    private val updateDefaultVibrationUseCase: UpdateDefaultVibrationUseCase,
    private val updateNotificationSoundUseCase: UpdateNotificationSoundUseCase,
    private val updateStockReductionModeUseCase: UpdateStockReductionModeUseCase,
    private val updateLowStockThresholdUseCase: UpdateLowStockThresholdUseCase,
    private val updateExpiryAlertDaysUseCase: UpdateExpiryAlertDaysUseCase,
    private val updateFullScreenAlarmUseCase: UpdateFullScreenAlarmUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase()
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { settings -> _uiState.update { it.copy(isLoading = false, settings = settings) } }
        }
    }

    fun updateTheme(themeMode: AppThemeMode) = update { updateThemeUseCase(themeMode) }
    fun updateTimeFormat(timeFormat: TimeFormat) = update { updateTimeFormatUseCase(timeFormat) }
    fun updateDefaultSnoozeDuration(minutes: Int) = update { updateDefaultSnoozeDurationUseCase(minutes) }
    fun updateDefaultVibration(enabled: Boolean) = update { updateDefaultVibrationUseCase(enabled) }
    fun updateNotificationSound(uri: String?) = update { updateNotificationSoundUseCase(uri) }
    fun updateStockReductionMode(mode: StockReductionMode) = update { updateStockReductionModeUseCase(mode) }
    fun updateLowStockThreshold(threshold: Double) = update { updateLowStockThresholdUseCase(threshold) }
    fun updateExpiryAlertDays(days: Int) = update { updateExpiryAlertDaysUseCase(days) }
    fun updateFullScreenAlarm(enabled: Boolean) = update { updateFullScreenAlarmUseCase(enabled) }
    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, successMessage = null) }

    private fun update(block: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            block()
                .onSuccess { _uiState.update { it.copy(successMessage = "Settings updated", errorMessage = null) } }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message, successMessage = null) } }
        }
    }
}
