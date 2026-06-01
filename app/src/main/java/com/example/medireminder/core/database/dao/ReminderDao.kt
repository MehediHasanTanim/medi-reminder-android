package com.example.medireminder.core.database.dao

import androidx.room.*
import com.example.medireminder.core.database.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity)

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: String): ReminderEntity?

    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isActive = 1")
    fun observeActiveReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE memberMedicineId = :memberMedicineId")
    fun observeRemindersByMemberMedicine(memberMedicineId: String): Flow<List<ReminderEntity>>

    @Transaction
    @Query("""
        SELECT r.*, fm.fullName as familyMemberName, fm.id as familyMemberId, m.name as medicineName, m.id as medicineId, m.unit as medicineUnit, mm.dosageQuantity as dosageQuantity, mm.instructions as instructions, mm.autoReduceStock as autoReduceStock
        FROM reminders r
        JOIN member_medicines mm ON r.memberMedicineId = mm.id
        JOIN family_members fm ON mm.familyMemberId = fm.id
        JOIN medicines m ON mm.medicineId = m.id
    """)
    fun observeRemindersWithDetails(): Flow<List<ReminderWithDetails>>

    @Transaction
    @Query("""
        SELECT r.*, fm.fullName as familyMemberName, fm.id as familyMemberId, m.name as medicineName, m.id as medicineId, m.unit as medicineUnit, mm.dosageQuantity as dosageQuantity, mm.instructions as instructions, mm.autoReduceStock as autoReduceStock
        FROM reminders r
        JOIN member_medicines mm ON r.memberMedicineId = mm.id
        JOIN family_members fm ON mm.familyMemberId = fm.id
        JOIN medicines m ON mm.medicineId = m.id
        WHERE r.id = :id
    """)
    suspend fun getReminderWithDetailsById(id: String): ReminderWithDetails?
    
    @Query("SELECT * FROM reminders WHERE isActive = 1")
    suspend fun getActiveRemindersSync(): List<ReminderEntity>
}

data class ReminderWithDetails(
    val id: String,
    val memberMedicineId: String,
    val reminderTime: String,
    val repeatType: String,
    val repeatDays: String?,
    val startDate: Long,
    val endDate: Long?,
    val alarmTone: String?,
    val vibrationEnabled: Boolean,
    val notificationEnabled: Boolean,
    val snoozeEnabled: Boolean,
    val snoozeDuration: Int,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    // Details
    val familyMemberId: String,
    val familyMemberName: String,
    val medicineId: String,
    val medicineName: String,
    val medicineUnit: String,
    val dosageQuantity: Double,
    val instructions: String?,
    val autoReduceStock: Boolean
)
