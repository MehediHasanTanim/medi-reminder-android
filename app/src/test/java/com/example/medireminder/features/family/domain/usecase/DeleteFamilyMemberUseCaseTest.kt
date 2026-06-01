package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DeleteFamilyMemberUseCaseTest {

    private lateinit var repository: FamilyMemberRepository
    private lateinit var deleteFamilyMemberUseCase: DeleteFamilyMemberUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        deleteFamilyMemberUseCase = DeleteFamilyMemberUseCase(repository)
    }

    @Test
    fun `when delete is called, repository delete is invoked`() = runBlocking {
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

        deleteFamilyMemberUseCase(member)

        coVerify { repository.deleteMember(member) }
    }
}
