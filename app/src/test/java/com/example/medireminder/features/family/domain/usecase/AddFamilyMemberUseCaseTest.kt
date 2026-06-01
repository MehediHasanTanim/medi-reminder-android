package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddFamilyMemberUseCaseTest {

    private lateinit var repository: FamilyMemberRepository
    private lateinit var addFamilyMemberUseCase: AddFamilyMemberUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        addFamilyMemberUseCase = AddFamilyMemberUseCase(repository)
    }

    @Test
    fun `when name is blank, returns failure`() = runBlocking {
        val result = addFamilyMemberUseCase(
            fullName = "  ",
            age = 25,
            gender = "Male",
            relationship = "Friend",
            bloodGroup = "A+",
            phone = "123",
            notes = "None"
        )

        assertTrue(result.isFailure)
        assertEquals("Full name is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when age is negative, returns failure`() = runBlocking {
        val result = addFamilyMemberUseCase(
            fullName = "John Doe",
            age = -1,
            gender = "Male",
            relationship = "Friend",
            bloodGroup = "A+",
            phone = "123",
            notes = "None"
        )

        assertTrue(result.isFailure)
        assertEquals("Age cannot be negative", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when data is valid, saves member and returns success`() = runBlocking {
        val result = addFamilyMemberUseCase(
            fullName = "John Doe",
            age = 25,
            gender = "Male",
            relationship = "Friend",
            bloodGroup = "A+",
            phone = "123",
            notes = "None"
        )

        assertTrue(result.isSuccess)
        coVerify { repository.addMember(any()) }
    }
}
