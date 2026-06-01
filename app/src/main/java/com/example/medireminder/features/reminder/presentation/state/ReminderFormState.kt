package com.example.medireminder.features.reminder.presentation.state

import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType

data class ReminderFormState(
    val memberMedicineId: String = "",
    val reminderTime: String = "",
    val repeatType: ReminderRepeatType = ReminderRepeatType.DAILY,
    val repeatDays: List<Int> = emptyList(),
    val startDate: Long? = System.currentTimeMillis(),
    val endDate: Long? = null,
    val alarmTone: String? = null,
    val vibrationEnabled: Boolean = true,
    val notificationEnabled: Boolean = true,
    val snoozeEnabled: Boolean = true,
    val snoozeDuration: String = "5",
    val isActive: Boolean = true,
    val validationErrors: Map<String, String> = emptyMap()
)
