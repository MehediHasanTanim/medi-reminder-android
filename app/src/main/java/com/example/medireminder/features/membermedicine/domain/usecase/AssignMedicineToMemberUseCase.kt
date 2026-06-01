package com.example.medireminder.features.membermedicine.domain.usecase

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import java.util.UUID
import javax.inject.Inject

class AssignMedicineToMemberUseCase @Inject constructor(
    private val repository: MemberMedicineRepository
) {
    suspend operator fun invoke(
        familyMemberId: String,
        medicineId: String,
        dosageQuantity: Double,
        frequencyPerDay: Int,
        startDate: Long,
        endDate: Long? = null,
        instructions: String? = null,
        autoReduceStock: Boolean = true
    ): Result<Unit> {
        if (familyMemberId.isBlank()) return Result.failure(Exception("Family member is required"))
        if (medicineId.isBlank()) return Result.failure(Exception("Medicine is required"))
        if (dosageQuantity <= 0) return Result.failure(Exception("Dosage quantity must be greater than 0"))
        if (frequencyPerDay <= 0) return Result.failure(Exception("Frequency per day must be greater than 0"))
        if (endDate != null && endDate < startDate) {
            return Result.failure(Exception("End date cannot be earlier than start date"))
        }

        val now = System.currentTimeMillis()
        val assignment = MemberMedicine(
            id = UUID.randomUUID().toString(),
            familyMemberId = familyMemberId,
            medicineId = medicineId,
            dosageQuantity = dosageQuantity,
            frequencyPerDay = frequencyPerDay,
            dailyTotalQuantity = dosageQuantity * frequencyPerDay,
            instructions = instructions,
            startDate = startDate,
            endDate = endDate,
            isActive = true,
            autoReduceStock = autoReduceStock,
            createdAt = now,
            updatedAt = now
        )

        return try {
            repository.assignMedicine(assignment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
