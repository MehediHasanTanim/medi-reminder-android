package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetFamilyMemberByIdUseCaseTest {

    private lateinit var repository: FamilyMemberRepository
    private lateinit var getFamilyMemberByIdUseCase: GetFamilyMemberByIdUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getFamilyMemberByIdUseCase = GetFamilyMemberByIdUseCase(repository)
    }

    @Test
    fun `when member exists, returns member`() = runBlocking {
        val member = FamilyMember(
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
        coEvery { repository.getMemberById("1") } returns member

        val result = getFamilyMemberByIdUseCase("1")

        assertEquals(member, result)
    }

    @Test
    fun `when member does not exist, returns null`() = runBlocking {
        coEvery { repository.getMemberById("1") } returns null

        val result = getFamilyMemberByIdUseCase("1")

        assertEquals(null, result)
    }
}
