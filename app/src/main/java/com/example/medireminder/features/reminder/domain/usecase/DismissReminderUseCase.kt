package com.example.medireminder.features.reminder.domain.usecase

import com.example.medireminder.core.alarm.ReminderScheduler
import com.example.medireminder.features.reminder.domain.model.ReminderAction
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class DismissReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(reminderId: String, scheduledTime: Long): Result<Unit> {
        return try {
            val reminder = repository.getReminderById(reminderId) ?: return Result.failure(Exception("Reminder not found"))
            if (repository.hasReminderLog(reminderId, scheduledTime, ReminderAction.DISMISSED)) {
                return Result.failure(Exception("Reminder already dismissed"))
            }
            
            repository.addReminderLog(
                reminderId = reminder.id,
                memberId = reminder.familyMemberId ?: "",
                medicineId = reminder.medicineId ?: "",
                scheduledTime = scheduledTime,
                actionTaken = ReminderAction.DISMISSED
            )
            
            // Schedule next occurrence if it's not a one-time reminder
            if (reminder.repeatType != ReminderRepeatType.ONCE) {
                scheduler.schedule(reminder)
            } else {
                repository.deactivateReminder(reminderId)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
