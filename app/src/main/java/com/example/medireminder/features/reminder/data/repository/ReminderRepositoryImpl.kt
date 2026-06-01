package com.example.medireminder.features.reminder.data.repository

import com.example.medireminder.core.database.dao.ReminderDao
import com.example.medireminder.core.database.dao.ReminderLogDao
import com.example.medireminder.core.database.dao.ReminderWithDetails
import com.example.medireminder.core.database.entities.ReminderEntity
import com.example.medireminder.core.database.entities.ReminderLogEntity
import com.example.medireminder.features.reminder.data.mapper.toDomain
import com.example.medireminder.features.reminder.data.mapper.toEntity
import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.model.ReminderAction
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    private val reminderLogDao: ReminderLogDao
) : ReminderRepository {

    override fun observeAllReminders(): Flow<List<Reminder>> {
        return reminderDao.observeRemindersWithDetails().map { details ->
            details.map { it.toDomainModel() }
        }
    }

    override fun observeActiveReminders(): Flow<List<Reminder>> {
        return reminderDao.observeActiveReminders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeRemindersByMemberMedicine(memberMedicineId: String): Flow<List<Reminder>> {
        return reminderDao.observeRemindersByMemberMedicine(memberMedicineId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReminderById(id: String): Reminder? {
        return reminderDao.getReminderWithDetailsById(id)?.toDomainModel()
    }

    override suspend fun addReminder(reminder: Reminder) {
        reminderDao.insert(reminder.toEntity())
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.update(reminder.toEntity())
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.delete(reminder.toEntity())
    }

    override suspend fun deactivateReminder(id: String) {
        val reminder = reminderDao.getById(id)
        reminder?.let {
            reminderDao.update(it.copy(isActive = false, updatedAt = System.currentTimeMillis()))
        }
    }

    override suspend fun getActiveReminders(): List<Reminder> {
        return reminderDao.getActiveRemindersSync().map { it.toDomain() }
    }

    override suspend fun addReminderLog(
        reminderId: String,
        memberId: String,
        medicineId: String,
        scheduledTime: Long,
        actionTaken: ReminderAction,
        notes: String?
    ) {
        val log = ReminderLogEntity(
            id = UUID.randomUUID().toString(),
            reminderId = reminderId,
            memberId = memberId,
            medicineId = medicineId,
            scheduledTime = scheduledTime,
            actionTaken = actionTaken.name,
            actionTime = System.currentTimeMillis(),
            notes = notes,
            createdAt = System.currentTimeMillis()
        )
        reminderLogDao.insert(log)
    }

    override suspend fun hasReminderLog(
        reminderId: String,
        scheduledTime: Long,
        actionTaken: ReminderAction
    ): Boolean {
        return reminderLogDao.hasLog(reminderId, scheduledTime, actionTaken.name)
    }

    private fun ReminderWithDetails.toDomainModel(): Reminder {
        return ReminderEntity(
            id = id,
            memberMedicineId = memberMedicineId,
            reminderTime = reminderTime,
            repeatType = repeatType,
            repeatDays = repeatDays,
            startDate = startDate,
            endDate = endDate,
            alarmTone = alarmTone,
            vibrationEnabled = vibrationEnabled,
            notificationEnabled = notificationEnabled,
            snoozeEnabled = snoozeEnabled,
            snoozeDuration = snoozeDuration,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        ).toDomain(
            familyMemberId = familyMemberId,
            familyMemberName = familyMemberName,
            medicineId = medicineId,
            medicineName = medicineName,
            dosageQuantity = dosageQuantity,
            medicineUnit = medicineUnit,
            instructions = instructions,
            autoReduceStock = autoReduceStock
        )
    }
}
