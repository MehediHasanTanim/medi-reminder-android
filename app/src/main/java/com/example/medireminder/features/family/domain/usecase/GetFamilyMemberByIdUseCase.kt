package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import javax.inject.Inject

class GetFamilyMemberByIdUseCase @Inject constructor(
    private val repository: FamilyMemberRepository
) {
    suspend operator fun invoke(id: String): FamilyMember? {
        return repository.getMemberById(id)
    }
}
