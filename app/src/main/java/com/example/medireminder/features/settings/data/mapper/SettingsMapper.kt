package com.example.medireminder.features.settings.data.mapper

import com.example.medireminder.features.settings.domain.model.AppThemeMode
import com.example.medireminder.features.settings.domain.model.StockReductionMode
import com.example.medireminder.features.settings.domain.model.TimeFormat

fun AppThemeMode.label(): String = when (this) {
    AppThemeMode.SYSTEM -> "System"
    AppThemeMode.LIGHT -> "Light"
    AppThemeMode.DARK -> "Dark"
}

fun TimeFormat.label(): String = when (this) {
    TimeFormat.HOUR_12 -> "12-hour"
    TimeFormat.HOUR_24 -> "24-hour"
}

fun StockReductionMode.label(): String = when (this) {
    StockReductionMode.REDUCE_ON_TAKEN -> "Reduce on Taken"
    StockReductionMode.AUTO_DAILY_REDUCTION -> "Auto Daily Reduction"
}
