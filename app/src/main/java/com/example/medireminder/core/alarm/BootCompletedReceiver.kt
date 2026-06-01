package com.example.medireminder.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class BootCompletedReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootCompletedReceiver"

        private val RESCHEDULE_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == null || action !in RESCHEDULE_ACTIONS) {
            Log.d(TAG, "Ignoring unknown action: $action")
            return
        }

        Log.d(TAG, "Received action: $action - enqueuing reschedule work")

        try {
            val workRequest = OneTimeWorkRequestBuilder<ReminderRescheduleWorker>()
                .addTag(ReminderRescheduleWorker.UNIQUE_WORK_NAME)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    ReminderRescheduleWorker.UNIQUE_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enqueue reschedule work", e)
        }
    }
}
