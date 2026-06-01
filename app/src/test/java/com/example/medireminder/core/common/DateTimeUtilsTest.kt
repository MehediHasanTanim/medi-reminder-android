package com.example.medireminder.core.common

import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DateTimeUtilsTest {

    @Test
    fun `Daily reminder - Next occurrence is today`() {
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 10)
        now.set(Calendar.MINUTE, 0)
        
        // Mock current time to 10:00 AM
        // We can't easily mock Calendar.getInstance() without PowerMock/MockK static mock
        // But the function uses Calendar.getInstance() internally.
        // Let's assume the test runs and we pick a time in the future for today.
        
        val targetTime = Calendar.getInstance()
        targetTime.add(Calendar.HOUR_OF_DAY, 1) // 1 hour from now
        val timeStr = String.format("%02d:%02d", targetTime.get(Calendar.HOUR_OF_DAY), targetTime.get(Calendar.MINUTE))
        
        val nextTime = DateTimeUtils.calculateNextReminderTime(
            reminderTime = timeStr,
            repeatType = ReminderRepeatType.DAILY,
            repeatDays = emptyList(),
            startDate = System.currentTimeMillis(),
            endDate = null
        )
        
        assertNotNull(nextTime)
        val resultCal = Calendar.getInstance().apply { timeInMillis = nextTime!! }
        assertEquals(targetTime.get(Calendar.HOUR_OF_DAY), resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(targetTime.get(Calendar.DAY_OF_YEAR), resultCal.get(Calendar.DAY_OF_YEAR))
    }

    @Test
    fun `Daily reminder - Next occurrence is tomorrow`() {
        val targetTime = Calendar.getInstance()
        targetTime.add(Calendar.HOUR_OF_DAY, -1) // 1 hour ago
        val timeStr = String.format("%02d:%02d", targetTime.get(Calendar.HOUR_OF_DAY), targetTime.get(Calendar.MINUTE))
        
        val nextTime = DateTimeUtils.calculateNextReminderTime(
            reminderTime = timeStr,
            repeatType = ReminderRepeatType.DAILY,
            repeatDays = emptyList(),
            startDate = System.currentTimeMillis(),
            endDate = null
        )
        
        assertNotNull(nextTime)
        val resultCal = Calendar.getInstance().apply { timeInMillis = nextTime!! }
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        
        assertEquals(targetTime.get(Calendar.HOUR_OF_DAY), resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(tomorrow.get(Calendar.DAY_OF_YEAR), resultCal.get(Calendar.DAY_OF_YEAR))
    }

    @Test
    fun `Once reminder - In past returns null`() {
        val past = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, -1) }
        val timeStr = String.format("%02d:%02d", past.get(Calendar.HOUR_OF_DAY), past.get(Calendar.MINUTE))
        
        val nextTime = DateTimeUtils.calculateNextReminderTime(
            reminderTime = timeStr,
            repeatType = ReminderRepeatType.ONCE,
            repeatDays = emptyList(),
            startDate = System.currentTimeMillis() - 100000,
            endDate = null
        )
        
        assertNull(nextTime)
    }

    @Test
    fun `End date passed - Returns null`() {
        val timeStr = "10:00"
        val nextTime = DateTimeUtils.calculateNextReminderTime(
            reminderTime = timeStr,
            repeatType = ReminderRepeatType.DAILY,
            repeatDays = emptyList(),
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() - 1000 // End date in past
        )
        
        assertNull(nextTime)
    }

    @Test
    fun `Specific weekdays - Returns next matching day`() {
        val now = Calendar.getInstance()
        // Let's say today is Monday (2 in Calendar)
        // We want Friday (6 in Calendar, 5 in our enum)
        
        val nextTime = DateTimeUtils.calculateNextReminderTime(
            reminderTime = "08:00",
            repeatType = ReminderRepeatType.SPECIFIC_WEEKDAYS,
            repeatDays = listOf(5), // Friday
            startDate = System.currentTimeMillis(),
            endDate = null
        )
        
        assertNotNull(nextTime)
        val resultCal = Calendar.getInstance().apply { timeInMillis = nextTime!! }
        
        // Calendar.FRIDAY is 6
        assertEquals(Calendar.FRIDAY, resultCal.get(Calendar.DAY_OF_WEEK))
    }
}
