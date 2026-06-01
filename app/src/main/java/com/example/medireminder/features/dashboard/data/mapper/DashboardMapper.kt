package com.example.medireminder.features.dashboard.data.mapper

import java.util.Calendar
import kotlin.math.roundToInt

fun reminderTimeToMillis(dayMillis: Long, reminderTime: String): Long {
    val parts = reminderTime.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return Calendar.getInstance().apply {
        timeInMillis = dayMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun roundOneDecimal(value: Double): Double = (value * 10).roundToInt() / 10.0
