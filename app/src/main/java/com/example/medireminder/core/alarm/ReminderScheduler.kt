package com.example.medireminder.core.alarm

import com.example.medireminder.features.reminder.domain.model.Reminder

interface ReminderScheduler {
    fun schedule(reminder: Reminder)
    fun cancel(reminderId: String)
    fun scheduleSnooze(reminderId: String, snoozeMinutes: Int)
    fun rescheduleAll(reminders: List<Reminder>)
}
