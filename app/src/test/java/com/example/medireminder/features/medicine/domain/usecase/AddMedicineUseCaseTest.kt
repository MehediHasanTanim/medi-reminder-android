package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddMedicineUseCaseTest {

    private lateinit var repository: MedicineRepository
    private lateinit var addMedicineUseCase: AddMedicineUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        addMedicineUseCase = AddMedicineUseCase(repository)
    }

    @Test
    fun `when name is blank, returns failure`() = runBlocking {
        val result = addMedicineUseCase(
            name = " ",
            genericName = "Paracetamol",
            medicineType = "Tablet",
            strength = "500mg",
            unit = "Tablet",
            manufacturer = "Beximco",
            notes = ""
        )

        assertTrue(result.isFailure)
        assertEquals("Medicine name is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when type is blank, returns failure`() = runBlocking {
        val result = addMedicineUseCase(
            name = "Napa",
            genericName = "Paracetamol",
            medicineType = "",
            strength = "500mg",
            unit = "Tablet",
            manufacturer = "Beximco",
            notes = ""
        )

        assertTrue(result.isFailure)
        assertEquals("Medicine type is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when unit is blank, returns failure`() = runBlocking {
        val result = addMedicineUseCase(
            name = "Napa",
            genericName = "Paracetamol",
            medicineType = "Tablet",
            strength = "500mg",
            unit = "",
            manufacturer = "Beximco",
            notes = ""
        )

        assertTrue(result.isFailure)
        assertEquals("Unit is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `when data is valid, saves medicine and returns success`() = runBlocking {
        val result = addMedicineUseCase(
            name = "Napa",
            genericName = "Paracetamol",
            medicineType = "Tablet",
            strength = "500mg",
            unit = "Tablet",
            manufacturer = "Beximco",
            notes = ""
        )

        assertTrue(result.isSuccess)
        coVerify { repository.addMedicine(any()) }
    }
}
