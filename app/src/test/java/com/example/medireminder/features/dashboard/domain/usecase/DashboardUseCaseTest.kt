package com.example.medireminder.features.dashboard.domain.usecase

import com.example.medireminder.features.dashboard.domain.model.DashboardOverview
import com.example.medireminder.features.dashboard.domain.model.ExpiringMedicineItem
import com.example.medireminder.features.dashboard.domain.model.FamilyOverviewItem
import com.example.medireminder.features.dashboard.domain.model.LowStockItem
import com.example.medireminder.features.dashboard.domain.model.MedicineDashboard
import com.example.medireminder.features.dashboard.domain.model.MissedReminderItem
import com.example.medireminder.features.dashboard.domain.model.TodayReminderItem
import com.example.medireminder.features.dashboard.domain.model.UpcomingReminderItem
import com.example.medireminder.features.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardUseCaseTest {

    @Test
    fun `dashboard overview returns aggregate counts`() = runBlocking {
        val overview = GetDashboardOverviewUseCase(FakeDashboardRepository()).invoke().first()

        assertEquals(2, overview.todayReminderCount)
        assertEquals(1, overview.lowStockCount)
        assertEquals(1, overview.familyMemberCount)
    }

    @Test
    fun `medicine dashboard exposes remaining days and compliance`() = runBlocking {
        val dashboard = GetMedicineDashboardUseCase(FakeDashboardRepository()).invoke("med1").first()

        assertEquals(5.0, dashboard.remainingDays!!, 0.0)
        assertEquals(75.0, dashboard.reminderCompliance, 0.0)
    }

    private class FakeDashboardRepository : DashboardRepository {
        override fun observeDashboardOverview(): Flow<DashboardOverview> = flowOf(
            DashboardOverview(
                todayReminderCount = 2,
                upcomingReminderCount = 3,
                missedReminderCount = 1,
                lowStockCount = 1,
                expiringMedicineCount = 1,
                familyMemberCount = 1
            )
        )

        override fun observeTodayReminders(startOfDay: Long, endOfDay: Long): Flow<List<TodayReminderItem>> = flowOf(emptyList())
        override fun observeUpcomingReminders(currentTime: Long, limit: Int): Flow<List<UpcomingReminderItem>> = flowOf(emptyList())
        override fun observeMissedReminders(startTime: Long, endTime: Long): Flow<List<MissedReminderItem>> = flowOf(emptyList())
        override fun observeLowStockMedicines(): Flow<List<LowStockItem>> = flowOf(emptyList())
        override fun observeExpiringMedicines(currentTime: Long, expiryThresholdTime: Long): Flow<List<ExpiringMedicineItem>> = flowOf(emptyList())
        override fun observeFamilyOverview(): Flow<List<FamilyOverviewItem>> = flowOf(emptyList())
        override fun observeMedicineDashboard(medicineId: String): Flow<MedicineDashboard> = flowOf(
            MedicineDashboard(
                medicineId = medicineId,
                medicineName = "Napa",
                currentStock = 10.0,
                unit = "tablet",
                dailyConsumption = 2.0,
                remainingDays = 5.0,
                assignedMembers = listOf("John"),
                reminderCompliance = 75.0,
                lowStockThreshold = 3.0,
                expiryDate = null
            )
        )
    }
}
