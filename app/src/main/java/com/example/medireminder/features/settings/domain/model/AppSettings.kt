package com.example.medireminder.features.settings.domain.model

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val timeFormat: TimeFormat = TimeFormat.HOUR_12,
    val defaultSnoozeDurationMinutes: Int = 10,
    val defaultVibrationEnabled: Boolean = true,
    val notificationSoundUri: String? = null,
    val stockReductionMode: StockReductionMode = StockReductionMode.REDUCE_ON_TAKEN,
    val defaultLowStockThreshold: Double = 5.0,
    val expiryAlertDays: Int = 7,
    val fullScreenAlarmEnabled: Boolean = true
)

enum class AppThemeMode { SYSTEM, LIGHT, DARK }
enum class TimeFormat { HOUR_12, HOUR_24 }
enum class StockReductionMode { REDUCE_ON_TAKEN, AUTO_DAILY_REDUCTION }
