package com.example.medireminder.core.alarm

import android.util.Log
import com.example.medireminder.core.common.DateTimeUtils
import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class ReminderRescheduler @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) {

    companion object {
        private const val TAG = "ReminderRescheduler"
    }

    suspend fun rescheduleAllActiveReminders(): RescheduleResult {
        val activeReminders = try {
            reminderRepository.getActiveReminders()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load active reminders", e)
            return RescheduleResult()
        }

        if (activeReminders.isEmpty()) {
            Log.d(TAG, "No active reminders to reschedule")
            return RescheduleResult(totalActiveReminders = 0)
        }

        var successfullyScheduled = 0
        var failedToSchedule = 0
        var skippedExpired = 0

        for (reminder in activeReminders) {
            when (rescheduleReminderInternal(reminder)) {
                RescheduleOutcome.SCHEDULED -> successfullyScheduled++
                RescheduleOutcome.FAILED -> failedToSchedule++
                RescheduleOutcome.EXPIRED -> skippedExpired++
            }
        }

        val result = RescheduleResult(
            totalActiveReminders = activeReminders.size,
            successfullyScheduled = successfullyScheduled,
            failedToSchedule = failedToSchedule,
            skippedExpired = skippedExpired
        )

        Log.d(TAG, "Reschedule complete: $result")
        return result
    }

    suspend fun rescheduleReminder(reminderId: String): Boolean {
        val reminder = try {
            reminderRepository.getReminderById(reminderId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load reminder $reminderId", e)
            return false
        }

        return if (reminder != null && reminder.isActive) {
            rescheduleReminderInternal(reminder) == RescheduleOutcome.SCHEDULED
        } else {
            false
        }
    }

    private fun rescheduleReminderInternal(reminder: Reminder): RescheduleOutcome {
        if (!reminder.isActive) return RescheduleOutcome.EXPIRED

        val nextTime = DateTimeUtils.calculateNextReminderTime(
            reminderTime = reminder.reminderTime,
            repeatType = reminder.repeatType,
            repeatDays = reminder.repeatDays,
            startDate = reminder.startDate,
            endDate = reminder.endDate
        )

        if (nextTime == null) {
            // Reminder has expired (past end date or one-time past event)
            Log.d(TAG, "Reminder ${reminder.id} has expired, skipping")
            return RescheduleOutcome.EXPIRED
        }

        return try {
            reminderScheduler.schedule(reminder)
            Log.d(TAG, "Successfully scheduled reminder ${reminder.id}")
            RescheduleOutcome.SCHEDULED
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule reminder ${reminder.id}", e)
            RescheduleOutcome.FAILED
        }
    }

    private enum class RescheduleOutcome {
        SCHEDULED,
        FAILED,
        EXPIRED
    }
}
