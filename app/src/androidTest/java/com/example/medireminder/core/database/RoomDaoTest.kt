package com.example.medireminder.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medireminder.core.database.dao.*
import com.example.medireminder.core.database.entities.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var familyMemberDao: FamilyMemberDao
    private lateinit var medicineDao: MedicineDao
    private lateinit var memberMedicineDao: MemberMedicineDao
    private lateinit var medicineStockDao: MedicineStockDao
    private lateinit var stockTransactionDao: StockTransactionDao
    private lateinit var reminderLogDao: ReminderLogDao
    private lateinit var reportsDao: ReportsDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        familyMemberDao = db.familyMemberDao()
        medicineDao = db.medicineDao()
        memberMedicineDao = db.memberMedicineDao()
        medicineStockDao = db.medicineStockDao()
        stockTransactionDao = db.stockTransactionDao()
        reminderLogDao = db.reminderLogDao()
        reportsDao = db.reportsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadFamilyMember() = runBlocking {
        val member = FamilyMemberEntity(
            id = "1",
            fullName = "John Doe",
            age = 30,
            gender = "Male",
            relationship = "Self",
            bloodGroup = "O+",
            phone = "123456789",
            notes = "Test notes",
            isActive = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        familyMemberDao.insert(member)
        val fetchedMember = familyMemberDao.getById("1")
        assertEquals(member.fullName, fetchedMember?.fullName)
    }

    @Test
    fun updateFamilyMember() = runBlocking {
        val member = FamilyMemberEntity(
            id = "1",
            fullName = "John Doe",
            age = 30,
            gender = "Male",
            relationship = "Self",
            bloodGroup = "O+",
            phone = "123456789",
            notes = "Test notes",
            isActive = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        familyMemberDao.insert(member)
        
        val updatedMember = member.copy(fullName = "John Updated")
        familyMemberDao.update(updatedMember)
        
        val fetched = familyMemberDao.getById("1")
        assertEquals("John Updated", fetched?.fullName)
    }

    @Test
    fun deleteFamilyMember() = runBlocking {
        val member = FamilyMemberEntity(
            id = "1",
            fullName = "John Doe",
            age = 30,
            gender = "Male",
            relationship = "Self",
            bloodGroup = "O+",
            phone = "123456789",
            notes = "Test notes",
            isActive = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        familyMemberDao.insert(member)
        familyMemberDao.delete(member)
        
        val fetched = familyMemberDao.getById("1")
        assertNull(fetched)
    }

    @Test
    fun observeActiveMembers() = runBlocking {
        val activeMember = FamilyMemberEntity(
            id = "1", fullName = "Active", age = 30, gender = "M", relationship = "Self",
            bloodGroup = "O+", phone = "1", notes = "", isActive = true,
            createdAt = 0, updatedAt = 0
        )
        val inactiveMember = FamilyMemberEntity(
            id = "2", fullName = "Inactive", age = 30, gender = "F", relationship = "Friend",
            bloodGroup = "A+", phone = "2", notes = "", isActive = false,
            createdAt = 0, updatedAt = 0
        )
        
        familyMemberDao.insert(activeMember)
        familyMemberDao.insert(inactiveMember)
        
        val activeMembers = familyMemberDao.observeActiveMembers().first()
        assertEquals(1, activeMembers.size)
        assertEquals("Active", activeMembers[0].fullName)
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadMedicine() = runBlocking {
        val medicine = MedicineEntity(
            id = "med1",
            name = "Napa",
            genericName = "Paracetamol",
            medicineType = "Tablet",
            strength = "500mg",
            unit = "Tablet",
            manufacturer = "Beximco",
            notes = "Common painkiller",
            isActive = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        medicineDao.insertMedicine(medicine)
        val fetchedMedicine = medicineDao.getMedicineById("med1")
        assertEquals(medicine.name, fetchedMedicine?.name)
    }

    @Test
    @Throws(Exception::class)
    fun assignMedicineToMember() = runBlocking {
        val assignment = MemberMedicineEntity(
            id = "assign1",
            familyMemberId = "1",
            medicineId = "med1",
            dosageQuantity = 1.0,
            frequencyPerDay = 3,
            dailyTotalQuantity = 3.0,
            instructions = "After meal",
            startDate = System.currentTimeMillis(),
            endDate = null,
            isActive = true,
            autoReduceStock = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        memberMedicineDao.insert(assignment)
        val fetched = memberMedicineDao.getById("assign1")
        assertNotNull(fetched)
        assertEquals("med1", fetched?.medicineId)
    }

    @Test
    @Throws(Exception::class)
    fun createMedicineStock() = runBlocking {
        val stock = MedicineStockEntity(
            id = "stock1",
            medicineId = "med1",
            currentQuantity = 50.0,
            unit = "Tablet",
            lowStockThreshold = 10.0,
            expiryDate = null,
            autoReduceEnabled = true,
            lastRefillDate = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        medicineStockDao.insert(stock)
        val fetched = medicineStockDao.getStockByMedicineId("med1")
        assertEquals(50.0, fetched?.currentQuantity)
    }

    @Test
    @Throws(Exception::class)
    fun insertStockTransaction() = runBlocking {
        val transaction = StockTransactionEntity(
            id = "trans1",
            medicineId = "med1",
            transactionType = "REFILL",
            quantity = 20.0,
            previousQuantity = 30.0,
            newQuantity = 50.0,
            reason = "Refill from pharmacy",
            createdAt = System.currentTimeMillis()
        )
        stockTransactionDao.insert(transaction)
        val fetched = stockTransactionDao.getById("trans1")
        assertEquals(20.0, fetched?.quantity)
    }

    @Test
    @Throws(Exception::class)
    fun observeLowStockMedicines() = runBlocking {
        val lowStock = MedicineStockEntity(
            id = "stock-low",
            medicineId = "med-low",
            currentQuantity = 3.0,
            unit = "Tablet",
            lowStockThreshold = 5.0,
            expiryDate = null,
            autoReduceEnabled = true,
            lastRefillDate = null,
            createdAt = 1L,
            updatedAt = 1L
        )
        val healthyStock = MedicineStockEntity(
            id = "stock-ok",
            medicineId = "med-ok",
            currentQuantity = 12.0,
            unit = "Tablet",
            lowStockThreshold = 5.0,
            expiryDate = null,
            autoReduceEnabled = true,
            lastRefillDate = null,
            createdAt = 1L,
            updatedAt = 1L
        )

        medicineStockDao.insertStock(lowStock)
        medicineStockDao.insertStock(healthyStock)

        val result = medicineStockDao.observeLowStockMedicines().first()
        assertEquals(1, result.size)
        assertEquals("med-low", result.first().medicineId)
    }

    @Test
    @Throws(Exception::class)
    fun observeTransactionsByMedicine() = runBlocking {
        val med1Transaction = StockTransactionEntity(
            id = "trans-med1",
            medicineId = "med1",
            transactionType = "REFILL",
            quantity = 10.0,
            previousQuantity = 0.0,
            newQuantity = 10.0,
            reason = "Refill",
            createdAt = 1L
        )
        val med2Transaction = StockTransactionEntity(
            id = "trans-med2",
            medicineId = "med2",
            transactionType = "REFILL",
            quantity = 5.0,
            previousQuantity = 0.0,
            newQuantity = 5.0,
            reason = "Refill",
            createdAt = 1L
        )

        stockTransactionDao.insertTransaction(med1Transaction)
        stockTransactionDao.insertTransaction(med2Transaction)

        val result = stockTransactionDao.observeTransactionsByMedicine("med1").first()
        assertEquals(1, result.size)
        assertEquals("trans-med1", result.first().id)
    }

    @Test
    @Throws(Exception::class)
    fun insertReminderLog() = runBlocking {
        val log = ReminderLogEntity(
            id = "log1",
            reminderId = "rem1",
            memberId = "1",
            medicineId = "med1",
            scheduledTime = System.currentTimeMillis(),
            actionTaken = "TAKEN",
            actionTime = System.currentTimeMillis(),
            notes = "On time",
            createdAt = System.currentTimeMillis()
        )
        reminderLogDao.insert(log)
        val fetched = reminderLogDao.getById("log1")
        assertEquals("TAKEN", fetched?.actionTaken)
    }

    @Test
    @Throws(Exception::class)
    fun observeReminderLogsByStatusAndFilters() = runBlocking {
        val taken = ReminderLogEntity(
            id = "log-taken",
            reminderId = "rem1",
            memberId = "member1",
            medicineId = "med1",
            scheduledTime = 10L,
            actionTaken = "TAKEN",
            actionTime = 11L,
            notes = null,
            createdAt = 11L
        )
        val skipped = ReminderLogEntity(
            id = "log-skipped",
            reminderId = "rem2",
            memberId = "member2",
            medicineId = "med2",
            scheduledTime = 20L,
            actionTaken = "SKIPPED",
            actionTime = 21L,
            notes = null,
            createdAt = 21L
        )

        reminderLogDao.insert(taken)
        reminderLogDao.insert(skipped)

        assertEquals(1, reminderLogDao.observeLogsByStatus("TAKEN").first().size)
        assertEquals(1, reminderLogDao.observeLogsByMember("member1").first().size)
        assertEquals(1, reminderLogDao.observeLogsByMedicine("med2").first().size)
        assertEquals(2, reminderLogDao.observeLogsByDateRange(0L, 30L).first().size)
        assertEquals(
            1,
            reminderLogDao.observeLogsByFilters(
                memberId = "member1",
                medicineId = "med1",
                status = "TAKEN",
                startTime = 0L,
                endTime = 15L
            ).first().size
        )
    }

    @Test
    @Throws(Exception::class)
    fun reportsDaoAggregatesReminderAndStockReports() = runBlocking {
        reminderLogDao.insert(
            ReminderLogEntity(
                id = "report-log-1",
                reminderId = "rem1",
                memberId = "member1",
                medicineId = "med1",
                scheduledTime = 10L,
                actionTaken = "TAKEN",
                actionTime = 11L,
                notes = null,
                createdAt = 11L
            )
        )
        medicineStockDao.insert(
            MedicineStockEntity(
                id = "report-stock-1",
                medicineId = "med1",
                currentQuantity = 2.0,
                unit = "Tablet",
                lowStockThreshold = 5.0,
                expiryDate = 5L,
                autoReduceEnabled = true,
                lastRefillDate = null,
                createdAt = 1L,
                updatedAt = 1L
            )
        )
        stockTransactionDao.insert(
            StockTransactionEntity(
                id = "report-transaction-1",
                medicineId = "med1",
                transactionType = "DOSE_TAKEN",
                quantity = 3.0,
                previousQuantity = 5.0,
                newQuantity = 2.0,
                reason = null,
                createdAt = 12L
            )
        )

        assertEquals(1, reportsDao.getReminderStatusCounts(0L, 20L).first().first().count)
        assertEquals(1, reportsDao.getLowStockReport().first().size)
        assertEquals(1, reportsDao.getExpiredMedicineReport(10L).first().size)
        assertEquals(3.0, reportsDao.getStockUsageReport(0L, 20L).first().first().totalConsumed ?: 0.0, 0.0)
    }
}
