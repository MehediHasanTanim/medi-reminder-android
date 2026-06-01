package com.example.medireminder.features.family.domain.repository

import com.example.medireminder.features.family.domain.model.FamilyMember
import kotlinx.coroutines.flow.Flow

interface FamilyMemberRepository {
    fun observeAllMembers(): Flow<List<FamilyMember>>
    fun observeActiveMembers(): Flow<List<FamilyMember>>
    suspend fun getMemberById(id: String): FamilyMember?
    suspend fun addMember(member: FamilyMember)
    suspend fun updateMember(member: FamilyMember)
    suspend fun deleteMember(member: FamilyMember)
    suspend fun toggleActiveStatus(id: String, isActive: Boolean)
}
