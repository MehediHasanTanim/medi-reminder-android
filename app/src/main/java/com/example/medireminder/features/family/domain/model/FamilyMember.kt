package com.example.medireminder.features.family.domain.model

import com.example.medireminder.core.database.entities.FamilyMemberEntity

data class FamilyMember(
    val id: String,
    val fullName: String,
    val age: Int?,
    val gender: String?,
    val relationship: String?,
    val bloodGroup: String?,
    val phone: String?,
    val notes: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

fun FamilyMemberEntity.toDomain(): FamilyMember {
    return FamilyMember(
        id = id,
        fullName = fullName,
        age = age,
        gender = gender,
        relationship = relationship,
        bloodGroup = bloodGroup,
        phone = phone,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun FamilyMember.toEntity(): FamilyMemberEntity {
    return FamilyMemberEntity(
        id = id,
        fullName = fullName,
        age = age,
        gender = gender,
        relationship = relationship,
        bloodGroup = bloodGroup,
        phone = phone,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
