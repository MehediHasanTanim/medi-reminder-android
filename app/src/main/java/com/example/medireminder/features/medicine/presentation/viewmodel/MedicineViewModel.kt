package com.example.medireminder.features.medicine.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.usecase.*
import com.example.medireminder.features.medicine.presentation.state.MedicineFormState
import com.example.medireminder.features.medicine.presentation.state.MedicineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val getMedicinesUseCase: GetMedicinesUseCase,
    private val getMedicineByIdUseCase: GetMedicineByIdUseCase,
    private val addMedicineUseCase: AddMedicineUseCase,
    private val updateMedicineUseCase: UpdateMedicineUseCase,
    private val deleteMedicineUseCase: DeleteMedicineUseCase,
    private val toggleActiveStatusUseCase: ToggleMedicineActiveStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineUiState())
    val uiState: StateFlow<MedicineUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(MedicineFormState())
    val formState: StateFlow<MedicineFormState> = _formState.asStateFlow()

    init {
        loadMedicines()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadMedicines()
    }

    private fun loadMedicines() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getMedicinesUseCase()
                .map { list ->
                    val query = _uiState.value.searchQuery
                    if (query.isBlank()) list
                    else list.filter { 
                        it.name.contains(query, ignoreCase = true) || 
                        it.genericName?.contains(query, ignoreCase = true) == true 
                    }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { medicines ->
                    _uiState.update { it.copy(isLoading = false, medicines = medicines) }
                }
        }
    }

    fun loadMedicineDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val medicine = getMedicineByIdUseCase(id)
            if (medicine != null) {
                _uiState.update { it.copy(isLoading = false, selectedMedicine = medicine) }
                _formState.update { 
                    it.copy(
                        name = medicine.name,
                        genericName = medicine.genericName ?: "",
                        medicineType = medicine.medicineType,
                        strength = medicine.strength ?: "",
                        unit = medicine.unit,
                        manufacturer = medicine.manufacturer ?: "",
                        notes = medicine.notes ?: "",
                        isActive = medicine.isActive,
                        validationErrors = emptyMap()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Medicine not found") }
            }
        }
    }

    fun onNameChange(name: String) = _formState.update { it.copy(name = name, validationErrors = it.validationErrors - "name") }
    fun onGenericNameChange(name: String) = _formState.update { it.copy(genericName = name) }
    fun onTypeChange(type: String) = _formState.update { it.copy(medicineType = type, validationErrors = it.validationErrors - "type") }
    fun onStrengthChange(strength: String) = _formState.update { it.copy(strength = strength) }
    fun onUnitChange(unit: String) = _formState.update { it.copy(unit = unit, validationErrors = it.validationErrors - "unit") }
    fun onManufacturerChange(mfr: String) = _formState.update { it.copy(manufacturer = mfr) }
    fun onNotesChange(notes: String) = _formState.update { it.copy(notes = notes) }
    fun onActiveStatusChange(active: Boolean) = _formState.update { it.copy(isActive = active) }

    fun saveMedicine(id: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _formState.value
            
            // Simple pre-validation for field errors UI
            val errors = mutableMapOf<String, String>()
            if (state.name.isBlank()) errors["name"] = "Medicine name is required"
            if (state.medicineType.isBlank()) errors["type"] = "Medicine type is required"
            if (state.unit.isBlank()) errors["unit"] = "Unit is required"
            
            if (errors.isNotEmpty()) {
                _formState.update { it.copy(validationErrors = errors) }
                return@launch
            }

            val result = if (id == null) {
                addMedicineUseCase(
                    name = state.name,
                    genericName = state.genericName.ifBlank { null },
                    medicineType = state.medicineType,
                    strength = state.strength.ifBlank { null },
                    unit = state.unit,
                    manufacturer = state.manufacturer.ifBlank { null },
                    notes = state.notes.ifBlank { null }
                )
            } else {
                updateMedicineUseCase(
                    id = id,
                    name = state.name,
                    genericName = state.genericName.ifBlank { null },
                    medicineType = state.medicineType,
                    strength = state.strength.ifBlank { null },
                    unit = state.unit,
                    manufacturer = state.manufacturer.ifBlank { null },
                    notes = state.notes.ifBlank { null },
                    isActive = state.isActive
                )
            }

            result.onSuccess {
                _uiState.update { it.copy(successMessage = if (id == null) "Medicine added" else "Medicine updated") }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun deleteMedicine(medicine: Medicine, onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteMedicineUseCase(medicine)
            _uiState.update { it.copy(successMessage = "Medicine deactivated") }
            onSuccess()
        }
    }

    fun toggleActiveStatus(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            toggleActiveStatusUseCase(id, !currentStatus)
            loadMedicineDetails(id)
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
