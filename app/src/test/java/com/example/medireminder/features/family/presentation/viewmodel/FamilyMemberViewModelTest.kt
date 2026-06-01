package com.example.medireminder.features.family.presentation.viewmodel

import com.example.medireminder.features.family.domain.model.FamilyMember
import com.example.medireminder.features.family.domain.usecase.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FamilyMemberViewModelTest {

    private val getFamilyMembersUseCase: GetFamilyMembersUseCase = mockk()
    private val getFamilyMemberByIdUseCase: GetFamilyMemberByIdUseCase = mockk()
    private val addFamilyMemberUseCase: AddFamilyMemberUseCase = mockk()
    private val updateFamilyMemberUseCase: UpdateFamilyMemberUseCase = mockk()
    private val deleteFamilyMemberUseCase: DeleteFamilyMemberUseCase = mockk()
    private val toggleActiveStatusUseCase: ToggleFamilyMemberActiveStatusUseCase = mockk()

    private lateinit var viewModel: FamilyMemberViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

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
            createdAt = 0L,
            updatedAt = 0L
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getFamilyMembersUseCase() } returns flowOf(members)
        viewModel = FamilyMemberViewModel(
            getFamilyMembersUseCase,
            getFamilyMemberByIdUseCase,
            addFamilyMemberUseCase,
            updateFamilyMemberUseCase,
            deleteFamilyMemberUseCase,
            toggleActiveStatusUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMembers updates uiState with members`() = runTest {
        assertEquals(members, viewModel.uiState.value.members)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onFullNameChange updates formState`() {
        viewModel.onFullNameChange("New Name")
        assertEquals("New Name", viewModel.formState.value.fullName)
        assertNull(viewModel.formState.value.fullNameError)
    }

    @Test
    fun `saveMember with blank name sets error`() = runTest {
        coEvery { 
            addFamilyMemberUseCase(any(), any(), any(), any(), any(), any(), any()) 
        } returns Result.failure(Exception("Full name is required"))

        viewModel.onFullNameChange("")
        viewModel.saveMember(null) {}

        assertEquals("Full name is required", viewModel.formState.value.fullNameError)
    }

    @Test
    fun `saveMember successfully updates state and calls onSuccess`() = runTest {
        coEvery { 
            addFamilyMemberUseCase(any(), any(), any(), any(), any(), any(), any()) 
        } returns Result.success(Unit)

        var successCalled = false
        viewModel.onFullNameChange("John Doe")
        viewModel.saveMember(null) {
            successCalled = true
        }

        assertEquals("Member added", viewModel.uiState.value.successMessage)
        assertEquals(true, successCalled)
    }

    @Test
    fun `toggleActiveStatus calls use case and reloads details`() = runTest {
        coEvery { toggleActiveStatusUseCase("1", false) } just Runs
        coEvery { getFamilyMemberByIdUseCase("1") } returns members[0].copy(isActive = false)

        viewModel.toggleActiveStatus("1", true)

        coVerify { toggleActiveStatusUseCase("1", false) }
        coVerify { getFamilyMemberByIdUseCase("1") }
    }
}
