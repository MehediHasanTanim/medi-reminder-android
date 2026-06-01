package com.example.medireminder.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.dashboard.domain.usecase.GetDashboardOverviewUseCase
import com.example.medireminder.features.dashboard.domain.usecase.GetExpiringMedicinesUseCase
import com.example.medireminder.features.dashboard.domain.usecase.GetFamilyOverviewUseCase
import com.example.medireminder.features.dashboard.domain.usecase.GetLowStockDashboardUseCase
import com.example.medireminder.features.dashboard.domain.usecase.GetMedicineDashboardUseCase
import com.example.medireminder.features.dashboard.domain.usecase.GetMissedRemindersUseCase
import com.example.medireminder.features.dashboard.domain.usecase.GetTodayRemindersUseCase
import com.example.medireminder.features.dashboard.domain.usecase.GetUpcomingRemindersUseCase
import com.example.medireminder.features.dashboard.presentation.state.DashboardUiState
import com.example.medireminder.features.dashboard.presentation.state.MedicineDashboardUiState
import com.example.medireminder.features.settings.domain.usecase.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardOverviewUseCase: GetDashboardOverviewUseCase,
    private val getTodayRemindersUseCase: GetTodayRemindersUseCase,
    private val getUpcomingRemindersUseCase: GetUpcomingRemindersUseCase,
    private val getMissedRemindersUseCase: GetMissedRemindersUseCase,
    private val getLowStockDashboardUseCase: GetLowStockDashboardUseCase,
    private val getExpiringMedicinesUseCase: GetExpiringMedicinesUseCase,
    private val getFamilyOverviewUseCase: GetFamilyOverviewUseCase,
    private val getMedicineDashboardUseCase: GetMedicineDashboardUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _medicineDashboardState = MutableStateFlow(MedicineDashboardUiState())
    val medicineDashboardState: StateFlow<MedicineDashboardUiState> = _medicineDashboardState.asStateFlow()

    private val dashboardJobs = mutableListOf<Job>()
    private var medicineDashboardJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        dashboardJobs.forEach { it.cancel() }
        dashboardJobs.clear()
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadOverview()
        loadTodayReminders()
        loadUpcomingReminders()
        loadMissedReminders()
        loadLowStock()
        loadExpiring()
        loadFamilyOverview()
    }

    fun loadMedicineDashboard(medicineId: String) {
        medicineDashboardJob?.cancel()
        medicineDashboardJob = viewModelScope.launch {
            _medicineDashboardState.update { it.copy(isLoading = true, errorMessage = null) }
            getMedicineDashboardUseCase(medicineId)
                .catch { e -> _medicineDashboardState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { dashboard ->
                    _medicineDashboardState.update { it.copy(isLoading = false, dashboard = dashboard) }
                }
        }
    }

    private fun loadOverview() = dashboardJobs.add(viewModelScope.launch {
        getDashboardOverviewUseCase()
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            .collect { overview -> _uiState.update { it.copy(isLoading = false, overview = overview) } }
    })

    private fun loadTodayReminders() = dashboardJobs.add(viewModelScope.launch {
        getTodayRemindersUseCase().collect { value -> _uiState.update { it.copy(todayReminders = value) } }
    })

    private fun loadUpcomingReminders() = dashboardJobs.add(viewModelScope.launch {
        getUpcomingRemindersUseCase().collect { value -> _uiState.update { it.copy(upcomingReminders = value) } }
    })

    private fun loadMissedReminders() = dashboardJobs.add(viewModelScope.launch {
        getMissedRemindersUseCase().collect { value -> _uiState.update { it.copy(missedReminders = value) } }
    })

    private fun loadLowStock() = dashboardJobs.add(viewModelScope.launch {
        getLowStockDashboardUseCase().collect { value -> _uiState.update { it.copy(lowStockMedicines = value) } }
    })

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadExpiring() = dashboardJobs.add(viewModelScope.launch {
        getSettingsUseCase()
            .flatMapLatest { settings -> getExpiringMedicinesUseCase(settings.expiryAlertDays) }
            .collect { value -> _uiState.update { it.copy(expiringMedicines = value) } }
    })

    private fun loadFamilyOverview() = dashboardJobs.add(viewModelScope.launch {
        getFamilyOverviewUseCase().collect { value -> _uiState.update { it.copy(familyMembers = value) } }
    })
}
