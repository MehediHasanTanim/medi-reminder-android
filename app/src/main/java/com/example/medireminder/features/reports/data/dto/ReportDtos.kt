package com.example.medireminder.features.reports.data.dto

data class StatusCountDto(
    val actionTaken: String,
    val count: Int
)

data class MemberWiseReportDto(
    val memberId: String,
    val memberName: String,
    val actionTaken: String?,
    val count: Int,
    val activeMedicineCount: Int
)

data class MedicineWiseReportDto(
    val medicineId: String,
    val medicineName: String,
    val actionTaken: String?,
    val count: Int,
    val totalConsumed: Double?,
    val unit: String?
)

data class StockUsageReportDto(
    val medicineId: String,
    val medicineName: String,
    val totalConsumed: Double?,
    val totalRefilled: Double?,
    val currentStock: Double?,
    val unit: String?,
    val dailyConsumption: Double?
)

data class LowStockReportDto(
    val medicineId: String,
    val medicineName: String,
    val currentQuantity: Double,
    val lowStockThreshold: Double,
    val unit: String
)

data class ExpiredMedicineReportDto(
    val medicineId: String,
    val medicineName: String,
    val expiryDate: Long,
    val currentQuantity: Double,
    val unit: String
)
