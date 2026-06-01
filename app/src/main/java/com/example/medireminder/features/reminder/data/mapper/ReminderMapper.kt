package com.example.medireminder.features.reminder.data.mapper

import com.example.medireminder.core.database.entities.ReminderEntity
import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType

fun ReminderEntity.toDomain(
    familyMemberId: String? = null,
    familyMemberName: String? = null,
    medicineId: String? = null,
    medicineName: String? = null,
    dosageQuantity: Double? = null,
    medicineUnit: String? = null,
    instructions: String? = null,
    autoReduceStock: Boolean = false
): Reminder {
    return Reminder(
        id = id,
        memberMedicineId = memberMedicineId,
        reminderTime = reminderTime,
        repeatType = ReminderRepeatType.valueOf(repeatType),
        repeatDays = repeatDays?.split(",")?.filter { it.isNotBlank() }?.map { it.toInt() } ?: emptyList(),
        startDate = startDate,
        endDate = endDate,
        alarmTone = alarmTone,
        vibrationEnabled = vibrationEnabled,
        notificationEnabled = notificationEnabled,
        snoozeEnabled = snoozeEnabled,
        snoozeDuration = snoozeDuration,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
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

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        memberMedicineId = memberMedicineId,
        reminderTime = reminderTime,
        repeatType = repeatType.name,
        repeatDays = if (repeatDays.isEmpty()) null else repeatDays.joinToString(","),
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
    )
}
