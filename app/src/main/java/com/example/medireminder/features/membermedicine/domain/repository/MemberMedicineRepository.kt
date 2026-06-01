package com.example.medireminder.features.membermedicine.domain.repository

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import kotlinx.coroutines.flow.Flow

interface MemberMedicineRepository {
    fun observeAllAssignments(): Flow<List<MemberMedicine>>
    fun observeActiveAssignments(): Flow<List<MemberMedicine>>
    fun observeMedicinesByMember(familyMemberId: String): Flow<List<MemberMedicine>>
    fun observeMembersByMedicine(medicineId: String): Flow<List<MemberMedicine>>
    suspend fun getAssignmentById(id: String): MemberMedicine?
    suspend fun assignMedicine(memberMedicine: MemberMedicine)
    suspend fun updateAssignment(memberMedicine: MemberMedicine)
    suspend fun deactivateAssignment(id: String)
    suspend fun deleteAssignment(memberMedicine: MemberMedicine)
}
