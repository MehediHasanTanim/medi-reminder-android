package com.example.medireminder.features.reminder.domain.usecase

import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class GetReminderByIdUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(id: String): Reminder? {
        return repository.getReminderById(id)
    }
}
