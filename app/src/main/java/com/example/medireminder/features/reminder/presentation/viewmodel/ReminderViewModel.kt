package com.example.medireminder.features.reminder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.membermedicine.domain.usecase.GetMemberMedicinesUseCase
import com.example.medireminder.features.reminder.domain.model.Reminder
import com.example.medireminder.features.reminder.domain.model.ReminderRepeatType
import com.example.medireminder.features.reminder.domain.usecase.*
import com.example.medireminder.features.reminder.presentation.state.ReminderFormState
import com.example.medireminder.features.reminder.presentation.state.ReminderUiState
import com.example.medireminder.features.settings.domain.usecase.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val getReminderByIdUseCase: GetReminderByIdUseCase,
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val markReminderTakenUseCase: MarkReminderTakenUseCase,
    private val skipReminderUseCase: SkipReminderUseCase,
    private val snoozeReminderUseCase: SnoozeReminderUseCase,
    private val getMemberMedicinesUseCase: GetMemberMedicinesUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ReminderFormState())
    val formState: StateFlow<ReminderFormState> = _formState.asStateFlow()

    init {
        loadReminderDefaults()
        loadReminders()
        loadMemberMedicines()
    }

    private fun loadReminderDefaults() {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _formState.update { state ->
                    if (state.reminderTime.isNotBlank() || _uiState.value.selectedReminder != null) {
                        state
                    } else {
                        state.copy(
                            vibrationEnabled = settings.defaultVibrationEnabled,
                            snoozeDuration = settings.defaultSnoozeDurationMinutes.toString()
                        )
                    }
                }
            }
        }
    }

    fun loadReminders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getRemindersUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { reminders ->
                    _uiState.update { it.copy(isLoading = false, reminders = reminders) }
                }
        }
    }

    private fun loadMemberMedicines() {
        viewModelScope.launch {
            getMemberMedicinesUseCase(onlyActive = true).collect { assignments ->
                _uiState.update { it.copy(memberMedicines = assignments) }
            }
        }
    }

    fun loadReminderDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val reminder = getReminderByIdUseCase(id)
            if (reminder != null) {
                _uiState.update { it.copy(isLoading = false, selectedReminder = reminder) }
                _formState.update {
                    it.copy(
                        memberMedicineId = reminder.memberMedicineId,
                        reminderTime = reminder.reminderTime,
                        repeatType = reminder.repeatType,
                        repeatDays = reminder.repeatDays,
                        startDate = reminder.startDate,
                        endDate = reminder.endDate,
                        alarmTone = reminder.alarmTone,
                        vibrationEnabled = reminder.vibrationEnabled,
                        notificationEnabled = reminder.notificationEnabled,
                        snoozeEnabled = reminder.snoozeEnabled,
                        snoozeDuration = reminder.snoozeDuration.toString(),
                        isActive = reminder.isActive,
                        validationErrors = emptyMap()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Reminder not found") }
            }
        }
    }

    fun onMemberMedicineChange(id: String) = _formState.update { it.copy(memberMedicineId = id, validationErrors = it.validationErrors - "memberMedicineId") }
    fun onReminderTimeChange(time: String) = _formState.update { it.copy(reminderTime = time, validationErrors = it.validationErrors - "reminderTime") }
    fun onRepeatTypeChange(type: ReminderRepeatType) = _formState.update { it.copy(repeatType = type) }
    
    fun onRepeatDaysChange(day: Int) {
        _formState.update { state ->
            val newDays = if (state.repeatDays.contains(day)) {
                state.repeatDays - day
            } else {
                state.repeatDays + day
            }
            state.copy(repeatDays = newDays, validationErrors = state.validationErrors - "repeatDays")
        }
    }

    fun onStartDateChange(date: Long?) = _formState.update { it.copy(startDate = date, validationErrors = it.validationErrors - "startDate") }
    fun onEndDateChange(date: Long?) = _formState.update { it.copy(endDate = date, validationErrors = it.validationErrors - "endDate") }
    fun onVibrationChange(enabled: Boolean) = _formState.update { it.copy(vibrationEnabled = enabled) }
    fun onNotificationChange(enabled: Boolean) = _formState.update { it.copy(notificationEnabled = enabled) }
    fun onSnoozeChange(enabled: Boolean) = _formState.update { it.copy(snoozeEnabled = enabled) }
    fun onSnoozeDurationChange(duration: String) = _formState.update { it.copy(snoozeDuration = duration, validationErrors = it.validationErrors - "snoozeDuration") }

    fun saveReminder(id: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _formState.value
            val errors = mutableMapOf<String, String>()

            if (state.memberMedicineId.isBlank()) errors["memberMedicineId"] = "Medicine assignment is required"
            if (state.reminderTime.isBlank()) errors["reminderTime"] = "Reminder time is required"
            if (state.repeatType == ReminderRepeatType.SPECIFIC_WEEKDAYS && state.repeatDays.isEmpty()) {
                errors["repeatDays"] = "At least one day is required"
            }
            if (state.startDate == null) errors["startDate"] = "Start date is required"
            if (state.endDate != null && state.startDate != null && state.endDate < state.startDate) {
                errors["endDate"] = "End date cannot be before start date"
            }
            val snoozeDur = state.snoozeDuration.toIntOrNull()
            if (state.snoozeEnabled && (snoozeDur == null || snoozeDur <= 0)) {
                errors["snoozeDuration"] = "Invalid duration"
            }

            if (errors.isNotEmpty()) {
                _formState.update { it.copy(validationErrors = errors) }
                return@launch
            }

            val result = if (id == null) {
                scheduleReminderUseCase(
                    memberMedicineId = state.memberMedicineId,
                    reminderTime = state.reminderTime,
                    repeatType = state.repeatType,
                    repeatDays = state.repeatDays,
                    startDate = state.startDate!!,
                    endDate = state.endDate,
                    vibrationEnabled = state.vibrationEnabled,
                    notificationEnabled = state.notificationEnabled,
                    snoozeEnabled = state.snoozeEnabled,
                    snoozeDuration = snoozeDur ?: 5
                )
            } else {
                val current = getReminderByIdUseCase(id)
                if (current != null) {
                    updateReminderUseCase(
                        current.copy(
                            memberMedicineId = state.memberMedicineId,
                            reminderTime = state.reminderTime,
                            repeatType = state.repeatType,
                            repeatDays = state.repeatDays,
                            startDate = state.startDate!!,
                            endDate = state.endDate,
                            vibrationEnabled = state.vibrationEnabled,
                            notificationEnabled = state.notificationEnabled,
                            snoozeEnabled = state.snoozeEnabled,
                            snoozeDuration = snoozeDur ?: 5,
                            isActive = state.isActive
                        )
                    )
                } else {
                    Result.failure(Exception("Reminder not found"))
                }
            }

            result.onSuccess {
                _uiState.update { it.copy(successMessage = if (id == null) "Reminder scheduled" else "Reminder updated") }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun cancelReminder(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            cancelReminderUseCase(id).onSuccess {
                _uiState.update { it.copy(successMessage = "Reminder cancelled") }
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
