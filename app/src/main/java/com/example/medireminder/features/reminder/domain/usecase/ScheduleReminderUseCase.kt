package com.example.medireminder.features.reminder.domain.usecase

import com.example.medireminder.core.alarm.ReminderScheduler
import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import java.util.UUID
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(
        memberMedicineId: String,
        reminderTime: String,
        repeatType: ReminderRepeatType,
        repeatDays: List<Int>,
        startDate: Long,
        endDate: Long? = null,
        alarmTone: String? = null,
        vibrationEnabled: Boolean = true,
        notificationEnabled: Boolean = true,
        snoozeEnabled: Boolean = true,
        snoozeDuration: Int = 5
    ): Result<Unit> {
        if (memberMedicineId.isBlank()) return Result.failure(Exception("Member medicine assignment is required"))
        if (reminderTime.isBlank()) return Result.failure(Exception("Reminder time is required"))
        if (repeatType == ReminderRepeatType.SPECIFIC_WEEKDAYS && repeatDays.isEmpty()) {
            return Result.failure(Exception("At least one weekday is required for specific days"))
        }
        if (endDate != null && endDate < startDate) {
            return Result.failure(Exception("End date cannot be before start date"))
        }
        if (snoozeEnabled && snoozeDuration <= 0) {
            return Result.failure(Exception("Snooze duration must be greater than 0"))
        }

        val now = System.currentTimeMillis()
        val reminder = Reminder(
            id = UUID.randomUUID().toString(),
            memberMedicineId = memberMedicineId,
            reminderTime = reminderTime,
            repeatType = repeatType,
            repeatDays = repeatDays,
            startDate = startDate,
            endDate = endDate,
            alarmTone = alarmTone,
            vibrationEnabled = vibrationEnabled,
            notificationEnabled = notificationEnabled,
            snoozeEnabled = snoozeEnabled,
            snoozeDuration = snoozeDuration,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )

        return try {
            repository.addReminder(reminder)
            scheduler.schedule(reminder)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
