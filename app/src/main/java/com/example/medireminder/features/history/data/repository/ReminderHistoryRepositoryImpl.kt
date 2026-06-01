package com.example.medireminder.features.history.data.repository

import com.example.medireminder.core.database.dao.ReminderLogDao
import com.example.medireminder.features.history.data.mapper.toDomain
import com.example.medireminder.features.history.data.mapper.toEntity
import com.example.medireminder.features.history.data.mapper.toReminderHistoryStatus
import com.example.medireminder.features.history.domain.model.ReminderHistory
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.history.domain.model.ReminderHistorySummary
import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.roundToInt

class ReminderHistoryRepositoryImpl @Inject constructor(
    private val reminderLogDao: ReminderLogDao
) : ReminderHistoryRepository {

    override fun observeAllHistory(): Flow<List<ReminderHistory>> {
        return reminderLogDao.observeHistoryWithDetails().map { list -> list.map { it.toDomain() } }
    }

    override fun observeHistoryByMember(memberId: String): Flow<List<ReminderHistory>> {
        return observeHistoryByFilters(memberId, null, null, null, null)
    }

    override fun observeHistoryByMedicine(medicineId: String): Flow<List<ReminderHistory>> {
        return observeHistoryByFilters(null, medicineId, null, null, null)
    }

    override fun observeHistoryByStatus(status: ReminderHistoryStatus): Flow<List<ReminderHistory>> {
        return observeHistoryByFilters(null, null, status, null, null)
    }

    override fun observeHistoryByDateRange(startTime: Long, endTime: Long): Flow<List<ReminderHistory>> {
        return observeHistoryByFilters(null, null, null, startTime, endTime)
    }

    override fun observeHistoryByFilters(
        memberId: String?,
        medicineId: String?,
        status: ReminderHistoryStatus?,
        startTime: Long?,
        endTime: Long?
    ): Flow<List<ReminderHistory>> {
        return reminderLogDao.observeHistoryWithDetailsByFilters(
            memberId = memberId,
            medicineId = medicineId,
            status = status?.name,
            startTime = startTime,
            endTime = endTime
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getHistoryById(id: String): ReminderHistory? {
        return reminderLogDao.getHistoryWithDetailsById(id)?.toDomain()
    }

    override suspend fun createLog(history: ReminderHistory) {
        reminderLogDao.insert(history.toEntity())
    }

    override suspend fun hasLog(
        reminderId: String,
        scheduledTime: Long,
        status: ReminderHistoryStatus
    ): Boolean {
        return reminderLogDao.hasLog(reminderId, scheduledTime, status.name)
    }

    override suspend fun getSummary(startTime: Long, endTime: Long): ReminderHistorySummary {
        val logs = reminderLogDao.getSummaryByDateRange(startTime, endTime)
        val total = logs.size
        val taken = logs.count { it.actionTaken.toReminderHistoryStatus() == ReminderHistoryStatus.TAKEN }
        val skipped = logs.count { it.actionTaken.toReminderHistoryStatus() == ReminderHistoryStatus.SKIPPED }
        val missed = logs.count { it.actionTaken.toReminderHistoryStatus() == ReminderHistoryStatus.MISSED }
        val snoozed = logs.count { it.actionTaken.toReminderHistoryStatus() == ReminderHistoryStatus.SNOOZED }
        val dismissed = logs.count { it.actionTaken.toReminderHistoryStatus() == ReminderHistoryStatus.DISMISSED }
        val adherence = if (total == 0) 0.0 else ((taken.toDouble() / total) * 1000).roundToInt() / 10.0

        return ReminderHistorySummary(
            totalCount = total,
            takenCount = taken,
            skippedCount = skipped,
            missedCount = missed,
            snoozedCount = snoozed,
            dismissedCount = dismissed,
            adherencePercentage = adherence
        )
    }
}
