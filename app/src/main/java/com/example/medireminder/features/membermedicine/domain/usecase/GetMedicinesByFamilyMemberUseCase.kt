package com.example.medireminder.features.membermedicine.domain.usecase

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMedicinesByFamilyMemberUseCase @Inject constructor(
    private val repository: MemberMedicineRepository
) {
    operator fun invoke(familyMemberId: String): Flow<List<MemberMedicine>> {
        return repository.observeMedicinesByMember(familyMemberId)
    }
}
