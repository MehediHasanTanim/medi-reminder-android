package com.example.medireminder.core.alarm

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Placeholder for reminder logic
        // In the future, this might trigger a notification or schedule an exact alarm
        return Result.success()
    }
}
