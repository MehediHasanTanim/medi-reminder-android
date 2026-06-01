package com.example.medireminder.features.history.domain.usecase

import com.example.medireminder.core.alarm.ReminderScheduler
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class MarkReminderMissedUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val historyRepository: ReminderHistoryRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(reminderId: String, scheduledTime: Long): Result<Unit> {
        return try {
            val reminder = reminderRepository.getReminderById(reminderId)
                ?: return Result.failure(Exception("Reminder not found"))

            val alreadyHandled = ReminderHistoryStatus.values().any { status ->
                historyRepository.hasLog(reminderId, scheduledTime, status)
            }
            if (alreadyHandled) return Result.success(Unit)

            historyRepository.createLog(
                com.example.medireminder.features.history.domain.model.ReminderHistory(
                    id = java.util.UUID.randomUUID().toString(),
                    reminderId = reminder.id,
                    memberId = reminder.familyMemberId ?: "",
                    medicineId = reminder.medicineId ?: "",
                    scheduledTime = scheduledTime,
                    status = ReminderHistoryStatus.MISSED,
                    actionTime = null,
                    notes = "No action within reminder timeout",
                    createdAt = System.currentTimeMillis()
                )
            )

            if (reminder.repeatType != ReminderRepeatType.ONCE) {
                scheduler.schedule(reminder)
            } else {
                reminderRepository.deactivateReminder(reminderId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
