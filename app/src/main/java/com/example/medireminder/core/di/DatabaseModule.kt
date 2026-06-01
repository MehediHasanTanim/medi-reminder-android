package com.example.medireminder.core.di

import android.content.Context
import androidx.room.Room
import com.example.medireminder.core.database.AppDatabase
import com.example.medireminder.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideFamilyMemberDao(database: AppDatabase): FamilyMemberDao = database.familyMemberDao()

    @Provides
    fun provideMedicineDao(database: AppDatabase): MedicineDao = database.medicineDao()

    @Provides
    fun provideMemberMedicineDao(database: AppDatabase): MemberMedicineDao = database.memberMedicineDao()

    @Provides
    fun provideMedicineStockDao(database: AppDatabase): MedicineStockDao = database.medicineStockDao()

    @Provides
    fun provideReminderDao(database: AppDatabase): ReminderDao = database.reminderDao()

    @Provides
    fun provideReminderLogDao(database: AppDatabase): ReminderLogDao = database.reminderLogDao()

    @Provides
    fun provideStockTransactionDao(database: AppDatabase): StockTransactionDao = database.stockTransactionDao()

    @Provides
    fun provideReportsDao(database: AppDatabase): ReportsDao = database.reportsDao()
}
