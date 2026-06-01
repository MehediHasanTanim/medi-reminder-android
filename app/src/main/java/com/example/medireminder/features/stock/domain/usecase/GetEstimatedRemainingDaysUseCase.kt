package com.example.medireminder.features.stock.domain.usecase

import javax.inject.Inject
import kotlin.math.roundToInt

class GetEstimatedRemainingDaysUseCase @Inject constructor() {
    operator fun invoke(currentStock: Double, dailyConsumption: Double): Double? {
        if (dailyConsumption <= 0) return null
        val days = currentStock / dailyConsumption
        return (days * 10).roundToInt() / 10.0
    }
}
