package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetMedicinesUseCaseTest {

    private lateinit var repository: MedicineRepository
    private lateinit var getMedicinesUseCase: GetMedicinesUseCase

    private val medicines = listOf(
        Medicine(
            id = "1", name = "Napa", genericName = "Paracetamol",
            medicineType = "Tablet", strength = "500mg", unit = "Tablet",
            isActive = true, createdAt = 0L, updatedAt = 0L,
            manufacturer = "Beximco", notes = ""
        ),
        Medicine(
            id = "2", name = "Seclo", genericName = "Omeprazole",
            medicineType = "Capsule", strength = "20mg", unit = "Capsule",
            isActive = false, createdAt = 0L, updatedAt = 0L,
            manufacturer = "Square", notes = ""
        )
    )

    @Before
    fun setUp() {
        repository = mockk()
        getMedicinesUseCase = GetMedicinesUseCase(repository)
    }

    @Test
    fun `when onlyActive is false, returns all medicines`() = runBlocking {
        every { repository.observeAllMedicines() } returns flowOf(medicines)

        val result = getMedicinesUseCase(onlyActive = false).first()

        assertEquals(2, result.size)
        assertEquals(medicines, result)
    }

    @Test
    fun `when onlyActive is true, returns active medicines`() = runBlocking {
        val activeMedicines = medicines.filter { it.isActive }
        every { repository.observeActiveMedicines() } returns flowOf(activeMedicines)

        val result = getMedicinesUseCase(onlyActive = true).first()

        assertEquals(1, result.size)
        assertTrue(result.all { it.isActive })
        assertEquals(activeMedicines, result)
    }
}
