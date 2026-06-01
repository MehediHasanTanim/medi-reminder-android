package com.example.medireminder.features.history.data.mapper

import com.example.medireminder.core.database.dao.ReminderHistoryWithDetails
import com.example.medireminder.core.database.entities.ReminderLogEntity
import com.example.medireminder.features.history.domain.model.ReminderHistory
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus

fun ReminderLogEntity.toDomain(): ReminderHistory {
    return ReminderHistory(
        id = id,
        reminderId = reminderId,
        memberId = memberId,
        medicineId = medicineId,
        scheduledTime = scheduledTime,
        status = actionTaken.toReminderHistoryStatus(),
        actionTime = actionTime,
        notes = notes,
        createdAt = createdAt
    )
}

fun ReminderHistoryWithDetails.toDomain(): ReminderHistory {
    return ReminderHistory(
        id = logId,
        reminderId = reminderId,
        memberId = memberId,
        memberName = memberName,
        medicineId = medicineId,
        medicineName = medicineName,
        medicineStrength = medicineStrength,
        scheduledTime = scheduledTime,
        status = actionTaken.toReminderHistoryStatus(),
        actionTime = actionTime,
        notes = notes,
        createdAt = createdAt
    )
}

fun ReminderHistory.toEntity(): ReminderLogEntity {
    return ReminderLogEntity(
        id = id,
        reminderId = reminderId,
        memberId = memberId,
        medicineId = medicineId,
        scheduledTime = scheduledTime,
        actionTaken = status.name,
        actionTime = actionTime,
        notes = notes,
        createdAt = createdAt
    )
}

fun String.toReminderHistoryStatus(): ReminderHistoryStatus {
    return runCatching { ReminderHistoryStatus.valueOf(this) }
        .getOrDefault(ReminderHistoryStatus.MISSED)
}
