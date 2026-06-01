package com.example.medireminder.features.settings.domain.repository

import com.example.medireminder.features.settings.domain.model.AppSettings
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun updateThemeMode(themeMode: AppThemeMode)
    suspend fun updateTimeFormat(timeFormat: TimeFormat)
    suspend fun updateDefaultSnoozeDuration(minutes: Int)
    suspend fun updateDefaultVibration(enabled: Boolean)
    suspend fun updateNotificationSound(uri: String?)
    suspend fun updateStockReductionMode(mode: StockReductionMode)
    suspend fun updateDefaultLowStockThreshold(threshold: Double)
    suspend fun updateExpiryAlertDays(days: Int)
    suspend fun updateFullScreenAlarmEnabled(enabled: Boolean)
}
