package com.example.medireminder.core.alarm

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderRescheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderRescheduler: ReminderRescheduler
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "ReminderRescheduleWorker"
        const val UNIQUE_WORK_NAME = "reminder_reschedule_work"
    }

    override suspend fun doWork(): Result {
        return try {
            val result = reminderRescheduler.rescheduleAllActiveReminders()
            Log.d(TAG, "Reschedule worker completed: $result")

            if (result.failedToSchedule > 0 && runAttemptCount < 3) {
                Log.w(TAG, "${result.failedToSchedule} reminders failed, retrying...")
                Result.retry()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Reschedule worker failed with exception", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
