package com.example.medireminder.features.membermedicine.domain.usecase

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMembersByMedicineUseCase @Inject constructor(
    private val repository: MemberMedicineRepository
) {
    operator fun invoke(medicineId: String): Flow<List<MemberMedicine>> {
        return repository.observeMembersByMedicine(medicineId)
    }
}
