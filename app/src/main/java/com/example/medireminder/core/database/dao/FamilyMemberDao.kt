package com.example.medireminder.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medireminder.core.database.entities.FamilyMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: FamilyMemberEntity)

    @Update
    suspend fun update(member: FamilyMemberEntity)

    @Delete
    suspend fun delete(member: FamilyMemberEntity)

    @Query("SELECT * FROM family_members WHERE id = :id")
    suspend fun getById(id: String): FamilyMemberEntity?

    @Query("SELECT * FROM family_members")
    fun getAll(): Flow<List<FamilyMemberEntity>>

    @Query("SELECT * FROM family_members WHERE isActive = 1")
    fun observeActiveMembers(): Flow<List<FamilyMemberEntity>>
}
