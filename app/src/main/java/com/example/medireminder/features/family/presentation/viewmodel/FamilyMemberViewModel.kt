package com.example.medireminder.features.family.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.usecase.*
import com.example.medireminder.features.family.presentation.state.FamilyMemberFormState
import com.example.medireminder.features.family.presentation.state.FamilyMemberUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyMemberViewModel @Inject constructor(
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase,
    private val getFamilyMemberByIdUseCase: GetFamilyMemberByIdUseCase,
    private val addFamilyMemberUseCase: AddFamilyMemberUseCase,
    private val updateFamilyMemberUseCase: UpdateFamilyMemberUseCase,
    private val deleteFamilyMemberUseCase: DeleteFamilyMemberUseCase,
    private val toggleActiveStatusUseCase: ToggleFamilyMemberActiveStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FamilyMemberUiState())
    val uiState: StateFlow<FamilyMemberUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(FamilyMemberFormState())
    val formState: StateFlow<FamilyMemberFormState> = _formState.asStateFlow()

    init {
        loadMembers()
    }

    fun loadMembers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getFamilyMembersUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { members ->
                    _uiState.update { it.copy(isLoading = false, members = members) }
                }
        }
    }

    fun loadMemberDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val member = getFamilyMemberByIdUseCase(id)
            if (member != null) {
                _uiState.update { it.copy(isLoading = false, selectedMember = member) }
                _formState.update { 
                    it.copy(
                        fullName = member.fullName,
                        age = member.age?.toString() ?: "",
                        gender = member.gender ?: "",
                        relationship = member.relationship ?: "",
                        bloodGroup = member.bloodGroup ?: "",
                        phone = member.phone ?: "",
                        notes = member.notes ?: "",
                        isActive = member.isActive
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Member not found") }
            }
        }
    }

    fun onFullNameChange(name: String) {
        _formState.update { it.copy(fullName = name, fullNameError = null) }
    }

    fun onAgeChange(age: String) {
        _formState.update { it.copy(age = age, ageError = null) }
    }

    fun onGenderChange(gender: String) {
        _formState.update { it.copy(gender = gender) }
    }

    fun onRelationshipChange(rel: String) {
        _formState.update { it.copy(relationship = rel) }
    }

    fun onBloodGroupChange(bg: String) {
        _formState.update { it.copy(bloodGroup = bg) }
    }

    fun onPhoneChange(phone: String) {
        _formState.update { it.copy(phone = phone) }
    }

    fun onNotesChange(notes: String) {
        _formState.update { it.copy(notes = notes) }
    }

    fun onActiveStatusChange(active: Boolean) {
        _formState.update { it.copy(isActive = active) }
    }

    fun saveMember(id: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _formState.value
            val ageInt = state.age.toIntOrNull()
            
            val result = if (id == null) {
                addFamilyMemberUseCase(
                    fullName = state.fullName,
                    age = ageInt,
                    gender = state.gender,
                    relationship = state.relationship,
                    bloodGroup = state.bloodGroup,
                    phone = state.phone,
                    notes = state.notes
                )
            } else {
                updateFamilyMemberUseCase(
                    id = id,
                    fullName = state.fullName,
                    age = ageInt,
                    gender = state.gender,
                    relationship = state.relationship,
                    bloodGroup = state.bloodGroup,
                    phone = state.phone,
                    notes = state.notes,
                    isActive = state.isActive
                )
            }

            result.onSuccess {
                _uiState.update { it.copy(successMessage = if (id == null) "Member added" else "Member updated") }
                onSuccess()
            }.onFailure { e ->
                if (e.message?.contains("name", ignoreCase = true) == true) {
                    _formState.update { it.copy(fullNameError = e.message) }
                } else if (e.message?.contains("age", ignoreCase = true) == true) {
                    _formState.update { it.copy(ageError = e.message) }
                } else {
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
            }
        }
    }

    fun deleteMember(member: FamilyMember, onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteFamilyMemberUseCase(member)
            _uiState.update { it.copy(successMessage = "Member deleted") }
            onSuccess()
        }
    }

    fun toggleActiveStatus(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            toggleActiveStatusUseCase(id, !currentStatus)
            loadMemberDetails(id) // Refresh details
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
