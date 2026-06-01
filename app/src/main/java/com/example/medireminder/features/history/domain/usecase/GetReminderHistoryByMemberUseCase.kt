package com.example.medireminder.features.history.domain.usecase

import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import javax.inject.Inject

class GetReminderHistoryByMemberUseCase @Inject constructor(
    private val repository: ReminderHistoryRepository
) {
    operator fun invoke(memberId: String) = repository.observeHistoryByMember(memberId)
}
