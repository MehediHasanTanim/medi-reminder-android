package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetFamilyMembersUseCaseTest {

    private lateinit var repository: FamilyMemberRepository
    private lateinit var getFamilyMembersUseCase: GetFamilyMembersUseCase

    private val members = listOf(
        FamilyMember(
            id = "1",
            fullName = "John Doe",
            age = 30,
            gender = "Male",
            relationship = "Self",
            bloodGroup = "O+",
            phone = "123",
            notes = "Notes",
            isActive = true,
            createdAt = 1000L,
            updatedAt = 1000L
        ),
        FamilyMember(
            id = "2",
            fullName = "Sarah Doe",
            age = 28,
            gender = "Female",
            relationship = "Wife",
            bloodGroup = "A+",
            phone = "456",
            notes = "Notes",
            isActive = false,
            createdAt = 1000L,
            updatedAt = 1000L
        )
    )

    @Before
    fun setUp() {
        repository = mockk()
        getFamilyMembersUseCase = GetFamilyMembersUseCase(repository)
    }

    @Test
    fun `when onlyActive is false, returns all members`() = runBlocking {
        every { repository.observeAllMembers() } returns flowOf(members)

        val result = getFamilyMembersUseCase(onlyActive = false).first()

        assertEquals(2, result.size)
        assertEquals(members, result)
    }

    @Test
    fun `when onlyActive is true, returns active members`() = runBlocking {
        val activeMembers = members.filter { it.isActive }
        every { repository.observeActiveMembers() } returns flowOf(activeMembers)

        val result = getFamilyMembersUseCase(onlyActive = true).first()

        assertEquals(1, result.size)
        assertTrue(result.all { it.isActive })
        assertEquals(activeMembers, result)
    }
    
    private fun assertTrue(condition: Boolean) {
        assert(condition)
    }
}
