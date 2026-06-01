package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import java.util.UUID
import javax.inject.Inject

class AddFamilyMemberUseCase @Inject constructor(
    private val repository: FamilyMemberRepository
) {
    suspend operator fun invoke(
        fullName: String,
        age: Int?,
        gender: String?,
        relationship: String?,
        bloodGroup: String?,
        phone: String?,
        notes: String?
    ): Result<Unit> {
        if (fullName.isBlank()) {
            return Result.failure(Exception("Full name is required"))
        }
        if (age != null && age < 0) {
            return Result.failure(Exception("Age cannot be negative"))
        }

        val currentTime = System.currentTimeMillis()
        val member = FamilyMember(
            id = UUID.randomUUID().toString(),
            fullName = fullName.trim(),
            age = age,
            gender = gender,
            relationship = relationship,
            bloodGroup = bloodGroup,
            phone = phone,
            notes = notes,
            isActive = true,
            createdAt = currentTime,
            updatedAt = currentTime
        )
        repository.addMember(member)
        return Result.success(Unit)
    }
}
