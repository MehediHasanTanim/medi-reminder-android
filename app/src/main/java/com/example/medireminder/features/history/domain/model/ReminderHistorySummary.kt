package com.example.medireminder.features.history.domain.model

data class ReminderHistorySummary(
    val totalCount: Int,
    val takenCount: Int,
    val skippedCount: Int,
    val missedCount: Int,
    val snoozedCount: Int,
    val dismissedCount: Int,
    val adherencePercentage: Double
)
