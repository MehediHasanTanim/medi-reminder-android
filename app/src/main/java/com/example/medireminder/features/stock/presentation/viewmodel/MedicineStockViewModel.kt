package com.example.medireminder.features.stock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medireminder.features.settings.domain.usecase.GetSettingsUseCase
import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.usecase.*
import com.example.medireminder.features.stock.presentation.state.MedicineStockUiState
import com.example.medireminder.features.stock.presentation.state.RefillStockFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineStockViewModel @Inject constructor(
    private val getMedicineStockUseCase: GetMedicineStockUseCase,
    private val getLowStockMedicinesUseCase: GetLowStockMedicinesUseCase,
    private val refillStockUseCase: RefillStockUseCase,
    private val adjustStockUseCase: AdjustStockUseCase,
    private val addStockUseCase: AddStockUseCase,
    private val calculateDailyConsumptionUseCase: CalculateDailyConsumptionUseCase,
    private val getEstimatedRemainingDaysUseCase: GetEstimatedRemainingDaysUseCase,
    private val getStockTransactionsUseCase: GetStockTransactionsUseCase,
    private val getExpiringMedicinesUseCase: GetExpiringMedicinesUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineStockUiState())
    val uiState: StateFlow<MedicineStockUiState> = _uiState.asStateFlow()

    private val _refillFormState = MutableStateFlow(RefillStockFormState())
    val refillFormState: StateFlow<RefillStockFormState> = _refillFormState.asStateFlow()

    init {
        loadAllStocks()
        loadLowStockMedicines()
        loadExpiringMedicines()
    }

    fun loadAllStocks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getMedicineStockUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { stocks ->
                    val enrichedStocks = stocks.map { stock ->
                        val dailyConsumption = calculateDailyConsumptionUseCase(stock.medicineId)
                        val remainingDays = getEstimatedRemainingDaysUseCase(stock.currentQuantity, dailyConsumption)
                        stock.copy(
                            dailyConsumption = dailyConsumption,
                            estimatedRemainingDays = remainingDays,
                            isLowStock = stock.currentQuantity <= stock.lowStockThreshold,
                            isExpired = stock.expiryDate?.let { it < System.currentTimeMillis() } ?: false
                        )
                    }
                    _uiState.update { it.copy(isLoading = false, stocks = enrichedStocks) }
                }
        }
    }

    private fun loadLowStockMedicines() {
        viewModelScope.launch {
            getLowStockMedicinesUseCase().collect { stocks ->
                _uiState.update { it.copy(lowStockMedicines = enrichStocks(stocks)) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadExpiringMedicines() {
        viewModelScope.launch {
            getSettingsUseCase().flatMapLatest { settings ->
                getExpiringMedicinesUseCase(settings.expiryAlertDays)
            }.collect { stocks ->
                _uiState.update { it.copy(expiringMedicines = enrichStocks(stocks)) }
            }
        }
    }

    fun loadStockDetails(medicineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val stock = getMedicineStockUseCase.getById(medicineId)
            if (stock != null) {
                val dailyConsumption = calculateDailyConsumptionUseCase(medicineId)
                val remainingDays = getEstimatedRemainingDaysUseCase(stock.currentQuantity, dailyConsumption)
                val enrichedStock = stock.copy(
                    dailyConsumption = dailyConsumption,
                    estimatedRemainingDays = remainingDays,
                    isLowStock = stock.currentQuantity <= stock.lowStockThreshold,
                    isExpired = stock.expiryDate?.let { it < System.currentTimeMillis() } ?: false
                )
                _uiState.update { it.copy(isLoading = false, selectedStock = enrichedStock) }
                loadTransactions(medicineId)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Stock record not found") }
            }
        }
    }

    fun loadTransactions(medicineId: String) {
        viewModelScope.launch {
            getStockTransactionsUseCase(medicineId).collect { transactions ->
                _uiState.update { it.copy(transactions = transactions) }
            }
        }
    }

    fun onRefillQuantityChange(quantity: String) {
        _refillFormState.update { it.copy(quantity = quantity, validationErrors = it.validationErrors - "quantity") }
    }

    fun onRefillReasonChange(reason: String) {
        _refillFormState.update { it.copy(reason = reason) }
    }

    fun refillStock(medicineId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val quantity = _refillFormState.value.quantity.toDoubleOrNull()
            if (quantity == null || quantity <= 0) {
                _refillFormState.update { it.copy(validationErrors = mapOf("quantity" to "Invalid quantity")) }
                return@launch
            }

            refillStockUseCase(medicineId, quantity, _refillFormState.value.reason.ifBlank { null })
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Stock refilled successfully") }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    fun addStock(
        medicineId: String,
        initialQuantity: Double,
        unit: String,
        lowStockThreshold: Double,
        expiryDate: Long?,
        autoReduceEnabled: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            addStockUseCase(
                medicineId = medicineId,
                initialQuantity = initialQuantity,
                unit = unit,
                lowStockThreshold = lowStockThreshold,
                expiryDate = expiryDate,
                autoReduceEnabled = autoReduceEnabled
            ).onSuccess {
                _uiState.update { it.copy(successMessage = "Stock record created") }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun adjustStock(medicineId: String, adjustment: Double, reason: String) {
        viewModelScope.launch {
            adjustStockUseCase(medicineId, adjustment, reason)
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Stock adjusted successfully") }
                    loadStockDetails(medicineId)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private suspend fun enrichStocks(stocks: List<MedicineStock>): List<MedicineStock> {
        return stocks.map { stock ->
            val dailyConsumption = calculateDailyConsumptionUseCase(stock.medicineId)
            val remainingDays = getEstimatedRemainingDaysUseCase(stock.currentQuantity, dailyConsumption)
            stock.copy(
                dailyConsumption = dailyConsumption,
                estimatedRemainingDays = remainingDays,
                isLowStock = stock.currentQuantity <= stock.lowStockThreshold,
                isExpired = stock.expiryDate?.let { it < System.currentTimeMillis() } ?: false
            )
        }
    }
}
