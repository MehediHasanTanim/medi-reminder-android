package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import javax.inject.Inject

class ToggleFamilyMemberActiveStatusUseCase @Inject constructor(
    private val repository: FamilyMemberRepository
) {
    suspend operator fun invoke(id: String, isActive: Boolean) {
        repository.toggleActiveStatus(id, isActive)
    }
}
