package com.example.medireminder.core.common

import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.settings.domain.model.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun formatTime(timeMillis: Long, timeFormat: TimeFormat): String {
        val pattern = when (timeFormat) {
            TimeFormat.HOUR_12 -> "hh:mm a"
            TimeFormat.HOUR_24 -> "HH:mm"
        }
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeMillis))
    }

    fun formatDateTime(timeMillis: Long, timeFormat: TimeFormat): String {
        val pattern = when (timeFormat) {
            TimeFormat.HOUR_12 -> "dd MMM yyyy, hh:mm a"
            TimeFormat.HOUR_24 -> "dd MMM yyyy, HH:mm"
        }
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeMillis))
    }

    fun formatShortDateTime(timeMillis: Long, timeFormat: TimeFormat): String {
        val pattern = when (timeFormat) {
            TimeFormat.HOUR_12 -> "dd MMM, hh:mm a"
            TimeFormat.HOUR_24 -> "dd MMM, HH:mm"
        }
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeMillis))
    }

    fun calculateNextReminderTime(
        reminderTime: String, // HH:mm format
        repeatType: ReminderRepeatType,
        repeatDays: List<Int>,
        startDate: Long,
        endDate: Long?
    ): Long? {
        val now = Calendar.getInstance()
        val timeParts = reminderTime.split(":")
        if (timeParts.size != 2) return null
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        val nextTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If start date is in the future, set nextTime to start date at reminderTime
        if (startDate > now.timeInMillis) {
            val startCal = Calendar.getInstance().apply { timeInMillis = startDate }
            nextTime.set(Calendar.YEAR, startCal.get(Calendar.YEAR))
            nextTime.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR))
        }

        when (repeatType) {
            ReminderRepeatType.ONCE -> {
                if (nextTime.before(now)) {
                    return null // Already passed
                }
            }
            ReminderRepeatType.DAILY -> {
                while (nextTime.before(now)) {
                    nextTime.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            ReminderRepeatType.WEEKLY, ReminderRepeatType.SPECIFIC_WEEKDAYS -> {
                if (repeatDays.isEmpty()) return null
                
                // Calendar weekdays: 1 = Sunday, 2 = Monday, ..., 7 = Saturday
                // Our weekdays: 1 = Monday, ..., 7 = Sunday
                val calendarToOurWeekday = mapOf(
                    Calendar.MONDAY to 1,
                    Calendar.TUESDAY to 2,
                    Calendar.WEDNESDAY to 3,
                    Calendar.THURSDAY to 4,
                    Calendar.FRIDAY to 5,
                    Calendar.SATURDAY to 6,
                    Calendar.SUNDAY to 7
                )
                
                val ourToCalendarWeekday = calendarToOurWeekday.entries.associate { it.value to it.key }

                var attempts = 0
                while (attempts < 8) {
                    val currentWeekday = calendarToOurWeekday[nextTime.get(Calendar.DAY_OF_WEEK)]
                    if (nextTime.after(now) && repeatDays.contains(currentWeekday)) {
                        break
                    }
                    nextTime.add(Calendar.DAY_OF_YEAR, 1)
                    attempts++
                }
                if (attempts >= 8) return null
            }
        }

        val resultTime = nextTime.timeInMillis
        if (endDate != null && resultTime > endDate) {
            return null
        }
        
        return resultTime
    }
}
