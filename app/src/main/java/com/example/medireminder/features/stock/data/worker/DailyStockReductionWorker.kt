package com.example.medireminder.features.stock.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.repository.SettingsRepository
import com.example.medireminder.features.stock.domain.model.StockTransactionType
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import com.example.medireminder.features.stock.domain.usecase.CalculateDailyConsumptionUseCase
import com.example.medireminder.features.stock.domain.usecase.ReduceStockUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyStockReductionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MedicineStockRepository,
    private val calculateDailyConsumptionUseCase: CalculateDailyConsumptionUseCase,
    private val reduceStockUseCase: ReduceStockUseCase,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val settings = settingsRepository.observeSettings().first()
            if (settings.stockReductionMode != StockReductionMode.AUTO_DAILY_REDUCTION) {
                return Result.success()
            }

            val stocks = repository.observeAllStock().first()
            
            stocks.filter { it.autoReduceEnabled }.forEach { stock ->
                val dailyConsumption = calculateDailyConsumptionUseCase(stock.medicineId)
                if (dailyConsumption > 0) {
                    reduceStockUseCase(
                        medicineId = stock.medicineId,
                        quantity = dailyConsumption,
                        reason = "Auto daily reduction",
                        transactionType = StockTransactionType.AUTO_DAILY_REDUCTION
                    )
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
