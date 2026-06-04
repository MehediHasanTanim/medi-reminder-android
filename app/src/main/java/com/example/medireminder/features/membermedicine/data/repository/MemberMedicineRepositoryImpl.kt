package com.example.medireminder.features.membermedicine.data.repository

import com.example.medireminder.core.database.dao.MemberMedicineDao
import com.example.medireminder.features.membermedicine.data.mapper.toEntity
import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MemberMedicineRepositoryImpl @Inject constructor(
    private val memberMedicineDao: MemberMedicineDao
) : MemberMedicineRepository {

    override fun observeAllAssignments(): Flow<List<MemberMedicine>> {
        return memberMedicineDao.observeMemberMedicineWithDetails().map { details ->
            details.map { detail ->
                MemberMedicine(
                    id = detail.id,
                    familyMemberId = detail.familyMemberId,
                    medicineId = detail.medicineId,
                    familyMemberName = detail.familyMemberName,
                    medicineName = detail.medicineName,
                    medicineUnit = detail.medicineUnit,
                    medicineType = detail.medicineType,
                    dosageQuantity = detail.dosageQuantity,
                    frequencyPerDay = detail.frequencyPerDay,
                    dailyTotalQuantity = detail.dailyTotalQuantity,
                    instructions = detail.instructions,
                    startDate = detail.startDate,
                    endDate = detail.endDate,
                    isActive = detail.isActive,
                    autoReduceStock = detail.autoReduceStock,
                    createdAt = detail.createdAt,
                    updatedAt = detail.updatedAt
                )
            }
        }
    }

    override fun observeActiveAssignments(): Flow<List<MemberMedicine>> {
        return memberMedicineDao.observeActiveWithDetails().map { details ->
            details.map { detail ->
                MemberMedicine(
                    id = detail.id,
                    familyMemberId = detail.familyMemberId,
                    medicineId = detail.medicineId,
                    familyMemberName = detail.familyMemberName,
                    medicineName = detail.medicineName,
                    medicineUnit = detail.medicineUnit,
                    medicineType = detail.medicineType,
                    dosageQuantity = detail.dosageQuantity,
                    frequencyPerDay = detail.frequencyPerDay,
                    dailyTotalQuantity = detail.dailyTotalQuantity,
                    instructions = detail.instructions,
                    startDate = detail.startDate,
                    endDate = detail.endDate,
                    isActive = detail.isActive,
                    autoReduceStock = detail.autoReduceStock,
                    createdAt = detail.createdAt,
                    updatedAt = detail.updatedAt
                )
            }
        }
    }

    override fun observeMedicinesByMember(familyMemberId: String): Flow<List<MemberMedicine>> {
        return memberMedicineDao.observeMemberMedicineWithDetailsByMember(familyMemberId).map { details ->
            details.map { detail ->
                MemberMedicine(
                    id = detail.id,
                    familyMemberId = detail.familyMemberId,
                    medicineId = detail.medicineId,
                    familyMemberName = detail.familyMemberName,
                    medicineName = detail.medicineName,
                    medicineUnit = detail.medicineUnit,
                    medicineType = detail.medicineType,
                    dosageQuantity = detail.dosageQuantity,
                    frequencyPerDay = detail.frequencyPerDay,
                    dailyTotalQuantity = detail.dailyTotalQuantity,
                    instructions = detail.instructions,
                    startDate = detail.startDate,
                    endDate = detail.endDate,
                    isActive = detail.isActive,
                    autoReduceStock = detail.autoReduceStock,
                    createdAt = detail.createdAt,
                    updatedAt = detail.updatedAt
                )
            }
        }
    }

    override fun observeMembersByMedicine(medicineId: String): Flow<List<MemberMedicine>> {
        return memberMedicineDao.observeMemberMedicineWithDetailsByMedicine(medicineId).map { details ->
            details.map { detail ->
                MemberMedicine(
                    id = detail.id,
                    familyMemberId = detail.familyMemberId,
                    medicineId = detail.medicineId,
                    familyMemberName = detail.familyMemberName,
                    medicineName = detail.medicineName,
                    medicineUnit = detail.medicineUnit,
                    medicineType = detail.medicineType,
                    dosageQuantity = detail.dosageQuantity,
                    frequencyPerDay = detail.frequencyPerDay,
                    dailyTotalQuantity = detail.dailyTotalQuantity,
                    instructions = detail.instructions,
                    startDate = detail.startDate,
                    endDate = detail.endDate,
                    isActive = detail.isActive,
                    autoReduceStock = detail.autoReduceStock,
                    createdAt = detail.createdAt,
                    updatedAt = detail.updatedAt
                )
            }
        }
    }

    override suspend fun getAssignmentById(id: String): MemberMedicine? {
        return memberMedicineDao.getMemberMedicineWithDetailsById(id)?.let { detail ->
            MemberMedicine(
                id = detail.id,
                familyMemberId = detail.familyMemberId,
                medicineId = detail.medicineId,
                familyMemberName = detail.familyMemberName,
                medicineName = detail.medicineName,
                medicineUnit = detail.medicineUnit,
                medicineType = detail.medicineType,
                dosageQuantity = detail.dosageQuantity,
                frequencyPerDay = detail.frequencyPerDay,
                dailyTotalQuantity = detail.dailyTotalQuantity,
                instructions = detail.instructions,
                startDate = detail.startDate,
                endDate = detail.endDate,
                isActive = detail.isActive,
                autoReduceStock = detail.autoReduceStock,
                createdAt = detail.createdAt,
                updatedAt = detail.updatedAt
            )
        }
    }

    override suspend fun assignMedicine(memberMedicine: MemberMedicine) {
        memberMedicineDao.insert(memberMedicine.toEntity())
    }

    override suspend fun updateAssignment(memberMedicine: MemberMedicine) {
        memberMedicineDao.update(memberMedicine.toEntity())
    }

    override suspend fun deactivateAssignment(id: String) {
        val assignment = memberMedicineDao.getById(id)
        assignment?.let {
            memberMedicineDao.update(it.copy(isActive = false, updatedAt = System.currentTimeMillis()))
        }
    }

    override suspend fun deleteAssignment(memberMedicine: MemberMedicine) {
        memberMedicineDao.delete(memberMedicine.toEntity())
    }
}
