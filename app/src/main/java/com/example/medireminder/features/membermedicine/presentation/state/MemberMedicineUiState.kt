package com.example.medireminder.features.membermedicine.presentation.state

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine

data class MemberMedicineUiState(
    val isLoading: Boolean = false,
    val assignments: List<MemberMedicine> = emptyList(),
    val familyMembers: List<FamilyMember> = emptyList(),
    val medicines: List<Medicine> = emptyList(),
    val selectedAssignment: MemberMedicine? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
