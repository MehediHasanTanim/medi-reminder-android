package com.example.medireminder.core.database.dao

import androidx.room.*
import com.example.medireminder.core.database.entities.ReminderLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ReminderLogEntity)

    @Update
    suspend fun update(log: ReminderLogEntity)

    @Delete
    suspend fun delete(log: ReminderLogEntity)

    @Query("SELECT * FROM reminder_logs WHERE id = :id")
    suspend fun getById(id: String): ReminderLogEntity?

    suspend fun getLogById(id: String): ReminderLogEntity? = getById(id)

    @Query("SELECT * FROM reminder_logs ORDER BY scheduledTime DESC")
    fun getAll(): Flow<List<ReminderLogEntity>>

    fun observeAllLogs(): Flow<List<ReminderLogEntity>> = getAll()

    @Query("SELECT * FROM reminder_logs WHERE memberId = :memberId")
    fun observeLogsByMember(memberId: String): Flow<List<ReminderLogEntity>>

    @Query("SELECT * FROM reminder_logs WHERE medicineId = :medicineId")
    fun observeLogsByMedicine(medicineId: String): Flow<List<ReminderLogEntity>>

    @Query("SELECT * FROM reminder_logs WHERE actionTaken = :status ORDER BY scheduledTime DESC")
    fun observeLogsByStatus(status: String): Flow<List<ReminderLogEntity>>

    @Query("SELECT * FROM reminder_logs WHERE scheduledTime BETWEEN :startTime AND :endTime ORDER BY scheduledTime DESC")
    fun observeLogsByDateRange(startTime: Long, endTime: Long): Flow<List<ReminderLogEntity>>

    @Query(
        """
        SELECT * FROM reminder_logs
        WHERE (:memberId IS NULL OR memberId = :memberId)
        AND (:medicineId IS NULL OR medicineId = :medicineId)
        AND (:status IS NULL OR actionTaken = :status)
        AND (:startTime IS NULL OR scheduledTime >= :startTime)
        AND (:endTime IS NULL OR scheduledTime <= :endTime)
        ORDER BY scheduledTime DESC
        """
    )
    fun observeLogsByFilters(
        memberId: String?,
        medicineId: String?,
        status: String?,
        startTime: Long?,
        endTime: Long?
    ): Flow<List<ReminderLogEntity>>

    @Transaction
    @Query(
        """
        SELECT l.id as logId, l.reminderId, l.memberId, fm.fullName as memberName,
            l.medicineId, m.name as medicineName, m.strength as medicineStrength,
            l.scheduledTime, l.actionTaken, l.actionTime, l.notes, l.createdAt
        FROM reminder_logs l
        LEFT JOIN family_members fm ON l.memberId = fm.id
        LEFT JOIN medicines m ON l.medicineId = m.id
        ORDER BY l.scheduledTime DESC
        """
    )
    fun observeHistoryWithDetails(): Flow<List<ReminderHistoryWithDetails>>

    @Transaction
    @Query(
        """
        SELECT l.id as logId, l.reminderId, l.memberId, fm.fullName as memberName,
            l.medicineId, m.name as medicineName, m.strength as medicineStrength,
            l.scheduledTime, l.actionTaken, l.actionTime, l.notes, l.createdAt
        FROM reminder_logs l
        LEFT JOIN family_members fm ON l.memberId = fm.id
        LEFT JOIN medicines m ON l.medicineId = m.id
        WHERE (:memberId IS NULL OR l.memberId = :memberId)
        AND (:medicineId IS NULL OR l.medicineId = :medicineId)
        AND (:status IS NULL OR l.actionTaken = :status)
        AND (:startTime IS NULL OR l.scheduledTime >= :startTime)
        AND (:endTime IS NULL OR l.scheduledTime <= :endTime)
        ORDER BY l.scheduledTime DESC
        """
    )
    fun observeHistoryWithDetailsByFilters(
        memberId: String?,
        medicineId: String?,
        status: String?,
        startTime: Long?,
        endTime: Long?
    ): Flow<List<ReminderHistoryWithDetails>>

    @Transaction
    @Query(
        """
        SELECT l.id as logId, l.reminderId, l.memberId, fm.fullName as memberName,
            l.medicineId, m.name as medicineName, m.strength as medicineStrength,
            l.scheduledTime, l.actionTaken, l.actionTime, l.notes, l.createdAt
        FROM reminder_logs l
        LEFT JOIN family_members fm ON l.memberId = fm.id
        LEFT JOIN medicines m ON l.medicineId = m.id
        WHERE l.id = :id
        """
    )
    suspend fun getHistoryWithDetailsById(id: String): ReminderHistoryWithDetails?

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM reminder_logs
            WHERE reminderId = :reminderId
            AND scheduledTime = :scheduledTime
            AND actionTaken = :actionTaken
        )
        """
    )
    suspend fun hasLog(reminderId: String, scheduledTime: Long, actionTaken: String): Boolean

    @Query("SELECT * FROM reminder_logs WHERE scheduledTime BETWEEN :startTime AND :endTime")
    suspend fun getSummaryByDateRange(startTime: Long, endTime: Long): List<ReminderLogEntity>
}

data class ReminderHistoryWithDetails(
    val logId: String,
    val reminderId: String,
    val memberId: String,
    val memberName: String?,
    val medicineId: String,
    val medicineName: String?,
    val medicineStrength: String?,
    val scheduledTime: Long,
    val actionTaken: String,
    val actionTime: Long?,
    val notes: String?,
    val createdAt: Long
)
