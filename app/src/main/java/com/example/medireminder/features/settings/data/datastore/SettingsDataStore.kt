package com.example.medireminder.features.settings.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.medireminder.features.settings.domain.model.AppSettings
import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val settingsFlow: Flow<AppSettings> = context.appSettingsDataStore.data.map { preferences ->
        AppSettings(
            themeMode = preferences[THEME_MODE].toEnum(AppThemeMode.SYSTEM),
            timeFormat = preferences[TIME_FORMAT].toEnum(TimeFormat.HOUR_12),
            defaultSnoozeDurationMinutes = preferences[DEFAULT_SNOOZE_DURATION] ?: 10,
            defaultVibrationEnabled = preferences[DEFAULT_VIBRATION_ENABLED] ?: true,
            notificationSoundUri = preferences[NOTIFICATION_SOUND_URI],
            stockReductionMode = preferences[STOCK_REDUCTION_MODE].toEnum(StockReductionMode.REDUCE_ON_TAKEN),
            defaultLowStockThreshold = preferences[DEFAULT_LOW_STOCK_THRESHOLD] ?: 5.0,
            expiryAlertDays = preferences[EXPIRY_ALERT_DAYS] ?: 7,
            fullScreenAlarmEnabled = preferences[FULL_SCREEN_ALARM_ENABLED] ?: true
        )
    }

    suspend fun updateThemeMode(themeMode: AppThemeMode) = edit { it[THEME_MODE] = themeMode.name }
    suspend fun updateTimeFormat(timeFormat: TimeFormat) = edit { it[TIME_FORMAT] = timeFormat.name }
    suspend fun updateDefaultSnoozeDuration(minutes: Int) = edit { it[DEFAULT_SNOOZE_DURATION] = minutes }
    suspend fun updateDefaultVibration(enabled: Boolean) = edit { it[DEFAULT_VIBRATION_ENABLED] = enabled }
    suspend fun updateNotificationSound(uri: String?) = edit {
        if (uri == null) it.remove(NOTIFICATION_SOUND_URI) else it[NOTIFICATION_SOUND_URI] = uri
    }
    suspend fun updateStockReductionMode(mode: StockReductionMode) = edit { it[STOCK_REDUCTION_MODE] = mode.name }
    suspend fun updateDefaultLowStockThreshold(threshold: Double) = edit { it[DEFAULT_LOW_STOCK_THRESHOLD] = threshold }
    suspend fun updateExpiryAlertDays(days: Int) = edit { it[EXPIRY_ALERT_DAYS] = days }
    suspend fun updateFullScreenAlarmEnabled(enabled: Boolean) = edit { it[FULL_SCREEN_ALARM_ENABLED] = enabled }

    private suspend fun edit(block: (MutablePreferences) -> Unit) {
        context.appSettingsDataStore.edit(block)
    }

    private inline fun <reified T : Enum<T>> String?.toEnum(default: T): T {
        return runCatching { if (this == null) default else enumValueOf<T>(this) }.getOrDefault(default)
    }

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val TIME_FORMAT = stringPreferencesKey("time_format")
        val DEFAULT_SNOOZE_DURATION = intPreferencesKey("default_snooze_duration")
        val DEFAULT_VIBRATION_ENABLED = booleanPreferencesKey("default_vibration_enabled")
        val NOTIFICATION_SOUND_URI = stringPreferencesKey("notification_sound_uri")
        val STOCK_REDUCTION_MODE = stringPreferencesKey("stock_reduction_mode")
        val DEFAULT_LOW_STOCK_THRESHOLD = doublePreferencesKey("default_low_stock_threshold")
        val EXPIRY_ALERT_DAYS = intPreferencesKey("expiry_alert_days")
        val FULL_SCREEN_ALARM_ENABLED = booleanPreferencesKey("full_screen_alarm_enabled")
    }
}
