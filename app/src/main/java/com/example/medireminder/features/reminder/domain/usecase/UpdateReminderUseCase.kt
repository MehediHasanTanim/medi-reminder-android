package com.example.medireminder.features.reminder.domain.usecase

import com.example.medireminder.core.alarm.ReminderScheduler
import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(reminder: Reminder): Result<Unit> {
        if (reminder.memberMedicineId.isBlank()) return Result.failure(Exception("Member medicine assignment is required"))
        if (reminder.reminderTime.isBlank()) return Result.failure(Exception("Reminder time is required"))
        if (reminder.repeatType == ReminderRepeatType.SPECIFIC_WEEKDAYS && reminder.repeatDays.isEmpty()) {
            return Result.failure(Exception("At least one weekday is required for specific days"))
        }
        if (reminder.endDate != null && reminder.endDate < reminder.startDate) {
            return Result.failure(Exception("End date cannot be before start date"))
        }
        if (reminder.snoozeEnabled && reminder.snoozeDuration <= 0) {
            return Result.failure(Exception("Snooze duration must be greater than 0"))
        }

        val updatedReminder = reminder.copy(updatedAt = System.currentTimeMillis())

        return try {
            repository.updateReminder(updatedReminder)
            scheduler.cancel(reminder.id)
            if (updatedReminder.isActive) {
                scheduler.schedule(updatedReminder)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
