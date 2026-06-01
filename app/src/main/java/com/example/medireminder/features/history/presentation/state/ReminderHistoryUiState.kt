package com.example.medireminder.features.history.presentation.state

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.history.domain.model.ReminderHistory
import com.example.medireminder.features.history.domain.model.ReminderHistorySummary
import com.example.medireminder.features.medicine.domain.model.Medicine

data class ReminderHistoryUiState(
    val isLoading: Boolean = false,
    val historyItems: List<ReminderHistory> = emptyList(),
    val selectedHistory: ReminderHistory? = null,
    val summary: ReminderHistorySummary? = null,
    val familyMembers: List<FamilyMember> = emptyList(),
    val medicines: List<Medicine> = emptyList(),
    val errorMessage: String? = null
)
