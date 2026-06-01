package com.example.medireminder.features.history.domain.usecase

import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import javax.inject.Inject

class GetReminderHistorySummaryUseCase @Inject constructor(
    private val repository: ReminderHistoryRepository
) {
    suspend operator fun invoke(startTime: Long, endTime: Long) = repository.getSummary(startTime, endTime)
}
