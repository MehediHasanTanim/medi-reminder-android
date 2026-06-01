package com.example.medireminder.features.membermedicine.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.family.domain.usecase.GetFamilyMembersUseCase
import com.example.medireminder.features.medicine.domain.usecase.GetMedicinesUseCase
import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.usecase.*
import com.example.medireminder.features.membermedicine.presentation.state.MemberMedicineFormState
import com.example.medireminder.features.membermedicine.presentation.state.MemberMedicineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberMedicineViewModel @Inject constructor(
    private val getMemberMedicinesUseCase: GetMemberMedicinesUseCase,
    private val getMemberMedicineByIdUseCase: GetMemberMedicineByIdUseCase,
    private val assignMedicineToMemberUseCase: AssignMedicineToMemberUseCase,
    private val updateMemberMedicineUseCase: UpdateMemberMedicineUseCase,
    private val deactivateMemberMedicineUseCase: DeactivateMemberMedicineUseCase,
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase,
    private val getMedicinesUseCase: GetMedicinesUseCase,
    private val getMedicinesByFamilyMemberUseCase: GetMedicinesByFamilyMemberUseCase,
    private val getMembersByMedicineUseCase: GetMembersByMedicineUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberMedicineUiState())
    val uiState: StateFlow<MemberMedicineUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(MemberMedicineFormState())
    val formState: StateFlow<MemberMedicineFormState> = _formState.asStateFlow()

    private var assignmentsJob: Job? = null

    init {
        loadAssignments()
        loadFamilyMembers()
        loadMedicines()
    }

    fun loadAssignments(familyMemberId: String? = null, medicineId: String? = null) {
        assignmentsJob?.cancel()
        assignmentsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val flow = when {
                familyMemberId != null -> getMedicinesByFamilyMemberUseCase(familyMemberId)
                medicineId != null -> getMembersByMedicineUseCase(medicineId)
                else -> getMemberMedicinesUseCase()
            }

            flow.catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { assignments ->
                    _uiState.update { it.copy(isLoading = false, assignments = assignments) }
                }
        }
    }

    private fun loadFamilyMembers() {
        viewModelScope.launch {
            getFamilyMembersUseCase().collect { members ->
                _uiState.update { it.copy(familyMembers = members) }
            }
        }
    }

    private fun loadMedicines() {
        viewModelScope.launch {
            getMedicinesUseCase().collect { medicines ->
                _uiState.update { it.copy(medicines = medicines) }
            }
        }
    }

    fun loadAssignmentDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val assignment = getMemberMedicineByIdUseCase(id)
            if (assignment != null) {
                _uiState.update { it.copy(isLoading = false, selectedAssignment = assignment) }
                _formState.update {
                    it.copy(
                        familyMemberId = assignment.familyMemberId,
                        medicineId = assignment.medicineId,
                        dosageQuantity = assignment.dosageQuantity.toString(),
                        frequencyPerDay = assignment.frequencyPerDay.toString(),
                        dailyTotalQuantity = assignment.dailyTotalQuantity,
                        instructions = assignment.instructions ?: "",
                        startDate = assignment.startDate,
                        endDate = assignment.endDate,
                        isActive = assignment.isActive,
                        autoReduceStock = assignment.autoReduceStock,
                        validationErrors = emptyMap()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Assignment not found") }
            }
        }
    }

    fun onFamilyMemberChange(id: String) = _formState.update { it.copy(familyMemberId = id, validationErrors = it.validationErrors - "familyMemberId") }
    fun onMedicineChange(id: String) = _formState.update { it.copy(medicineId = id, validationErrors = it.validationErrors - "medicineId") }
    
    fun onDosageQuantityChange(quantity: String) {
        _formState.update { 
            val total = (quantity.toDoubleOrNull() ?: 0.0) * (it.frequencyPerDay.toIntOrNull() ?: 0)
            it.copy(dosageQuantity = quantity, dailyTotalQuantity = total, validationErrors = it.validationErrors - "dosageQuantity") 
        }
    }

    fun onFrequencyChange(frequency: String) {
        _formState.update { 
            val total = (it.dosageQuantity.toDoubleOrNull() ?: 0.0) * (frequency.toIntOrNull() ?: 0)
            it.copy(frequencyPerDay = frequency, dailyTotalQuantity = total, validationErrors = it.validationErrors - "frequencyPerDay") 
        }
    }

    fun onInstructionsChange(instructions: String) = _formState.update { it.copy(instructions = instructions) }
    fun onStartDateChange(date: Long?) = _formState.update { it.copy(startDate = date, validationErrors = it.validationErrors - "startDate") }
    fun onEndDateChange(date: Long?) = _formState.update { it.copy(endDate = date, validationErrors = it.validationErrors - "endDate") }
    fun onAutoReduceStockChange(auto: Boolean) = _formState.update { it.copy(autoReduceStock = auto) }

    fun saveAssignment(id: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _formState.value
            val errors = mutableMapOf<String, String>()
            
            if (state.familyMemberId.isBlank()) errors["familyMemberId"] = "Family member is required"
            if (state.medicineId.isBlank()) errors["medicineId"] = "Medicine is required"
            
            val dosage = state.dosageQuantity.toDoubleOrNull()
            if (dosage == null || dosage <= 0) errors["dosageQuantity"] = "Dosage must be greater than 0"
            
            val frequency = state.frequencyPerDay.toIntOrNull()
            if (frequency == null || frequency <= 0) errors["frequencyPerDay"] = "Frequency must be greater than 0"
            
            if (state.startDate == null) errors["startDate"] = "Start date is required"
            if (state.endDate != null && state.startDate != null && state.endDate < state.startDate) {
                errors["endDate"] = "End date cannot be before start date"
            }

            if (errors.isNotEmpty()) {
                _formState.update { it.copy(validationErrors = errors) }
                return@launch
            }

            val result = if (id == null) {
                assignMedicineToMemberUseCase(
                    familyMemberId = state.familyMemberId,
                    medicineId = state.medicineId,
                    dosageQuantity = dosage!!,
                    frequencyPerDay = frequency!!,
                    startDate = state.startDate!!,
                    endDate = state.endDate,
                    instructions = state.instructions.ifBlank { null },
                    autoReduceStock = state.autoReduceStock
                )
            } else {
                val current = getMemberMedicineByIdUseCase(id)
                if (current != null) {
                    updateMemberMedicineUseCase(
                        current.copy(
                            familyMemberId = state.familyMemberId,
                            medicineId = state.medicineId,
                            dosageQuantity = dosage!!,
                            frequencyPerDay = frequency!!,
                            instructions = state.instructions.ifBlank { null },
                            startDate = state.startDate!!,
                            endDate = state.endDate,
                            autoReduceStock = state.autoReduceStock,
                            isActive = state.isActive
                        )
                    )
                } else {
                    Result.failure(Exception("Assignment not found"))
                }
            }

            result.onSuccess {
                _uiState.update { it.copy(successMessage = if (id == null) "Medicine assigned successfully" else "Assignment updated successfully") }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun deactivateAssignment(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            deactivateMemberMedicineUseCase(id).onSuccess {
                _uiState.update { it.copy(successMessage = "Assignment deactivated") }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
