package com.example.medireminder.features.history.domain.usecase

import com.example.medireminder.features.history.domain.model.ReminderHistory
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.history.domain.model.ReminderHistorySummary
import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderHistoryUseCaseTest {

    @Test
    fun `create taken log validates required fields`() = runBlocking {
        val useCase = CreateReminderLogUseCase(FakeHistoryRepository())

        val result = useCase(
            reminderId = "",
            memberId = "member1",
            medicineId = "med1",
            scheduledTime = 1L,
            status = ReminderHistoryStatus.TAKEN
        )

        assertTrue(result.isFailure)
        assertEquals("Reminder is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create skipped log saves history`() = runBlocking {
        val repository = FakeHistoryRepository()
        val useCase = CreateReminderLogUseCase(repository)

        val result = useCase(
            reminderId = "rem1",
            memberId = "member1",
            medicineId = "med1",
            scheduledTime = 1L,
            status = ReminderHistoryStatus.SKIPPED
        )

        assertTrue(result.isSuccess)
        assertEquals(ReminderHistoryStatus.SKIPPED, repository.items.single().status)
    }

    @Test
    fun `summary calculates adherence percentage`() = runBlocking {
        val repository = FakeHistoryRepository(
            mutableListOf(
                history("1", ReminderHistoryStatus.TAKEN),
                history("2", ReminderHistoryStatus.TAKEN),
                history("3", ReminderHistoryStatus.MISSED),
                history("4", ReminderHistoryStatus.SKIPPED)
            )
        )
        val useCase = GetReminderHistorySummaryUseCase(repository)

        val summary = useCase(0L, 10L)

        assertEquals(4, summary.totalCount)
        assertEquals(2, summary.takenCount)
        assertEquals(50.0, summary.adherencePercentage, 0.0)
    }

    private fun history(id: String, status: ReminderHistoryStatus) = ReminderHistory(
        id = id,
        reminderId = "rem$id",
        memberId = "member1",
        medicineId = "med1",
        scheduledTime = id.toLong(),
        status = status,
        actionTime = 2L,
        createdAt = 2L
    )

    private class FakeHistoryRepository(
        val items: MutableList<ReminderHistory> = mutableListOf()
    ) : ReminderHistoryRepository {
        override fun observeAllHistory(): Flow<List<ReminderHistory>> = flowOf(items)
        override fun observeHistoryByMember(memberId: String): Flow<List<ReminderHistory>> = flowOf(items.filter { it.memberId == memberId })
        override fun observeHistoryByMedicine(medicineId: String): Flow<List<ReminderHistory>> = flowOf(items.filter { it.medicineId == medicineId })
        override fun observeHistoryByStatus(status: ReminderHistoryStatus): Flow<List<ReminderHistory>> = flowOf(items.filter { it.status == status })
        override fun observeHistoryByDateRange(startTime: Long, endTime: Long): Flow<List<ReminderHistory>> = flowOf(items.filter { it.scheduledTime in startTime..endTime })
        override fun observeHistoryByFilters(memberId: String?, medicineId: String?, status: ReminderHistoryStatus?, startTime: Long?, endTime: Long?): Flow<List<ReminderHistory>> {
            return flowOf(items.filter {
                (memberId == null || it.memberId == memberId) &&
                    (medicineId == null || it.medicineId == medicineId) &&
                    (status == null || it.status == status) &&
                    (startTime == null || it.scheduledTime >= startTime) &&
                    (endTime == null || it.scheduledTime <= endTime)
            })
        }
        override suspend fun getHistoryById(id: String): ReminderHistory? = items.find { it.id == id }
        override suspend fun createLog(history: ReminderHistory) {
            items += history
        }
        override suspend fun hasLog(reminderId: String, scheduledTime: Long, status: ReminderHistoryStatus): Boolean {
            return items.any { it.reminderId == reminderId && it.scheduledTime == scheduledTime && it.status == status }
        }
        override suspend fun getSummary(startTime: Long, endTime: Long): ReminderHistorySummary {
            val scoped = items.filter { it.scheduledTime in startTime..endTime }
            val total = scoped.size
            val taken = scoped.count { it.status == ReminderHistoryStatus.TAKEN }
            return ReminderHistorySummary(
                totalCount = total,
                takenCount = taken,
                skippedCount = scoped.count { it.status == ReminderHistoryStatus.SKIPPED },
                missedCount = scoped.count { it.status == ReminderHistoryStatus.MISSED },
                snoozedCount = scoped.count { it.status == ReminderHistoryStatus.SNOOZED },
                dismissedCount = scoped.count { it.status == ReminderHistoryStatus.DISMISSED },
                adherencePercentage = if (total == 0) 0.0 else (taken.toDouble() / total) * 100
            )
        }
    }
}
