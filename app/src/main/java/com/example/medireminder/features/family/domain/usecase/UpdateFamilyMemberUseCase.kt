package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import javax.inject.Inject

class UpdateFamilyMemberUseCase @Inject constructor(
    private val repository: FamilyMemberRepository
) {
    suspend operator fun invoke(
        id: String,
        fullName: String,
        age: Int?,
        gender: String?,
        relationship: String?,
        bloodGroup: String?,
        phone: String?,
        notes: String?,
        isActive: Boolean
    ): Result<Unit> {
        if (fullName.isBlank()) {
            return Result.failure(Exception("Full name is required"))
        }
        if (age != null && age < 0) {
            return Result.failure(Exception("Age cannot be negative"))
        }

        val existingMember = repository.getMemberById(id) 
            ?: return Result.failure(Exception("Member not found"))

        val updatedMember = existingMember.copy(
            fullName = fullName.trim(),
            age = age,
            gender = gender,
            relationship = relationship,
            bloodGroup = bloodGroup,
            phone = phone,
            notes = notes,
            isActive = isActive,
            updatedAt = System.currentTimeMillis()
        )
        
        repository.updateMember(updatedMember)
        return Result.success(Unit)
    }
}
