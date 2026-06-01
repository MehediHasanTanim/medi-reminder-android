package com.example.medireminder.features.reminder.domain.usecase

import com.example.medireminder.core.alarm.ReminderScheduler
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class CancelReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(reminderId: String): Result<Unit> {
        return try {
            repository.deactivateReminder(reminderId)
            scheduler.cancel(reminderId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
