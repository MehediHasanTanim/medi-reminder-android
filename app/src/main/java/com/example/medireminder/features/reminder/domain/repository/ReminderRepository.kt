package com.example.medireminder.features.reminder.domain.repository

import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.model.ReminderAction
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun observeAllReminders(): Flow<List<Reminder>>
    fun observeActiveReminders(): Flow<List<Reminder>>
    fun observeRemindersByMemberMedicine(memberMedicineId: String): Flow<List<Reminder>>
    suspend fun getReminderById(id: String): Reminder?
    suspend fun addReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun deactivateReminder(id: String)
    suspend fun getActiveReminders(): List<Reminder>
    
    // Reminder Logs
    suspend fun addReminderLog(
        reminderId: String,
        memberId: String,
        medicineId: String,
        scheduledTime: Long,
        actionTaken: ReminderAction,
        notes: String? = null
    )

    suspend fun hasReminderLog(
        reminderId: String,
        scheduledTime: Long,
        actionTaken: ReminderAction
    ): Boolean
}
