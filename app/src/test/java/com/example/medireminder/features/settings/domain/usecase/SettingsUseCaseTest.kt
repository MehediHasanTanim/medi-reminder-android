package com.example.medireminder.features.settings.domain.usecase

import com.example.medireminder.features.settings.domain.model.AppSettings
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import com.example.medireminder.features.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsUseCaseTest {

    private lateinit var repository: FakeSettingsRepository

    @Before
    fun setUp() {
        repository = FakeSettingsRepository()
    }

    @Test
    fun `default settings match requirements`() {
        assertEquals(AppSettings(), repository.current)
    }

    @Test
    fun `update theme saves selected theme`() = runTest {
        UpdateThemeUseCase(repository)(AppThemeMode.DARK)

        assertEquals(AppThemeMode.DARK, repository.current.themeMode)
    }

    @Test
    fun `update time format saves selected format`() = runTest {
        UpdateTimeFormatUseCase(repository)(TimeFormat.HOUR_24)

        assertEquals(TimeFormat.HOUR_24, repository.current.timeFormat)
    }

    @Test
    fun `update snooze duration validates positive duration`() = runTest {
        val result = UpdateDefaultSnoozeDurationUseCase(repository)(0)

        assertFalse(result.isSuccess)
        assertEquals(10, repository.current.defaultSnoozeDurationMinutes)
    }

    @Test
    fun `update vibration saves default vibration`() = runTest {
        UpdateDefaultVibrationUseCase(repository)(false)

        assertFalse(repository.current.defaultVibrationEnabled)
    }

    @Test
    fun `update stock reduction mode saves selected mode`() = runTest {
        UpdateStockReductionModeUseCase(repository)(StockReductionMode.AUTO_DAILY_REDUCTION)

        assertEquals(StockReductionMode.AUTO_DAILY_REDUCTION, repository.current.stockReductionMode)
    }

    @Test
    fun `update low stock threshold rejects negative values`() = runTest {
        val result = UpdateLowStockThresholdUseCase(repository)(-1.0)

        assertFalse(result.isSuccess)
        assertEquals(5.0, repository.current.defaultLowStockThreshold, 0.0)
    }

    @Test
    fun `update expiry alert days rejects negative values`() = runTest {
        val result = UpdateExpiryAlertDaysUseCase(repository)(-1)

        assertFalse(result.isSuccess)
        assertEquals(7, repository.current.expiryAlertDays)
    }

    @Test
    fun `update full screen alarm saves toggle`() = runTest {
        val result = UpdateFullScreenAlarmUseCase(repository)(false)

        assertTrue(result.isSuccess)
        assertFalse(repository.current.fullScreenAlarmEnabled)
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val settings = MutableStateFlow(AppSettings())
        val current: AppSettings get() = settings.value

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
