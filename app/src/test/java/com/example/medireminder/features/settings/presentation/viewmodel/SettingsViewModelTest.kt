package com.example.medireminder.features.settings.presentation.viewmodel

import com.example.medireminder.features.settings.domain.model.AppSettings
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import com.example.medireminder.features.settings.domain.repository.SettingsRepository
import com.example.medireminder.features.settings.domain.usecase.GetSettingsUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateDefaultSnoozeDurationUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateDefaultVibrationUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateExpiryAlertDaysUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateFullScreenAlarmUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateLowStockThresholdUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateNotificationSoundUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateStockReductionModeUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateThemeUseCase
import com.example.medireminder.features.settings.domain.usecase.UpdateTimeFormatUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var repository: FakeSettingsRepository
    private lateinit var viewModel: SettingsViewModel
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeSettingsRepository()
        viewModel = SettingsViewModel(
            getSettingsUseCase = GetSettingsUseCase(repository),
            updateThemeUseCase = UpdateThemeUseCase(repository),
            updateTimeFormatUseCase = UpdateTimeFormatUseCase(repository),
            updateDefaultSnoozeDurationUseCase = UpdateDefaultSnoozeDurationUseCase(repository),
            updateDefaultVibrationUseCase = UpdateDefaultVibrationUseCase(repository),
            updateNotificationSoundUseCase = UpdateNotificationSoundUseCase(repository),
            updateStockReductionModeUseCase = UpdateStockReductionModeUseCase(repository),
            updateLowStockThresholdUseCase = UpdateLowStockThresholdUseCase(repository),
            updateExpiryAlertDaysUseCase = UpdateExpiryAlertDaysUseCase(repository),
            updateFullScreenAlarmUseCase = UpdateFullScreenAlarmUseCase(repository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `settings load successfully`() {
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppSettings(), viewModel.uiState.value.settings)
    }

    @Test
    fun `theme update updates ui state`() = runTest {
        viewModel.updateTheme(AppThemeMode.DARK)

        assertEquals(AppThemeMode.DARK, viewModel.uiState.value.settings.themeMode)
        assertEquals("Settings updated", viewModel.uiState.value.successMessage)
    }

    @Test
    fun `invalid threshold shows error`() = runTest {
        viewModel.updateLowStockThreshold(-1.0)

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(5.0, viewModel.uiState.value.settings.defaultLowStockThreshold, 0.0)
    }

    @Test
    fun `full screen alarm toggle updates setting`() = runTest {
        viewModel.updateFullScreenAlarm(false)

        assertFalse(viewModel.uiState.value.settings.fullScreenAlarmEnabled)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `stock reduction mode updates setting`() = runTest {
        viewModel.updateStockReductionMode(StockReductionMode.AUTO_DAILY_REDUCTION)

        assertEquals(StockReductionMode.AUTO_DAILY_REDUCTION, viewModel.uiState.value.settings.stockReductionMode)
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val settings = MutableStateFlow(AppSettings())

        override fun observeSettings(): Flow<AppSettings> = settings
        override suspend fun updateThemeMode(themeMode: AppThemeMode) {
            settings.value = settings.value.copy(themeMode = themeMode)
        }
        override suspend fun updateTimeFormat(timeFormat: TimeFormat) {
            settings.value = settings.value.copy(timeFormat = timeFormat)
        }
        override suspend fun updateDefaultSnoozeDuration(minutes: Int) {
            settings.value = settings.value.copy(defaultSnoozeDurationMinutes = minutes)
        }
        override suspend fun updateDefaultVibration(enabled: Boolean) {
            settings.value = settings.value.copy(defaultVibrationEnabled = enabled)
        }
        override suspend fun updateNotificationSound(uri: String?) {
            settings.value = settings.value.copy(notificationSoundUri = uri)
        }
        override suspend fun updateStockReductionMode(mode: StockReductionMode) {
            settings.value = settings.value.copy(stockReductionMode = mode)
        }
        override suspend fun updateDefaultLowStockThreshold(threshold: Double) {
            settings.value = settings.value.copy(defaultLowStockThreshold = threshold)
        }
        override suspend fun updateExpiryAlertDays(days: Int) {
            settings.value = settings.value.copy(expiryAlertDays = days)
        }
        override suspend fun updateFullScreenAlarmEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(fullScreenAlarmEnabled = enabled)
        }
    }
}
