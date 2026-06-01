package com.example.medireminder.core.alarm

data class RescheduleResult(
    val totalActiveReminders: Int = 0,
    val successfullyScheduled: Int = 0,
    val failedToSchedule: Int = 0,
    val skippedExpired: Int = 0
) {
    val isCompleteSuccess: Boolean
        get() = successfullyScheduled == totalActiveReminders && failedToSchedule == 0

    val hasFailures: Boolean
        get() = failedToSchedule > 0

    val hasExpired: Boolean
        get() = skippedExpired > 0
}
