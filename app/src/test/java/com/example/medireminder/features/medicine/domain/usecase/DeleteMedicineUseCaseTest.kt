package com.example.medireminder.features.medicine.domain.usecase

import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DeleteMedicineUseCaseTest {

    private lateinit var repository: MedicineRepository
    private lateinit var deleteMedicineUseCase: DeleteMedicineUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        deleteMedicineUseCase = DeleteMedicineUseCase(repository)
    }

    @Test
    fun `when delete is called, repository toggleActiveStatus is invoked with false`() = runBlocking {
        val medicine = Medicine(
            id = "1",
            name = "Napa",
            genericName = "Paracetamol",
            medicineType = "Tablet",
            strength = "500mg",
            unit = "Tablet",
            isActive = true,
            createdAt = 1000L,
            updatedAt = 1000L,
            manufacturer = "Beximco",
            notes = ""
        )

        deleteMedicineUseCase(medicine)

        coVerify { repository.toggleActiveStatus("1", false) }
    }
}
