package com.example.medireminder.features.settings.domain.usecase

import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat
import com.example.medireminder.features.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repository: SettingsRepository) {
    operator fun invoke() = repository.observeSettings()
}
class UpdateThemeUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(themeMode: AppThemeMode): Result<Unit> = runCatching { repository.updateThemeMode(themeMode) }
}
class UpdateTimeFormatUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(timeFormat: TimeFormat): Result<Unit> = runCatching { repository.updateTimeFormat(timeFormat) }
}
class UpdateDefaultSnoozeDurationUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(minutes: Int): Result<Unit> {
        if (minutes <= 0) return Result.failure(Exception("Snooze duration must be greater than 0"))
        return runCatching { repository.updateDefaultSnoozeDuration(minutes) }
    }
}
class UpdateDefaultVibrationUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> = runCatching { repository.updateDefaultVibration(enabled) }
}
class UpdateNotificationSoundUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(uri: String?): Result<Unit> = runCatching { repository.updateNotificationSound(uri) }
}
class UpdateStockReductionModeUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(mode: StockReductionMode): Result<Unit> = runCatching { repository.updateStockReductionMode(mode) }
}
class UpdateLowStockThresholdUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(threshold: Double): Result<Unit> {
        if (threshold < 0) return Result.failure(Exception("Low stock threshold must be 0 or greater"))
        return runCatching { repository.updateDefaultLowStockThreshold(threshold) }
    }
}
class UpdateExpiryAlertDaysUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(days: Int): Result<Unit> {
        if (days < 0) return Result.failure(Exception("Expiry alert days must be 0 or greater"))
        return runCatching { repository.updateExpiryAlertDays(days) }
    }
}
class UpdateFullScreenAlarmUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> = runCatching { repository.updateFullScreenAlarmEnabled(enabled) }
}
