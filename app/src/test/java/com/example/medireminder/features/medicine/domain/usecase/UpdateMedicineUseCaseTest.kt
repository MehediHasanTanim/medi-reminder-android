package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateMedicineUseCaseTest {

    private lateinit var repository: MedicineRepository
    private lateinit var updateMedicineUseCase: UpdateMedicineUseCase

    private val existingMedicine = Medicine(
        id = "1",
        name = "Napa",
        genericName = "Paracetamol",
        medicineType = "Tablet",
        strength = "500mg",
        unit = "Tablet",
        manufacturer = "Beximco",
        notes = "",
        isActive = true,
        createdAt = 1000L,
        updatedAt = 1000L
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        updateMedicineUseCase = UpdateMedicineUseCase(repository)
    }

    @Test
    fun `when member exists and data is valid, updates medicine`() = runBlocking {
        coEvery { repository.getMedicineById("1") } returns existingMedicine

        val result = updateMedicineUseCase(
            id = "1",
            name = "Napa Extra",
            genericName = "Paracetamol + Caffeine",
            medicineType = "Tablet",
            strength = "500mg+65mg",
            unit = "Tablet",
            manufacturer = "Beximco",
            notes = "Updated notes",
            isActive = true
        )

        assertTrue(result.isSuccess)
        coVerify { 
            repository.updateMedicine(withArg {
                assertEquals("Napa Extra", it.name)
                assertEquals(1000L, it.createdAt)
                assertTrue(it.updatedAt > 1000L)
            })
        }
    }

    @Test
    fun `when medicine does not exist, returns failure`() = runBlocking {
        coEvery { repository.getMedicineById("1") } returns null

        val result = updateMedicineUseCase(
            id = "1",
            name = "Napa Extra",
            genericName = "Paracetamol + Caffeine",
            medicineType = "Tablet",
            strength = "500mg+65mg",
            unit = "Tablet",
            manufacturer = "Beximco",
            notes = "Updated notes",
            isActive = true
        )

        assertTrue(result.isFailure)
        assertEquals("Medicine not found", result.exceptionOrNull()?.message)
    }
}
