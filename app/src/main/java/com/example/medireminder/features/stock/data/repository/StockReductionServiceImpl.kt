package com.example.medireminder.features.stock.data.repository

import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import com.example.medireminder.features.stock.domain.StockReductionService
import com.example.medireminder.features.stock.domain.usecase.ReduceStockUseCase
import javax.inject.Inject

class StockReductionServiceImpl @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val memberMedicineRepository: MemberMedicineRepository,
    private val reduceStockUseCase: ReduceStockUseCase
) : StockReductionService {

    override suspend fun reduceStockForTakenReminder(reminderId: String): Result<Unit> {
        return try {
            val reminder = reminderRepository.getReminderById(reminderId)
                ?: return Result.failure(Exception("Reminder not found"))

            val memberMedicine = memberMedicineRepository.getAssignmentById(reminder.memberMedicineId)
                ?: return Result.failure(Exception("Member medicine assignment not found"))

            if (memberMedicine.autoReduceStock) {
                reduceStockUseCase(
                    medicineId = memberMedicine.medicineId,
                    quantity = memberMedicine.dosageQuantity,
                    reason = "Reminder Taken: ${memberMedicine.medicineName} for ${memberMedicine.familyMemberName}"
                )
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
