package com.example.medireminder.features.dashboard.presentation.state

import com.example.medireminder.features.dashboard.domain.model.DashboardOverview
import com.example.medireminder.features.dashboard.domain.model.ExpiringMedicineItem
import com.example.medireminder.features.dashboard.domain.model.FamilyOverviewItem
import com.example.medireminder.features.dashboard.domain.model.LowStockItem
import com.example.medireminder.features.dashboard.domain.model.MissedReminderItem
import com.example.medireminder.features.dashboard.domain.model.TodayReminderItem
import com.example.medireminder.features.dashboard.domain.model.UpcomingReminderItem

data class DashboardUiState(
    val isLoading: Boolean = false,
    val overview: DashboardOverview? = null,
    val todayReminders: List<TodayReminderItem> = emptyList(),
    val upcomingReminders: List<UpcomingReminderItem> = emptyList(),
    val missedReminders: List<MissedReminderItem> = emptyList(),
    val lowStockMedicines: List<LowStockItem> = emptyList(),
    val expiringMedicines: List<ExpiringMedicineItem> = emptyList(),
    val familyMembers: List<FamilyOverviewItem> = emptyList(),
    val errorMessage: String? = null
)
