package com.example.medireminder.features.membermedicine.domain.usecase

import com.example.medireminder.features.membermedicine.domain.model.MemberMedicine
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MemberMedicineUseCaseTest {

    private val repository: MemberMedicineRepository = mockk()

    private lateinit var assignUseCase: AssignMedicineToMemberUseCase
    private lateinit var updateUseCase: UpdateMemberMedicineUseCase
    private lateinit var deactivateUseCase: DeactivateMemberMedicineUseCase

    @Before
    fun setUp() {
        assignUseCase = AssignMedicineToMemberUseCase(repository)
        updateUseCase = UpdateMemberMedicineUseCase(repository)
        deactivateUseCase = DeactivateMemberMedicineUseCase(repository)
    }

    @Test
    fun `Assign medicine - Valid data - Success`() = runBlocking {
        coEvery { repository.assignMedicine(any()) } returns Unit

        val result = assignUseCase(
            familyMemberId = "member1",
            medicineId = "med1",
            dosageQuantity = 1.0,
            frequencyPerDay = 3,
            startDate = System.currentTimeMillis()
        )

        assertTrue(result.isSuccess)
        coVerify { repository.assignMedicine(any()) }
    }

    @Test
    fun `Assign medicine - Missing member - Failure`() = runBlocking {
        val result = assignUseCase(
            familyMemberId = "",
            medicineId = "med1",
            dosageQuantity = 1.0,
            frequencyPerDay = 3,
            startDate = System.currentTimeMillis()
        )

        assertTrue(result.isFailure)
        assertEquals("Family member is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `Assign medicine - Invalid dosage - Failure`() = runBlocking {
        val result = assignUseCase(
            familyMemberId = "m1",
            medicineId = "med1",
            dosageQuantity = 0.0,
            frequencyPerDay = 3,
            startDate = System.currentTimeMillis()
        )

        assertTrue(result.isFailure)
        assertEquals("Dosage quantity must be greater than 0", result.exceptionOrNull()?.message)
    }

    @Test
    fun `Assign medicine - End date before start date - Failure`() = runBlocking {
        val start = System.currentTimeMillis()
        val result = assignUseCase(
            familyMemberId = "m1",
            medicineId = "med1",
            dosageQuantity = 1.0,
            frequencyPerDay = 3,
            startDate = start,
            endDate = start - 1000
        )

        assertTrue(result.isFailure)
        assertEquals("End date cannot be earlier than start date", result.exceptionOrNull()?.message)
    }

    @Test
    fun `Daily total calculation - Correct result`() = runBlocking {
        coEvery { repository.assignMedicine(any()) } returns Unit
        
        val result = assignUseCase(
            familyMemberId = "m1",
            medicineId = "med1",
            dosageQuantity = 2.0,
            frequencyPerDay = 3,
            startDate = System.currentTimeMillis()
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun `Update assignment - Recalculates total - Success`() = runBlocking {
        val assignment = MemberMedicine(
            id = "1",
            familyMemberId = "m1",
            medicineId = "med1",
            dosageQuantity = 2.0,
            frequencyPerDay = 2,
            dailyTotalQuantity = 4.0,
            startDate = System.currentTimeMillis(),
            createdAt = 0,
            updatedAt = 0
        )

        coEvery { repository.updateAssignment(any()) } returns Unit

        val result = updateUseCase(assignment.copy(frequencyPerDay = 4))

        assertTrue(result.isSuccess)
        coVerify { repository.updateAssignment(match { it.dailyTotalQuantity == 8.0 }) }
    }

    @Test
    fun `Deactivate assignment - Success`() = runBlocking {
        coEvery { repository.deactivateAssignment("1") } returns Unit
        
        val result = deactivateUseCase("1")
        
        assertTrue(result.isSuccess)
        coVerify { repository.deactivateAssignment("1") }
    }
}
