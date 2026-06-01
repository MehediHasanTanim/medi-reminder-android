package com.example.medireminder.core.alarm

import android.content.Context
import android.content.Intent
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class BootCompletedReceiverTest {

    private lateinit var context: Context
    private lateinit var receiver: BootCompletedReceiver

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        receiver = BootCompletedReceiver()
    }

    @Test
    fun `BOOT_COMPLETED does not crash`() {
        val intent = mockk<Intent>(relaxed = true)
        every { intent.action } returns Intent.ACTION_BOOT_COMPLETED

        // Should not throw any exception
        receiver.onReceive(context, intent)
    }

    @Test
    fun `MY_PACKAGE_REPLACED does not crash`() {
        val intent = mockk<Intent>(relaxed = true)
        every { intent.action } returns Intent.ACTION_MY_PACKAGE_REPLACED

        receiver.onReceive(context, intent)
    }

    @Test
    fun `TIME_CHANGED does not crash`() {
        val intent = mockk<Intent>(relaxed = true)
        every { intent.action } returns Intent.ACTION_TIME_CHANGED

        receiver.onReceive(context, intent)
    }

    @Test
    fun `TIMEZONE_CHANGED does not crash`() {
        val intent = mockk<Intent>(relaxed = true)
        every { intent.action } returns Intent.ACTION_TIMEZONE_CHANGED

        receiver.onReceive(context, intent)
    }

    @Test
    fun `unknown action is silently ignored`() {
        val intent = mockk<Intent>(relaxed = true)
        every { intent.action } returns Intent.ACTION_AIRPLANE_MODE_CHANGED

        // Should not throw any exception
        receiver.onReceive(context, intent)
    }

    @Test
    fun `LOCKED_BOOT_COMPLETED does not crash`() {
        val intent = mockk<Intent>(relaxed = true)
        every { intent.action } returns Intent.ACTION_LOCKED_BOOT_COMPLETED

        receiver.onReceive(context, intent)
    }
}
