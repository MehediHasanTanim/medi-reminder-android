package com.example.medireminder.features.history.domain.usecase

import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import javax.inject.Inject

class GetReminderHistoryByDateRangeUseCase @Inject constructor(
    private val repository: ReminderHistoryRepository
) {
    operator fun invoke(startTime: Long, endTime: Long) = repository.observeHistoryByDateRange(startTime, endTime)
}
