package com.example.medireminder.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.medireminder.features.reports.data.dto.ExpiredMedicineReportDto
import com.example.medireminder.features.reports.data.dto.LowStockReportDto
import com.example.medireminder.features.reports.data.dto.MedicineWiseReportDto
import com.example.medireminder.features.reports.data.dto.MemberWiseReportDto
import com.example.medireminder.features.reports.data.dto.StatusCountDto
import com.example.medireminder.features.reports.data.dto.StockUsageReportDto
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportsDao {
    @Query(
        """
        SELECT actionTaken, COUNT(*) as count
        FROM reminder_logs
        WHERE scheduledTime BETWEEN :startTime AND :endTime
        GROUP BY actionTaken
        """
    )
    fun getReminderStatusCounts(startTime: Long, endTime: Long): Flow<List<StatusCountDto>>

    @Query(
        """
        SELECT l.memberId, COALESCE(fm.fullName, 'Unknown') as memberName,
            l.actionTaken, COUNT(l.id) as count,
            (
                SELECT COUNT(*) FROM member_medicines mm
                WHERE mm.familyMemberId = l.memberId AND mm.isActive = 1
            ) as activeMedicineCount
        FROM reminder_logs l
        LEFT JOIN family_members fm ON l.memberId = fm.id
        WHERE l.scheduledTime BETWEEN :startTime AND :endTime
        GROUP BY l.memberId, l.actionTaken
        """
    )
    fun getMemberWiseReport(startTime: Long, endTime: Long): Flow<List<MemberWiseReportDto>>

    @Query(
        """
        SELECT l.medicineId, COALESCE(m.name, 'Unknown') as medicineName,
            l.actionTaken, COUNT(l.id) as count,
            (
                SELECT SUM(st.quantity) FROM stock_transactions st
                WHERE st.medicineId = l.medicineId
                AND st.transactionType = 'DOSE_TAKEN'
                AND st.createdAt BETWEEN :startTime AND :endTime
            ) as totalConsumed,
            m.unit as unit
        FROM reminder_logs l
        LEFT JOIN medicines m ON l.medicineId = m.id
        WHERE l.scheduledTime BETWEEN :startTime AND :endTime
        GROUP BY l.medicineId, l.actionTaken
        """
    )
    fun getMedicineWiseReport(startTime: Long, endTime: Long): Flow<List<MedicineWiseReportDto>>

    @Query(
        """
        SELECT s.medicineId, COALESCE(m.name, 'Unknown') as medicineName,
            SUM(CASE WHEN t.transactionType = 'DOSE_TAKEN' THEN t.quantity ELSE 0 END) as totalConsumed,
            SUM(CASE WHEN t.transactionType = 'REFILL' THEN t.quantity ELSE 0 END) as totalRefilled,
            s.currentQuantity as currentStock,
            s.unit as unit,
            (
                SELECT SUM(mm.dailyTotalQuantity) FROM member_medicines mm
                WHERE mm.medicineId = s.medicineId AND mm.isActive = 1
            ) as dailyConsumption
        FROM medicine_stock s
        LEFT JOIN medicines m ON s.medicineId = m.id
        LEFT JOIN stock_transactions t ON s.medicineId = t.medicineId
            AND t.createdAt BETWEEN :startTime AND :endTime
        GROUP BY s.medicineId
        """
    )
    fun getStockUsageReport(startTime: Long, endTime: Long): Flow<List<StockUsageReportDto>>

    @Query(
        """
        SELECT s.medicineId, COALESCE(m.name, 'Unknown') as medicineName,
            s.currentQuantity, s.lowStockThreshold, s.unit
        FROM medicine_stock s
        LEFT JOIN medicines m ON s.medicineId = m.id
        WHERE s.currentQuantity <= s.lowStockThreshold
        ORDER BY s.currentQuantity ASC
        """
    )
    fun getLowStockReport(): Flow<List<LowStockReportDto>>

    @Query(
        """
        SELECT s.medicineId, COALESCE(m.name, 'Unknown') as medicineName,
            s.expiryDate as expiryDate, s.currentQuantity, s.unit
        FROM medicine_stock s
        LEFT JOIN medicines m ON s.medicineId = m.id
        WHERE s.expiryDate IS NOT NULL AND s.expiryDate < :currentTime
        ORDER BY s.expiryDate ASC
        """
    )
    fun getExpiredMedicineReport(currentTime: Long): Flow<List<ExpiredMedicineReportDto>>
}
