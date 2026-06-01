package com.example.medireminder.features.history.presentation.state

import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus

data class ReminderHistoryFilterState(
    val selectedMemberId: String? = null,
    val selectedMedicineId: String? = null,
    val selectedStatus: ReminderHistoryStatus? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val selectedPreset: String? = "Today"
)
