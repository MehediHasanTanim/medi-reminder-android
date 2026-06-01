package com.example.medireminder.core.di

import com.example.medireminder.core.alarm.AndroidReminderScheduler
import com.example.medireminder.core.alarm.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(
        scheduler: AndroidReminderScheduler
    ): ReminderScheduler
}
