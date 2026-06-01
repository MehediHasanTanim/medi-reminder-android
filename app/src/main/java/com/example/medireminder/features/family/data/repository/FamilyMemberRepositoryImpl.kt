package com.example.medireminder.features.family.data.repository

import com.example.medireminder.core.database.dao.FamilyMemberDao
import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.model.toDomain
import com.example.medireminder.features.family.domain.model.toEntity
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FamilyMemberRepositoryImpl @Inject constructor(
    private val dao: FamilyMemberDao
) : FamilyMemberRepository {

    override fun observeAllMembers(): Flow<List<FamilyMember>> {
        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeActiveMembers(): Flow<List<FamilyMember>> {
        return dao.observeActiveMembers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMemberById(id: String): FamilyMember? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun addMember(member: FamilyMember) {
        dao.insert(member.toEntity())
    }

    override suspend fun updateMember(member: FamilyMember) {
        dao.update(member.toEntity())
    }

    override suspend fun deleteMember(member: FamilyMember) {
        dao.delete(member.toEntity())
    }

    override suspend fun toggleActiveStatus(id: String, isActive: Boolean) {
        val member = dao.getById(id)
        member?.let {
            dao.update(it.copy(isActive = isActive, updatedAt = System.currentTimeMillis()))
        }
    }
}
