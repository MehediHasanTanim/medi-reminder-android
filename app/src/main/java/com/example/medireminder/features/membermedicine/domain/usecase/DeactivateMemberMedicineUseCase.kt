package com.example.medireminder.features.membermedicine.domain.usecase

import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import javax.inject.Inject

class DeactivateMemberMedicineUseCase @Inject constructor(
    private val repository: MemberMedicineRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return try {
            repository.deactivateAssignment(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
