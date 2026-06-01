package com.example.medireminder.features.family.domain.usecase

import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ToggleFamilyMemberActiveStatusUseCaseTest {

    private lateinit var repository: FamilyMemberRepository
    private lateinit var toggleUseCase: ToggleFamilyMemberActiveStatusUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        toggleUseCase = ToggleFamilyMemberActiveStatusUseCase(repository)
    }

    @Test
    fun `when toggle is called, repository toggleActiveStatus is invoked`() = runBlocking {
        toggleUseCase("1", false)

        coVerify { repository.toggleActiveStatus("1", false) }
    }
}
