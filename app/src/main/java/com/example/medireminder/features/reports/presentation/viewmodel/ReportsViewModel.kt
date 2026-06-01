package com.example.medireminder.features.reports.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.reports.domain.usecase.GetDailyReminderReportUseCase
import com.example.medireminder.features.reports.domain.usecase.GetExpiredMedicineReportUseCase
import com.example.medireminder.features.reports.domain.usecase.GetLowStockReportUseCase
import com.example.medireminder.features.reports.domain.usecase.GetMedicineWiseReportUseCase
import com.example.medireminder.features.reports.domain.usecase.GetMemberWiseReportUseCase
import com.example.medireminder.features.reports.domain.usecase.GetMonthlyComplianceReportUseCase
import com.example.medireminder.features.reports.domain.usecase.GetStockUsageReportUseCase
import com.example.medireminder.features.reports.domain.usecase.GetWeeklyComplianceReportUseCase
import com.example.medireminder.features.reports.presentation.state.ReportFilterState
import com.example.medireminder.features.reports.presentation.state.ReportsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getDailyReminderReportUseCase: GetDailyReminderReportUseCase,
    private val getWeeklyComplianceReportUseCase: GetWeeklyComplianceReportUseCase,
    private val getMonthlyComplianceReportUseCase: GetMonthlyComplianceReportUseCase,
    private val getMemberWiseReportUseCase: GetMemberWiseReportUseCase,
    private val getMedicineWiseReportUseCase: GetMedicineWiseReportUseCase,
    private val getStockUsageReportUseCase: GetStockUsageReportUseCase,
    private val getLowStockReportUseCase: GetLowStockReportUseCase,
    private val getExpiredMedicineReportUseCase: GetExpiredMedicineReportUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _filterState = MutableStateFlow(ReportFilterState())
    val filterState: StateFlow<ReportFilterState> = _filterState.asStateFlow()

    private var reportJob: Job? = null

    fun selectReport(type: String) {
        _filterState.update { it.copy(selectedReportType = type) }
        refresh()
    }

    fun updateDateRange(startDate: Long?, endDate: Long?) {
        _filterState.update { it.copy(startDate = startDate, endDate = endDate) }
        refresh()
    }

    fun refresh() {
        when (_filterState.value.selectedReportType) {
            "weekly" -> loadWeekly()
            "monthly" -> loadMonthly()
            "member-wise" -> loadMemberWise()
            "medicine-wise" -> loadMedicineWise()
            "stock-usage" -> loadStockUsage()
            "low-stock" -> loadLowStock()
            "expired" -> loadExpired()
            else -> loadDaily()
        }
    }

    fun loadDaily(date: Long = System.currentTimeMillis()) = collectReport {
        getDailyReminderReportUseCase(date).collect { report ->
            _uiState.update { it.copy(isLoading = false, dailyReport = report) }
        }
    }

    fun loadWeekly(date: Long = System.currentTimeMillis()) = collectReport {
        getWeeklyComplianceReportUseCase(date).collect { report ->
            _uiState.update { it.copy(isLoading = false, weeklyReport = report) }
        }
    }

    fun loadMonthly(date: Long = System.currentTimeMillis()) = collectReport {
        getMonthlyComplianceReportUseCase(date).collect { report ->
            _uiState.update { it.copy(isLoading = false, monthlyReport = report) }
        }
    }

    fun loadMemberWise() = collectReport {
        getMemberWiseReportUseCase().collect { report ->
            _uiState.update { it.copy(isLoading = false, memberWiseReports = report) }
        }
    }

    fun loadMedicineWise() = collectReport {
        getMedicineWiseReportUseCase().collect { report ->
            _uiState.update { it.copy(isLoading = false, medicineWiseReports = report) }
        }
    }

    fun loadStockUsage() = collectReport {
        getStockUsageReportUseCase().collect { report ->
            _uiState.update { it.copy(isLoading = false, stockUsageReports = report) }
        }
    }

    fun loadLowStock() = collectReport {
        getLowStockReportUseCase().collect { report ->
            _uiState.update { it.copy(isLoading = false, lowStockReports = report) }
        }
    }

    fun loadExpired() = collectReport {
        getExpiredMedicineReportUseCase().collect { report ->
            _uiState.update { it.copy(isLoading = false, expiredMedicineReports = report) }
        }
    }

    private fun collectReport(block: suspend () -> Unit) {
        reportJob?.cancel()
        reportJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { block() }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
