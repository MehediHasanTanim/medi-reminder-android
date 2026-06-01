package com.example.medireminder.features.membermedicine.domain.usecase

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import javax.inject.Inject

class GetMemberMedicineByIdUseCase @Inject constructor(
    private val repository: MemberMedicineRepository
) {
    suspend operator fun invoke(id: String): MemberMedicine? {
        return repository.getAssignmentById(id)
    }
}
