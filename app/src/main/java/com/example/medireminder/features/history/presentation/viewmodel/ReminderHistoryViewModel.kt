package com.example.medireminder.features.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.family.domain.usecase.GetFamilyMembersUseCase
import com.example.medireminder.features.history.domain.model.ReminderHistoryStatus
import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import com.example.medireminder.features.history.domain.usecase.GetReminderHistorySummaryUseCase
import com.example.medireminder.features.history.presentation.state.ReminderHistoryFilterState
import com.example.medireminder.features.history.presentation.state.ReminderHistoryUiState
import com.example.medireminder.features.medicine.domain.usecase.GetMedicinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReminderHistoryViewModel @Inject constructor(
    private val repository: ReminderHistoryRepository,
    private val getSummaryUseCase: GetReminderHistorySummaryUseCase,
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase,
    private val getMedicinesUseCase: GetMedicinesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderHistoryUiState())
    val uiState: StateFlow<ReminderHistoryUiState> = _uiState.asStateFlow()

    private val _filterState = MutableStateFlow(todayFilter())
    val filterState: StateFlow<ReminderHistoryFilterState> = _filterState.asStateFlow()

    private var historyJob: Job? = null

    init {
        loadFilterOptions()
        applyFilters()
    }

    fun applyFilters() {
        val filter = _filterState.value
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.observeHistoryByFilters(
                memberId = filter.selectedMemberId,
                medicineId = filter.selectedMedicineId,
                status = filter.selectedStatus,
                startTime = filter.startDate,
                endTime = filter.endDate
            ).catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }.collect { items ->
                _uiState.update { it.copy(isLoading = false, historyItems = items) }
            }
        }
        loadSummary()
    }

    fun loadHistoryDetails(historyId: String) {
        viewModelScope.launch {
            val history = repository.getHistoryById(historyId)
            _uiState.update {
                it.copy(
                    selectedHistory = history,
                    errorMessage = if (history == null) "History item not found" else null
                )
            }
        }
    }

    fun updateMemberFilter(memberId: String?) {
        _filterState.update { it.copy(selectedMemberId = memberId) }
    }

    fun updateMedicineFilter(medicineId: String?) {
        _filterState.update { it.copy(selectedMedicineId = medicineId) }
    }

    fun updateStatusFilter(status: ReminderHistoryStatus?) {
        _filterState.update { it.copy(selectedStatus = status) }
    }

    fun selectPreset(preset: String) {
        _filterState.value = when (preset) {
            "Yesterday" -> rangeFilter(preset, daysAgoStart = 1, daysAgoEnd = 1)
            "Last 7 days" -> rangeFilter(preset, daysAgoStart = 6, daysAgoEnd = 0)
            "Last 30 days" -> rangeFilter(preset, daysAgoStart = 29, daysAgoEnd = 0)
            else -> todayFilter()
        }.copy(
            selectedMemberId = _filterState.value.selectedMemberId,
            selectedMedicineId = _filterState.value.selectedMedicineId,
            selectedStatus = _filterState.value.selectedStatus
        )
    }

    fun clearFilters() {
        _filterState.value = todayFilter()
        applyFilters()
    }

    private fun loadSummary() {
        viewModelScope.launch {
            val filter = _filterState.value
            val start = filter.startDate ?: startOfDay(System.currentTimeMillis())
            val end = filter.endDate ?: endOfDay(System.currentTimeMillis())
            _uiState.update { it.copy(summary = getSummaryUseCase(start, end)) }
        }
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            getFamilyMembersUseCase().collect { members ->
                _uiState.update { it.copy(familyMembers = members) }
            }
        }
        viewModelScope.launch {
            getMedicinesUseCase().collect { medicines ->
                _uiState.update { it.copy(medicines = medicines) }
            }
        }
    }

    private fun todayFilter(): ReminderHistoryFilterState {
        val now = System.currentTimeMillis()
        return ReminderHistoryFilterState(
            startDate = startOfDay(now),
            endDate = endOfDay(now),
            selectedPreset = "Today"
        )
    }

    private fun rangeFilter(preset: String, daysAgoStart: Int, daysAgoEnd: Int): ReminderHistoryFilterState {
        val now = System.currentTimeMillis()
        return ReminderHistoryFilterState(
            startDate = startOfDay(now, daysAgoStart),
            endDate = endOfDay(now, daysAgoEnd),
            selectedPreset = preset
        )
    }

    private fun startOfDay(time: Long, daysAgo: Int = 0): Long {
        return Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.DAY_OF_YEAR, -daysAgo)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun endOfDay(time: Long, daysAgo: Int = 0): Long {
        return Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.DAY_OF_YEAR, -daysAgo)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}
