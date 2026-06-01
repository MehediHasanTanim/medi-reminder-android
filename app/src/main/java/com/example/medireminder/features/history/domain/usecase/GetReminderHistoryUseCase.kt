package com.example.medireminder.features.history.domain.usecase

import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import javax.inject.Inject

class GetReminderHistoryUseCase @Inject constructor(
    private val repository: ReminderHistoryRepository
) {
    operator fun invoke() = repository.observeAllHistory()
}
