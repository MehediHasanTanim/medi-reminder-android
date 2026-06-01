package com.example.medireminder.features.history.domain.repository

import com.example.medireminder.features.history.domain.model.ReminderHistory
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.history.domain.model.ReminderHistorySummary
import kotlinx.coroutines.flow.Flow

interface ReminderHistoryRepository {
    fun observeAllHistory(): Flow<List<ReminderHistory>>
    fun observeHistoryByMember(memberId: String): Flow<List<ReminderHistory>>
    fun observeHistoryByMedicine(medicineId: String): Flow<List<ReminderHistory>>
    fun observeHistoryByStatus(status: ReminderHistoryStatus): Flow<List<ReminderHistory>>
    fun observeHistoryByDateRange(startTime: Long, endTime: Long): Flow<List<ReminderHistory>>
    fun observeHistoryByFilters(
        memberId: String?,
        medicineId: String?,
        status: ReminderHistoryStatus?,
        startTime: Long?,
        endTime: Long?
    ): Flow<List<ReminderHistory>>
    suspend fun getHistoryById(id: String): ReminderHistory?
    suspend fun createLog(history: ReminderHistory)
    suspend fun hasLog(reminderId: String, scheduledTime: Long, status: ReminderHistoryStatus): Boolean
    suspend fun getSummary(startTime: Long, endTime: Long): ReminderHistorySummary
}
