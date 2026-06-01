package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CalculateDailyConsumptionUseCase @Inject constructor(
    private val memberMedicineRepository: MemberMedicineRepository
) {
    suspend operator fun invoke(medicineId: String): Double {
        val now = System.currentTimeMillis()
        val assignments = memberMedicineRepository.observeMembersByMedicine(medicineId).first()
        return assignments
            .filter { assignment ->
                assignment.isActive && 
                assignment.autoReduceStock && 
                (assignment.endDate == null || assignment.endDate > now)
            }
            .sumOf { it.dailyTotalQuantity }
    }
}
