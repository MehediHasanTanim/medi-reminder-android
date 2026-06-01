package com.example.medireminder.core.alarm

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RescheduleResultTest {

    @Test
    fun `isCompleteSuccess when all reminders scheduled`() {
        val result = RescheduleResult(
            totalActiveReminders = 5,
            successfullyScheduled = 5
        )

        assertTrue(result.isCompleteSuccess)
        assertFalse(result.hasFailures)
        assertFalse(result.hasExpired)
    }

    @Test
    fun `isCompleteSuccess false when not all scheduled`() {
        val result = RescheduleResult(
            totalActiveReminders = 5,
            successfullyScheduled = 3,
            failedToSchedule = 2
        )

        assertFalse(result.isCompleteSuccess)
        assertTrue(result.hasFailures)
    }

    @Test
    fun `hasExpired true when expired reminders exist`() {
        val result = RescheduleResult(
            totalActiveReminders = 5,
            successfullyScheduled = 3,
            skippedExpired = 2
        )

        assertTrue(result.hasExpired)
    }

    @Test
    fun `empty result is complete success`() {
        val result = RescheduleResult()

        assertTrue(result.isCompleteSuccess)
        assertFalse(result.hasFailures)
        assertFalse(result.hasExpired)
    }
}
