package com.example.medireminder.features.stock.presentation.state

data class RefillStockFormState(
    val quantity: String = "",
    val reason: String = "",
    val validationErrors: Map<String, String> = emptyMap()
)
