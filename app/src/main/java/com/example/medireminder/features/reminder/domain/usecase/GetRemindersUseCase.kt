package com.example.medireminder.features.reminder.domain.usecase

import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    operator fun invoke(onlyActive: Boolean = false): Flow<List<Reminder>> {
        return if (onlyActive) {
            repository.observeActiveReminders()
        } else {
            repository.observeAllReminders()
        }
    }
}
