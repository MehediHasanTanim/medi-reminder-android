package com.example.medireminder.core.alarm

import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReminderReschedulerTest {

    private lateinit var repository: ReminderRepository
    private lateinit var scheduler: ReminderScheduler
    private lateinit var rescheduler: ReminderRescheduler

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        scheduler = mockk(relaxed = true)
        rescheduler = ReminderRescheduler(repository, scheduler)
    }

    @Test
    fun `rescheduler loads active reminders`() = runBlocking {
        val reminder = createDailyReminder(id = "1")
        coEvery { repository.getActiveReminders() } returns listOf(reminder)

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(1, result.totalActiveReminders)
        coVerify { repository.getActiveReminders() }
    }

    @Test
    fun `expired reminders are skipped`() = runBlocking {
        val pastOnceReminder = createReminder(
            id = "1",
            repeatType = ReminderRepeatType.ONCE,
            startDate = System.currentTimeMillis() - 86400000, // yesterday
            endDate = null
        )
        coEvery { repository.getActiveReminders() } returns listOf(pastOnceReminder)

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(1, result.totalActiveReminders)
        assertEquals(0, result.successfullyScheduled)
        assertEquals(1, result.skippedExpired)
        coVerify(exactly = 0) { scheduler.schedule(any()) }
    }

    @Test
    fun `daily reminder is rescheduled`() = runBlocking {
        val futureTime = System.currentTimeMillis() + 3600000 // 1 hour from now
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = futureTime
        val timeStr = String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))

        val reminder = createReminder(
            id = "daily1",
            repeatType = ReminderRepeatType.DAILY,
            reminderTime = timeStr,
            startDate = System.currentTimeMillis(),
            endDate = null
        )
        coEvery { repository.getActiveReminders() } returns listOf(reminder)

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(1, result.totalActiveReminders)
        assertEquals(1, result.successfullyScheduled)
        assertEquals(0, result.skippedExpired)
        coVerify(exactly = 1) { scheduler.schedule(reminder) }
    }

    @Test
    fun `weekly reminder is rescheduled`() = runBlocking {
        val now = java.util.Calendar.getInstance()
        val targetWeekday = if (now.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.FRIDAY) {
            java.util.Calendar.FRIDAY
        } else {
            java.util.Calendar.MONDAY
        }
        val ourWeekday = mapOf(
            java.util.Calendar.MONDAY to 1, java.util.Calendar.TUESDAY to 2,
            java.util.Calendar.WEDNESDAY to 3, java.util.Calendar.THURSDAY to 4,
            java.util.Calendar.FRIDAY to 5, java.util.Calendar.SATURDAY to 6,
            java.util.Calendar.SUNDAY to 7
        )[targetWeekday] ?: 1

        val reminder = createReminder(
            id = "weekly1",
            repeatType = ReminderRepeatType.SPECIFIC_WEEKDAYS,
            repeatDays = listOf(ourWeekday),
            reminderTime = "08:00",
            startDate = System.currentTimeMillis(),
            endDate = null
        )
        coEvery { repository.getActiveReminders() } returns listOf(reminder)

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(1, result.totalActiveReminders)
        assertEquals(1, result.successfullyScheduled)
        assertEquals(0, result.skippedExpired)
        coVerify(exactly = 1) { scheduler.schedule(reminder) }
    }

    @Test
    fun `one-time past reminder is skipped`() = runBlocking {
        val pastReminder = createReminder(
            id = "once1",
            repeatType = ReminderRepeatType.ONCE,
            reminderTime = "00:01",
            startDate = System.currentTimeMillis() - 86400000, // yesterday
            endDate = null
        )
        coEvery { repository.getActiveReminders() } returns listOf(pastReminder)

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(1, result.totalActiveReminders)
        assertEquals(0, result.successfullyScheduled)
        assertEquals(1, result.skippedExpired)
    }

    @Test
    fun `reschedule result counts success and failure correctly`() = runBlocking {
        val goodReminder = createDailyReminder(id = "good")
        val badReminder = createDailyReminder(id = "bad")

        coEvery { repository.getActiveReminders() } returns listOf(goodReminder, badReminder)
        coEvery { scheduler.schedule(goodReminder) } returns Unit
        coEvery { scheduler.schedule(badReminder) } throws RuntimeException("Schedule failed")

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(2, result.totalActiveReminders)
        assertEquals(1, result.successfullyScheduled)
        assertEquals(1, result.failedToSchedule)
        assertEquals(0, result.skippedExpired)
    }

    @Test
    fun `exact alarm permission missing does not crash and returns failure`() = runBlocking {
        val reminder = createDailyReminder(id = "perm1")
        coEvery { repository.getActiveReminders() } returns listOf(reminder)
        coEvery { scheduler.schedule(any()) } throws SecurityException("Cannot schedule exact alarm")

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(1, result.totalActiveReminders)
        assertEquals(0, result.successfullyScheduled)
        assertEquals(1, result.failedToSchedule)
    }

    @Test
    fun `empty active reminders returns empty result`() = runBlocking {
        coEvery { repository.getActiveReminders() } returns emptyList()

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(0, result.totalActiveReminders)
        assertEquals(0, result.successfullyScheduled)
    }

    @Test
    fun `repository exception does not crash`() = runBlocking {
        coEvery { repository.getActiveReminders() } throws RuntimeException("DB error")

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(0, result.totalActiveReminders)
        assertEquals(0, result.successfullyScheduled)
    }

    @Test
    fun `reschedule single reminder returns true on success`() = runBlocking {
        val reminder = createDailyReminder(id = "single1")
        coEvery { repository.getReminderById("single1") } returns reminder

        val result = rescheduler.rescheduleReminder("single1")

        assertTrue(result)
        coVerify { scheduler.schedule(reminder) }
    }

    @Test
    fun `reschedule single reminder returns false when not found`() = runBlocking {
        coEvery { repository.getReminderById("missing") } returns null

        val result = rescheduler.rescheduleReminder("missing")

        assertFalse(result)
    }

    private fun createDailyReminder(id: String): Reminder {
        val futureTime = System.currentTimeMillis() + 3600000
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = futureTime
        val timeStr = String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
        return createReminder(
            id = id,
            repeatType = ReminderRepeatType.DAILY,
            reminderTime = timeStr,
            startDate = System.currentTimeMillis(),
            endDate = null
        )
    }

    private fun createReminder(
        id: String,
        repeatType: ReminderRepeatType = ReminderRepeatType.DAILY,
        repeatDays: List<Int> = emptyList(),
        reminderTime: String = "08:00",
        startDate: Long = System.currentTimeMillis(),
        endDate: Long? = null,
        isActive: Boolean = true
    ): Reminder {
        return Reminder(
            id = id,
            memberMedicineId = "mm1",
            reminderTime = reminderTime,
            repeatType = repeatType,
            repeatDays = repeatDays,
            startDate = startDate,
            endDate = endDate,
            alarmTone = null,
            vibrationEnabled = true,
            notificationEnabled = true,
            snoozeEnabled = true,
            snoozeDuration = 10,
            isActive = isActive,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}
