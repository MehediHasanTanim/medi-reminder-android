package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMedicineByIdUseCaseTest {

    private lateinit var repository: MedicineRepository
    private lateinit var getMedicineByIdUseCase: GetMedicineByIdUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getMedicineByIdUseCase = GetMedicineByIdUseCase(repository)
    }

    @Test
    fun `when medicine exists, returns medicine`() = runBlocking {
        val medicine = Medicine(
            id = "1", name = "Napa", genericName = "Paracetamol",
            medicineType = "Tablet", strength = "500mg", unit = "Tablet",
            isActive = true, createdAt = 0L, updatedAt = 0L,
            manufacturer = "Beximco", notes = ""
        )
        coEvery { repository.getMedicineById("1") } returns medicine

        val result = getMedicineByIdUseCase("1")

        assertEquals(medicine, result)
    }

    @Test
    fun `when medicine does not exist, returns null`() = runBlocking {
        coEvery { repository.getMedicineById("1") } returns null

        val result = getMedicineByIdUseCase("1")

        assertEquals(null, result)
    }
}
