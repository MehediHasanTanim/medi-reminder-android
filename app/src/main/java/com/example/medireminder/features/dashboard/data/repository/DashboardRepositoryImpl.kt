package com.example.medireminder.features.dashboard.data.repository

import com.example.medireminder.features.dashboard.data.mapper.reminderTimeToMillis
import com.example.medireminder.features.dashboard.data.mapper.roundOneDecimal
import com.example.medireminder.features.dashboard.domain.model.DashboardOverview
import com.example.medireminder.features.dashboard.domain.model.ExpiringMedicineItem
import com.example.medireminder.features.dashboard.domain.model.FamilyOverviewItem
import com.example.medireminder.features.dashboard.domain.model.LowStockItem
import com.example.medireminder.features.dashboard.domain.model.MedicineDashboard
import com.example.medireminder.features.dashboard.domain.model.MissedReminderItem
import com.example.medireminder.features.dashboard.domain.model.TodayReminderItem
import com.example.medireminder.features.dashboard.domain.model.UpcomingReminderItem
import com.example.medireminder.features.dashboard.domain.repository.DashboardRepository
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import com.example.medireminder.features.settings.domain.repository.SettingsRepository
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DashboardRepositoryImpl @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val historyRepository: ReminderHistoryRepository,
    private val stockRepository: MedicineStockRepository,
    private val familyRepository: FamilyMemberRepository,
    private val memberMedicineRepository: MemberMedicineRepository,
    private val settingsRepository: SettingsRepository
) : DashboardRepository {

    override fun observeDashboardOverview(): Flow<DashboardOverview> {
        val now = System.currentTimeMillis()
        val start = startOfDay(now)
        val end = endOfDay(now)
        val reminderCounts = combine(
            observeTodayReminders(start, end),
            observeUpcomingReminders(now, 10),
            observeMissedReminders(start, end)
        ) { today, upcoming, missed ->
            Triple(today.size, upcoming.size, missed.size)
        }

        return combine(
            reminderCounts,
            observeLowStockMedicines(),
            stockRepository.observeAllStock(),
            settingsRepository.observeSettings(),
            familyRepository.observeActiveMembers()
        ) { counts, lowStock, stocks, settings, family ->
            val expiryEnd = now + TimeUnit.DAYS.toMillis(settings.expiryAlertDays.toLong())
            val expiringCount = stocks.count { stock ->
                val expiry = stock.expiryDate
                expiry != null && expiry > now && expiry <= expiryEnd
            }
            DashboardOverview(
                todayReminderCount = counts.first,
                upcomingReminderCount = counts.second,
                missedReminderCount = counts.third,
                lowStockCount = lowStock.size,
                expiringMedicineCount = expiringCount,
                familyMemberCount = family.size
            )
        }
    }

    override fun observeTodayReminders(startOfDay: Long, endOfDay: Long): Flow<List<TodayReminderItem>> {
        return combine(
            reminderRepository.observeAllReminders(),
            historyRepository.observeHistoryByDateRange(startOfDay, endOfDay)
        ) { reminders, logs ->
            reminders.filter { it.isActive && it.startDate <= endOfDay && (it.endDate == null || it.endDate >= startOfDay) }
                .map { reminder ->
                    val scheduled = reminderTimeToMillis(startOfDay, reminder.reminderTime)
                    val log = logs.find { it.reminderId == reminder.id && it.scheduledTime == scheduled }
                    TodayReminderItem(
                        reminderId = reminder.id,
                        memberName = reminder.familyMemberName ?: "Unknown",
                        medicineName = reminder.medicineName ?: "Unknown",
                        dosage = "${reminder.dosageQuantity ?: 0.0} ${reminder.medicineUnit ?: ""}".trim(),
                        reminderTime = scheduled,
                        status = log?.status?.name ?: "PENDING",
                        instructions = reminder.instructions
                    )
                }
                .sortedBy { it.reminderTime }
        }
    }

    override fun observeUpcomingReminders(currentTime: Long, limit: Int): Flow<List<UpcomingReminderItem>> {
        val todayStart = startOfDay(currentTime)
        return reminderRepository.observeAllReminders().map { reminders ->
            reminders.filter { it.isActive }
                .map { reminder ->
                    val scheduled = reminderTimeToMillis(todayStart, reminder.reminderTime)
                    UpcomingReminderItem(
                        reminderId = reminder.id,
                        memberName = reminder.familyMemberName ?: "Unknown",
                        medicineName = reminder.medicineName ?: "Unknown",
                        reminderTime = if (scheduled >= currentTime) scheduled else scheduled + TimeUnit.DAYS.toMillis(1),
                        dosage = reminder.dosageQuantity?.let { "$it ${reminder.medicineUnit ?: ""}".trim() }
                    )
                }
                .filter { it.reminderTime >= currentTime }
                .sortedBy { it.reminderTime }
                .take(limit)
        }
    }

    override fun observeMissedReminders(startTime: Long, endTime: Long): Flow<List<MissedReminderItem>> {
        return historyRepository.observeHistoryByFilters(
            memberId = null,
            medicineId = null,
            status = ReminderHistoryStatus.MISSED,
            startTime = startTime,
            endTime = endTime
        ).map { logs ->
            logs.map {
                MissedReminderItem(
                    reminderLogId = it.id,
                    memberName = it.memberName ?: "Unknown",
                    medicineName = it.medicineName ?: "Unknown",
                    scheduledTime = it.scheduledTime,
                    notes = it.notes
                )
            }
        }
    }

    override fun observeLowStockMedicines(): Flow<List<LowStockItem>> {
        return stockRepository.observeLowStockMedicines().map { stocks ->
            stocks.map {
                LowStockItem(
                    medicineId = it.medicineId,
                    medicineName = it.medicineName ?: "Unknown",
                    currentQuantity = it.currentQuantity,
                    unit = it.unit,
                    lowStockThreshold = it.lowStockThreshold
                )
            }
        }
    }

    override fun observeExpiringMedicines(currentTime: Long, expiryThresholdTime: Long): Flow<List<ExpiringMedicineItem>> {
        return stockRepository.observeAllStock().map { stocks ->
            stocks.mapNotNull { stock ->
                val expiry = stock.expiryDate ?: return@mapNotNull null
                if (expiry <= currentTime || expiry > expiryThresholdTime) return@mapNotNull null
                ExpiringMedicineItem(
                    medicineId = stock.medicineId,
                    medicineName = stock.medicineName ?: "Unknown",
                    expiryDate = expiry,
                    daysRemaining = TimeUnit.MILLISECONDS.toDays(expiry - currentTime).toInt().coerceAtLeast(0)
                )
            }.sortedBy { it.expiryDate }
        }
    }

    override fun observeFamilyOverview(): Flow<List<FamilyOverviewItem>> {
        val now = System.currentTimeMillis()
        val start = startOfDay(now)
        val end = endOfDay(now)
        return combine(
            familyRepository.observeActiveMembers(),
            memberMedicineRepository.observeActiveAssignments(),
            reminderRepository.observeAllReminders(),
            historyRepository.observeHistoryByFilters(null, null, ReminderHistoryStatus.MISSED, start, end)
        ) { members, assignments, reminders, missed ->
            members.map { member ->
                val memberAssignments = assignments.filter { it.familyMemberId == member.id }
                val memberReminderIds = reminders.filter { it.familyMemberId == member.id }.map { it.id }.toSet()
                FamilyOverviewItem(
                    memberId = member.id,
                    fullName = member.fullName,
                    relationship = member.relationship,
                    activeMedicineCount = memberAssignments.size,
                    todayReminderCount = reminders.count { it.familyMemberId == member.id && it.isActive },
                    missedReminderCount = missed.count { it.memberId == member.id || it.reminderId in memberReminderIds }
                )
            }
        }
    }

    override fun observeMedicineDashboard(medicineId: String): Flow<MedicineDashboard> {
        return combine(
            stockRepository.observeAllStock(),
            memberMedicineRepository.observeMembersByMedicine(medicineId),
            historyRepository.observeHistoryByMedicine(medicineId)
        ) { stocks, assignments, history ->
            val stock = stocks.firstOrNull { it.medicineId == medicineId }
            val activeAssignments = assignments.filter { it.isActive && (it.endDate == null || it.endDate > System.currentTimeMillis()) }
            val dailyConsumption = activeAssignments.sumOf { it.dailyTotalQuantity }
            val actionable = history.filter { it.status != ReminderHistoryStatus.SNOOZED }
            val taken = actionable.count { it.status == ReminderHistoryStatus.TAKEN }
            val compliance = if (actionable.isEmpty()) 0.0 else roundOneDecimal(taken.toDouble() / actionable.size * 100)

            MedicineDashboard(
                medicineId = medicineId,
                medicineName = stock?.medicineName ?: activeAssignments.firstOrNull()?.medicineName ?: "Unknown",
                currentStock = stock?.currentQuantity ?: 0.0,
                unit = stock?.unit ?: activeAssignments.firstOrNull()?.medicineUnit ?: "",
                dailyConsumption = dailyConsumption,
                remainingDays = if (dailyConsumption <= 0.0 || stock == null) null else roundOneDecimal(stock.currentQuantity / dailyConsumption),
                assignedMembers = activeAssignments.mapNotNull { it.familyMemberName },
                reminderCompliance = compliance,
                lowStockThreshold = stock?.lowStockThreshold ?: 0.0,
                expiryDate = stock?.expiryDate
            )
        }
    }

    private fun startOfDay(time: Long): Long {
        return java.util.Calendar.getInstance().apply {
            timeInMillis = time
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun endOfDay(time: Long): Long {
        return java.util.Calendar.getInstance().apply {
            timeInMillis = time
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
            set(java.util.Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}
