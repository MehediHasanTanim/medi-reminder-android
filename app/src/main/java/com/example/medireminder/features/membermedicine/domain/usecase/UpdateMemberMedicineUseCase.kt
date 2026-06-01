package com.example.medireminder.features.membermedicine.domain.usecase

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import javax.inject.Inject

class UpdateMemberMedicineUseCase @Inject constructor(
    private val repository: MemberMedicineRepository
) {
    suspend operator fun invoke(
        memberMedicine: MemberMedicine
    ): Result<Unit> {
        if (memberMedicine.dosageQuantity <= 0) return Result.failure(Exception("Dosage quantity must be greater than 0"))
        if (memberMedicine.frequencyPerDay <= 0) return Result.failure(Exception("Frequency per day must be greater than 0"))
        if (memberMedicine.endDate != null && memberMedicine.endDate < memberMedicine.startDate) {
            return Result.failure(Exception("End date cannot be earlier than start date"))
        }

        val updatedAssignment = memberMedicine.copy(
            dailyTotalQuantity = memberMedicine.dosageQuantity * memberMedicine.frequencyPerDay,
            updatedAt = System.currentTimeMillis()
        )

        return try {
            repository.updateAssignment(updatedAssignment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
