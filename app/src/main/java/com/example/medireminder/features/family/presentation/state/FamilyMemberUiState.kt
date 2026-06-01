package com.example.medireminder.features.family.presentation.state

import com.example.medireminder.features.family.domain.model.FamilyMember

data class FamilyMemberUiState(
    val isLoading: Boolean = false,
    val members: List<FamilyMember> = emptyList(),
    val selectedMember: FamilyMember? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class FamilyMemberFormState(
    val fullName: String = "",
    val age: String = "",
    val gender: String = "",
    val relationship: String = "",
    val bloodGroup: String = "",
    val phone: String = "",
    val notes: String = "",
    val isActive: Boolean = true,
    val fullNameError: String? = null,
    val ageError: String? = null
)
