package com.example.medireminder.features.dashboard.domain.repository

import com.example.medireminder.features.dashboard.domain.model.DashboardOverview
import com.example.medireminder.features.dashboard.domain.model.ExpiringMedicineItem
import com.example.medireminder.features.dashboard.domain.model.FamilyOverviewItem
import com.example.medireminder.features.dashboard.domain.model.LowStockItem
import com.example.medireminder.features.dashboard.domain.model.MedicineDashboard
import com.example.medireminder.features.dashboard.domain.model.MissedReminderItem
import com.example.medireminder.features.dashboard.domain.model.TodayReminderItem
import com.example.medireminder.features.dashboard.domain.model.UpcomingReminderItem
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeDashboardOverview(): Flow<DashboardOverview>
    fun observeTodayReminders(startOfDay: Long, endOfDay: Long): Flow<List<TodayReminderItem>>
    fun observeUpcomingReminders(currentTime: Long, limit: Int): Flow<List<UpcomingReminderItem>>
    fun observeMissedReminders(startTime: Long, endTime: Long): Flow<List<MissedReminderItem>>
    fun observeLowStockMedicines(): Flow<List<LowStockItem>>
    fun observeExpiringMedicines(currentTime: Long, expiryThresholdTime: Long): Flow<List<ExpiringMedicineItem>>
    fun observeFamilyOverview(): Flow<List<FamilyOverviewItem>>
    fun observeMedicineDashboard(medicineId: String): Flow<MedicineDashboard>
}
