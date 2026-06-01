package com.example.medireminder.features.membermedicine.domain.usecase

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMemberMedicinesUseCase @Inject constructor(
    private val repository: MemberMedicineRepository
) {
    operator fun invoke(onlyActive: Boolean = false): Flow<List<MemberMedicine>> {
        return if (onlyActive) {
            repository.observeActiveAssignments()
        } else {
            repository.observeAllAssignments()
        }
    }
}
