package com.example.medireminder.features.reminder.presentation.state

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.reminder.domain.model.Reminder

data class ReminderUiState(
    val isLoading: Boolean = false,
    val reminders: List<Reminder> = emptyList(),
    val memberMedicines: List<MemberMedicine> = emptyList(),
    val selectedReminder: Reminder? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
