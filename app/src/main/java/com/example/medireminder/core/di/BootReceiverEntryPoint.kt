package com.example.medireminder.core.di

import com.example.medireminder.core.alarm.ReminderRescheduler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BootReceiverEntryPoint {
    fun reminderRescheduler(): ReminderRescheduler
}
