package com.example.medireminder.features.history.domain.usecase

import com.example.medireminder.features.history.domain.model.ReminderHistory
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import java.util.UUID
import javax.inject.Inject

class CreateReminderLogUseCase @Inject constructor(
    private val repository: ReminderHistoryRepository
) {
    suspend operator fun invoke(
        reminderId: String,
        memberId: String,
        medicineId: String,
        scheduledTime: Long,
        status: ReminderHistoryStatus,
        actionTime: Long? = System.currentTimeMillis(),
        notes: String? = null
    ): Result<Unit> {
        if (reminderId.isBlank()) return Result.failure(Exception("Reminder is required"))
        if (memberId.isBlank()) return Result.failure(Exception("Family member is required"))
        if (medicineId.isBlank()) return Result.failure(Exception("Medicine is required"))

        return try {
            repository.createLog(
                ReminderHistory(
                    id = UUID.randomUUID().toString(),
                    reminderId = reminderId,
                    memberId = memberId,
                    medicineId = medicineId,
                    scheduledTime = scheduledTime,
                    status = status,
                    actionTime = actionTime,
                    notes = notes,
                    createdAt = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
