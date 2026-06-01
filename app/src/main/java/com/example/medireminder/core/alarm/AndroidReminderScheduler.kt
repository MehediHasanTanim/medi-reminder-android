package com.example.medireminder.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.medireminder.core.common.DateTimeUtils
import com.example.medireminder.features.reminder.domain.model.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(reminder: Reminder) {
        if (!canScheduleExactAlarms()) {
            Log.e("ReminderScheduler", "Cannot schedule exact alarms. Permission missing.")
            return
        }

        val nextTime = DateTimeUtils.calculateNextReminderTime(
            reminderTime = reminder.reminderTime,
            repeatType = reminder.repeatType,
            repeatDays = reminder.repeatDays,
            startDate = reminder.startDate,
            endDate = reminder.endDate
        ) ?: return

        cancel(reminder.id)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmConstants.EXTRA_REMINDER_ID, reminder.id)
            putExtra(AlarmConstants.EXTRA_SCHEDULED_TIME, nextTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("ReminderScheduler", "SecurityException while scheduling exact alarm", e)
        }
    }

    override fun cancel(reminderId: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun scheduleSnooze(reminderId: String, snoozeMinutes: Int) {
        if (!canScheduleExactAlarms()) return

        val triggerTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)
        
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmConstants.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmConstants.EXTRA_SCHEDULED_TIME, triggerTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    override fun rescheduleAll(reminders: List<Reminder>) {
        reminders.filter { it.isActive }.forEach { schedule(it) }
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}
