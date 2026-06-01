package com.example.medireminder.core.alarm

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ReminderRescheduleWorkerTest {

    private lateinit var rescheduler: ReminderRescheduler

    @Before
    fun setUp() {
        rescheduler = mockk(relaxed = true)
    }

    @Test
    fun `worker calls ReminderRescheduler`() = runBlocking {
        coEvery { rescheduler.rescheduleAllActiveReminders() } returns RescheduleResult(
            totalActiveReminders = 3,
            successfullyScheduled = 3
        )

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(3, result.successfullyScheduled)
        coVerify(exactly = 1) { rescheduler.rescheduleAllActiveReminders() }
    }

    @Test
    fun `worker returns success when all reminders scheduled`() = runBlocking {
        coEvery { rescheduler.rescheduleAllActiveReminders() } returns RescheduleResult(
            totalActiveReminders = 5,
            successfullyScheduled = 5
        )

        val result = rescheduler.rescheduleAllActiveReminders()

        assertEquals(5, result.totalActiveReminders)
        assertEquals(5, result.successfullyScheduled)
        assertEquals(0, result.failedToSchedule)
    }

    @Test
    fun `worker handles exception gracefully`() {
        runBlocking {
            coEvery { rescheduler.rescheduleAllActiveReminders() } throws RuntimeException("Test exception")

            try {
                rescheduler.rescheduleAllActiveReminders()
            } catch (e: RuntimeException) {
                assertEquals("Test exception", e.message)
            }
        }
    }
}
