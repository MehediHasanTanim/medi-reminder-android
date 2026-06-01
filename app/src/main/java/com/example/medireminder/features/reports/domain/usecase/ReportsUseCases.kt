package com.example.medireminder.features.reports.domain.usecase

import com.example.medireminder.features.reports.domain.repository.ReportsRepository
import java.util.Calendar
import javax.inject.Inject

class GetDailyReminderReportUseCase @Inject constructor(private val repository: ReportsRepository) {
    operator fun invoke(date: Long = System.currentTimeMillis()) = repository.getDailyReminderReport(startOfDay(date), endOfDay(date))
}

class GetWeeklyComplianceReportUseCase @Inject constructor(private val repository: ReportsRepository) {
    operator fun invoke(date: Long = System.currentTimeMillis()) = repository.getWeeklyComplianceReport(startOfWeek(date), endOfWeek(date))
}

class GetMonthlyComplianceReportUseCase @Inject constructor(private val repository: ReportsRepository) {
    operator fun invoke(date: Long = System.currentTimeMillis()) = repository.getMonthlyComplianceReport(startOfMonth(date), endOfMonth(date))
}

class GetMemberWiseReportUseCase @Inject constructor(private val repository: ReportsRepository) {
    operator fun invoke(startTime: Long = startOfMonth(), endTime: Long = endOfMonth()) = repository.getMemberWiseReport(startTime, endTime)
}

class GetMedicineWiseReportUseCase @Inject constructor(private val repository: ReportsRepository) {
    operator fun invoke(startTime: Long = startOfMonth(), endTime: Long = endOfMonth()) = repository.getMedicineWiseReport(startTime, endTime)
}

class GetStockUsageReportUseCase @Inject constructor(private val repository: ReportsRepository) {
    operator fun invoke(startTime: Long = startOfMonth(), endTime: Long = endOfMonth()) = repository.getStockUsageReport(startTime, endTime)
}

class GetLowStockReportUseCase @Inject constructor(private val repository: ReportsRepository) {
    operator fun invoke() = repository.getLowStockReport()
}

class GetExpiredMedicineReportUseCase @Inject constructor(private val repository: ReportsRepository) {
    operator fun invoke(currentTime: Long = System.currentTimeMillis()) = repository.getExpiredMedicineReport(currentTime)
}

private fun startOfDay(time: Long = System.currentTimeMillis()): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun endOfDay(time: Long = System.currentTimeMillis()): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}.timeInMillis

private fun startOfWeek(time: Long = System.currentTimeMillis()): Long = Calendar.getInstance().apply {
    timeInMillis = time
    firstDayOfWeek = Calendar.MONDAY
    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun endOfWeek(time: Long = System.currentTimeMillis()): Long = Calendar.getInstance().apply {
    timeInMillis = time
    firstDayOfWeek = Calendar.MONDAY
    set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}.timeInMillis

private fun startOfMonth(time: Long = System.currentTimeMillis()): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun endOfMonth(time: Long = System.currentTimeMillis()): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}.timeInMillis
