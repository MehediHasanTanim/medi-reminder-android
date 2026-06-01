package com.example.medireminder.features.stock.domain.model

enum class StockTransactionType {
    INITIAL_STOCK,
    REFILL,
    DOSE_TAKEN,
    MANUAL_ADJUSTMENT,
    AUTO_DAILY_REDUCTION,
    EXPIRED_REMOVAL,
    WASTED
}
