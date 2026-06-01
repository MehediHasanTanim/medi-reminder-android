package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFamilyMembersUseCase @Inject constructor(
    private val repository: FamilyMemberRepository
) {
    operator fun invoke(onlyActive: Boolean = false): Flow<List<FamilyMember>> {
        return if (onlyActive) {
            repository.observeActiveMembers()
        } else {
            repository.observeAllMembers()
        }
    }
}
