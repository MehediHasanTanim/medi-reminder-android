package com.example.medireminder.features.stock.domain.usecase

import com.example.medireminder.core.notification.NotificationHelper
import com.example.medireminder.features.medicine.domain.model.Medicine
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import com.example.medireminder.features.stock.domain.model.MedicineStock
import com.example.medireminder.features.stock.domain.model.StockTransactionType
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MedicineStockUseCaseTest {

    private lateinit var stockRepository: MedicineStockRepository
    private lateinit var medicineRepository: MedicineRepository
    private lateinit var notificationHelper: NotificationHelper

    @Before
    fun setUp() {
        stockRepository = mockk(relaxed = true)
        medicineRepository = mockk(relaxed = true)
        notificationHelper = mockk(relaxed = true)
    }

    @Test
    fun `add stock validates medicine exists`() = runBlocking {
        coEvery { medicineRepository.getMedicineById("missing") } returns null
        val useCase = AddStockUseCase(stockRepository, medicineRepository)

        val result = useCase(
            medicineId = "missing",
            initialQuantity = 10.0,
            unit = "tablet",
            lowStockThreshold = 3.0
        )

        assertTrue(result.isFailure)
        assertEquals("Medicine not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `add stock creates initial transaction`() = runBlocking {
        coEvery { medicineRepository.getMedicineById("med1") } returns medicine()
        val useCase = AddStockUseCase(stockRepository, medicineRepository)

        val result = useCase(
            medicineId = "med1",
            initialQuantity = 10.0,
            unit = "tablet",
            lowStockThreshold = 3.0
        )

        assertTrue(result.isSuccess)
        coVerify { stockRepository.saveStock(any()) }
        coVerify {
            stockRepository.addTransaction(
                match { it.transactionType == StockTransactionType.INITIAL_STOCK && it.newQuantity == 10.0 }
            )
        }
    }

    @Test
    fun `refill stock increases quantity and records transaction`() = runBlocking {
        coEvery { stockRepository.getStockByMedicineId("med1") } returns stock(currentQuantity = 4.0)
        val useCase = RefillStockUseCase(stockRepository)

        val result = useCase("med1", 6.0, "Bought new strip")

        assertTrue(result.isSuccess)
        coVerify { stockRepository.updateStock(match { it.currentQuantity == 10.0 }) }
        coVerify {
            stockRepository.addTransaction(
                match { it.transactionType == StockTransactionType.REFILL && it.previousQuantity == 4.0 && it.newQuantity == 10.0 }
            )
        }
    }

    @Test
    fun `reduce stock fails when quantity is insufficient`() = runBlocking {
        coEvery { stockRepository.getStockByMedicineId("med1") } returns stock(currentQuantity = 2.0)
        val useCase = ReduceStockUseCase(stockRepository, notificationHelper)

        val result = useCase("med1", 3.0)

        assertTrue(result.isFailure)
        assertEquals("Insufficient stock for Napa", result.exceptionOrNull()?.message)
    }

    @Test
    fun `reduce stock records dose taken transaction`() = runBlocking {
        coEvery { stockRepository.getStockByMedicineId("med1") } returns stock(currentQuantity = 8.0)
        val useCase = ReduceStockUseCase(stockRepository, notificationHelper)

        val result = useCase("med1", 2.0)

        assertTrue(result.isSuccess)
        coVerify { stockRepository.updateStockQuantity("med1", 6.0) }
        coVerify {
            stockRepository.addTransaction(
                match { it.transactionType == StockTransactionType.DOSE_TAKEN && it.newQuantity == 6.0 }
            )
        }
    }

    @Test
    fun `manual adjustment fails when final stock would be negative`() = runBlocking {
        coEvery { stockRepository.getStockByMedicineId("med1") } returns stock(currentQuantity = 2.0)
        val useCase = AdjustStockUseCase(stockRepository, notificationHelper)

        val result = useCase("med1", -3.0)

        assertTrue(result.isFailure)
        assertEquals("Adjustment would make stock negative", result.exceptionOrNull()?.message)
    }

    @Test
    fun `estimated remaining days rounds to one decimal`() {
        val useCase = GetEstimatedRemainingDaysUseCase()

        assertEquals(3.3, useCase(currentStock = 10.0, dailyConsumption = 3.0)!!, 0.0)
        assertEquals(null, useCase(currentStock = 10.0, dailyConsumption = 0.0))
    }

    private fun medicine() = Medicine(
        id = "med1",
        name = "Napa",
        genericName = "Paracetamol",
        medicineType = "Tablet",
        strength = "500mg",
        unit = "tablet",
        manufacturer = null,
        notes = null,
        isActive = true,
        createdAt = 1L,
        updatedAt = 1L
    )

    private fun stock(currentQuantity: Double) = MedicineStock(
        id = "stock1",
        medicineId = "med1",
        medicineName = "Napa",
        medicineType = "Tablet",
        currentQuantity = currentQuantity,
        unit = "tablet",
        lowStockThreshold = 3.0,
        expiryDate = null,
        autoReduceEnabled = true,
        lastRefillDate = null,
        createdAt = 1L,
        updatedAt = 1L
    )
}
