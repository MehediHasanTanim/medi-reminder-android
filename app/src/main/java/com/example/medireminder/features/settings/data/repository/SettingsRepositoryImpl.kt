package com.example.medireminder.features.settings.data.repository

import com.example.medireminder.features.settings.data.datastore.SettingsDataStore
import com.example.medireminder.features.settings.domain.model.AppSettings
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import com.example.medireminder.features.settings.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {
    override fun observeSettings(): Flow<AppSettings> = dataStore.settingsFlow
    override suspend fun updateThemeMode(themeMode: AppThemeMode) = dataStore.updateThemeMode(themeMode)
    override suspend fun updateTimeFormat(timeFormat: TimeFormat) = dataStore.updateTimeFormat(timeFormat)
    override suspend fun updateDefaultSnoozeDuration(minutes: Int) = dataStore.updateDefaultSnoozeDuration(minutes)
    override suspend fun updateDefaultVibration(enabled: Boolean) = dataStore.updateDefaultVibration(enabled)
    override suspend fun updateNotificationSound(uri: String?) = dataStore.updateNotificationSound(uri)
    override suspend fun updateStockReductionMode(mode: StockReductionMode) = dataStore.updateStockReductionMode(mode)
    override suspend fun updateDefaultLowStockThreshold(threshold: Double) = dataStore.updateDefaultLowStockThreshold(threshold)
    override suspend fun updateExpiryAlertDays(days: Int) = dataStore.updateExpiryAlertDays(days)
    override suspend fun updateFullScreenAlarmEnabled(enabled: Boolean) = dataStore.updateFullScreenAlarmEnabled(enabled)
}
