package com.example.medireminder.core.database.dao

import androidx.room.*
import com.example.medireminder.core.database.entities.MemberMedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberMedicineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memberMedicine: MemberMedicineEntity)

    @Update
    suspend fun update(memberMedicine: MemberMedicineEntity)

    @Delete
    suspend fun delete(memberMedicine: MemberMedicineEntity)

    @Query("SELECT * FROM member_medicines WHERE id = :id")
    suspend fun getById(id: String): MemberMedicineEntity?

    @Query("SELECT * FROM member_medicines")
    fun getAll(): Flow<List<MemberMedicineEntity>>

    @Query("SELECT * FROM member_medicines WHERE isActive = 1")
    fun observeActive(): Flow<List<MemberMedicineEntity>>

    @Query("SELECT * FROM member_medicines WHERE familyMemberId = :familyMemberId")
    fun observeMedicinesByMember(familyMemberId: String): Flow<List<MemberMedicineEntity>>

    @Query("SELECT * FROM member_medicines WHERE medicineId = :medicineId")
    fun observeMembersByMedicine(medicineId: String): Flow<List<MemberMedicineEntity>>

    @Query("SELECT * FROM member_medicines WHERE medicineId = :medicineId AND isActive = 1")
    suspend fun getActiveMemberMedicinesForMedicine(medicineId: String): List<MemberMedicineEntity>

    @Transaction
    @Query("""
        SELECT mm.*, fm.fullName as familyMemberName, m.name as medicineName, m.unit as medicineUnit, m.medicineType as medicineType
        FROM member_medicines mm
        JOIN family_members fm ON mm.familyMemberId = fm.id
        JOIN medicines m ON mm.medicineId = m.id
    """)
    fun observeMemberMedicineWithDetails(): Flow<List<MemberMedicineWithDetails>>

    @Transaction
    @Query("""
        SELECT mm.*, fm.fullName as familyMemberName, m.name as medicineName, m.unit as medicineUnit, m.medicineType as medicineType
        FROM member_medicines mm
        JOIN family_members fm ON mm.familyMemberId = fm.id
        JOIN medicines m ON mm.medicineId = m.id
        WHERE mm.familyMemberId = :familyMemberId
    """)
    fun observeMemberMedicineWithDetailsByMember(familyMemberId: String): Flow<List<MemberMedicineWithDetails>>

    @Transaction
    @Query("""
        SELECT mm.*, fm.fullName as familyMemberName, m.name as medicineName, m.unit as medicineUnit, m.medicineType as medicineType
        FROM member_medicines mm
        JOIN family_members fm ON mm.familyMemberId = fm.id
        JOIN medicines m ON mm.medicineId = m.id
        WHERE mm.medicineId = :medicineId
    """)
    fun observeMemberMedicineWithDetailsByMedicine(medicineId: String): Flow<List<MemberMedicineWithDetails>>

    @Transaction
    @Query("""
        SELECT mm.*, fm.fullName as familyMemberName, m.name as medicineName, m.unit as medicineUnit, m.medicineType as medicineType
        FROM member_medicines mm
        JOIN family_members fm ON mm.familyMemberId = fm.id
        JOIN medicines m ON mm.medicineId = m.id
        WHERE mm.id = :id
    """)
    suspend fun getMemberMedicineWithDetailsById(id: String): MemberMedicineWithDetails?
}

data class MemberMedicineWithDetails(
    val id: String,
    val familyMemberId: String,
    val medicineId: String,
    val familyMemberName: String,
    val medicineName: String,
    val medicineUnit: String,
    val medicineType: String,
    val dosageQuantity: Double,
    val frequencyPerDay: Int,
    val dailyTotalQuantity: Double,
    val instructions: String?,
    val startDate: Long,
    val endDate: Long?,
    val isActive: Boolean,
    val autoReduceStock: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
