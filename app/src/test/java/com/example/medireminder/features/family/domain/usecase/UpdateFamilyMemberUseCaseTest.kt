package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateFamilyMemberUseCaseTest {

    private lateinit var repository: FamilyMemberRepository
    private lateinit var updateFamilyMemberUseCase: UpdateFamilyMemberUseCase

    private val existingMember = FamilyMember(
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
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        updateFamilyMemberUseCase = UpdateFamilyMemberUseCase(repository)
    }

    @Test
    fun `when member exists and data is valid, updates member`() = runBlocking {
        coEvery { repository.getMemberById("1") } returns existingMember

        val result = updateFamilyMemberUseCase(
            id = "1",
            fullName = "John Updated",
            age = 31,
            gender = "Male",
            relationship = "Self",
            bloodGroup = "O+",
            phone = "123",
            notes = "Updated notes",
            isActive = true
        )

        assertTrue(result.isSuccess)
        coVerify { 
            repository.updateMember(withArg {
                assertEquals("John Updated", it.fullName)
                assertEquals(31, it.age)
                assertEquals(1000L, it.createdAt) // Should keep original createdAt
                assertTrue(it.updatedAt > 1000L) // Should update updatedAt
            })
        }
    }

    @Test
    fun `when member does not exist, returns failure`() = runBlocking {
        coEvery { repository.getMemberById("1") } returns null

        val result = updateFamilyMemberUseCase(
            id = "1",
            fullName = "John Updated",
            age = 31,
            gender = "Male",
            relationship = "Self",
            bloodGroup = "O+",
            phone = "123",
            notes = "Updated notes",
            isActive = true
        )

        assertTrue(result.isFailure)
        assertEquals("Member not found", result.exceptionOrNull()?.message)
    }
}
