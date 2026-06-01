package com.example.medireminder.features.dashboard.domain.usecase

import com.example.medireminder.features.dashboard.domain.repository.DashboardRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetDashboardOverviewUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke() = repository.observeDashboardOverview()
}

class GetTodayRemindersUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke() = repository.observeTodayReminders(startOfDay(), endOfDay())
}

class GetUpcomingRemindersUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke(limit: Int = 5) = repository.observeUpcomingReminders(System.currentTimeMillis(), limit)
}

class GetMissedRemindersUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke() = repository.observeMissedReminders(startOfDay(), endOfDay())
}

class GetLowStockDashboardUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke() = repository.observeLowStockMedicines()
}

class GetExpiringMedicinesUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke(thresholdDays: Int = 7) = repository.observeExpiringMedicines(
        currentTime = System.currentTimeMillis(),
        expiryThresholdTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(thresholdDays.toLong())
    )
}

class GetFamilyOverviewUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke() = repository.observeFamilyOverview()
}

class GetMedicineDashboardUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke(medicineId: String) = repository.observeMedicineDashboard(medicineId)
}

private fun startOfDay(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun endOfDay(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}
