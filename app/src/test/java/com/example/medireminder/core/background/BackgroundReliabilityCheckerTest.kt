package com.example.medireminder.core.background

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackgroundReliabilityCheckerTest {

    @Test
    fun `ReliabilityStatus data class works correctly`() {
        val status = ReliabilityStatus(
            exactAlarmAllowed = true,
            notificationAllowed = true,
            batteryOptimizationIgnored = false,
            bootReceiverEnabled = true
        )

        assertTrue(status.exactAlarmAllowed)
        assertTrue(status.notificationAllowed)
        assertFalse(status.batteryOptimizationIgnored)
        assertTrue(status.bootReceiverEnabled)
        assertTrue(status.needsUserAction)
        assertTrue(status.warnings.isNotEmpty())
    }

    @Test
    fun `ReliabilityStatus with all permissions has no warnings`() {
        val status = ReliabilityStatus(
            exactAlarmAllowed = true,
            notificationAllowed = true,
            batteryOptimizationIgnored = true,
            bootReceiverEnabled = true
        )

        assertFalse(status.needsUserAction)
        assertTrue(status.warnings.isEmpty())
    }

    @Test
    fun `ReliabilityStatus with missing permissions has warnings`() {
        val status = ReliabilityStatus(
            exactAlarmAllowed = false,
            notificationAllowed = false,
            batteryOptimizationIgnored = false,
            bootReceiverEnabled = false
        )

        assertTrue(status.needsUserAction)
        assertEquals(4, status.warnings.size)
    }

    @Test
    fun `ReliabilityStatus warnings contain relevant messages`() {
        val status = ReliabilityStatus(
            exactAlarmAllowed = false,
            notificationAllowed = false,
            batteryOptimizationIgnored = false,
            bootReceiverEnabled = true
        )

        assertTrue(status.warnings.any { it.contains("Exact alarm") })
        assertTrue(status.warnings.any { it.contains("Notification") })
        assertTrue(status.warnings.any { it.contains("Battery") })
    }

    private fun assertEquals(expected: Int, actual: Int) {
        org.junit.Assert.assertEquals(expected, actual)
    }
}
