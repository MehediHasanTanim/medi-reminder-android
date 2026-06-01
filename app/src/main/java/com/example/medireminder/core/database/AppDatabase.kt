package com.example.medireminder.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.medireminder.core.database.dao.*
import com.example.medireminder.core.database.entities.*

@Database(
    entities = [
        FamilyMemberEntity::class,
        MedicineEntity::class,
        MemberMedicineEntity::class,
        MedicineStockEntity::class,
        ReminderEntity::class,
        ReminderLogEntity::class,
        StockTransactionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun medicineDao(): MedicineDao
    abstract fun memberMedicineDao(): MemberMedicineDao
    abstract fun medicineStockDao(): MedicineStockDao
    abstract fun reminderDao(): ReminderDao
    abstract fun reminderLogDao(): ReminderLogDao
    abstract fun stockTransactionDao(): StockTransactionDao
    abstract fun reportsDao(): ReportsDao

    companion object {
        const val DATABASE_NAME = "medireminder_db"
    }
}
