package com.example.medireminder.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey
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
