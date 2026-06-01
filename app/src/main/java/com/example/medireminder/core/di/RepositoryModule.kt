package com.example.medireminder.core.di

import com.example.medireminder.features.family.data.repository.FamilyMemberRepositoryImpl
import com.example.medireminder.features.dashboard.data.repository.DashboardRepositoryImpl
import com.example.medireminder.features.dashboard.domain.repository.DashboardRepository
import com.example.medireminder.features.family.domain.repository.FamilyMemberRepository
import com.example.medireminder.features.history.data.repository.ReminderHistoryRepositoryImpl
import com.example.medireminder.features.history.domain.repository.ReminderHistoryRepository
import com.example.medireminder.features.medicine.data.repository.MedicineRepositoryImpl
import com.example.medireminder.features.medicine.domain.repository.MedicineRepository
import com.example.medireminder.features.membermedicine.data.repository.MemberMedicineRepositoryImpl
import com.example.medireminder.features.membermedicine.domain.repository.MemberMedicineRepository
import com.example.medireminder.features.reminder.data.repository.ReminderRepositoryImpl
import com.example.medireminder.features.reminder.domain.repository.ReminderRepository
import com.example.medireminder.features.reports.data.repository.ReportsRepositoryImpl
import com.example.medireminder.features.reports.domain.repository.ReportsRepository
import com.example.medireminder.features.settings.data.repository.SettingsRepositoryImpl
import com.example.medireminder.features.settings.domain.repository.SettingsRepository
import com.example.medireminder.features.stock.data.repository.MedicineStockRepositoryImpl
import com.example.medireminder.features.stock.domain.repository.MedicineStockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFamilyMemberRepository(
        impl: FamilyMemberRepositoryImpl
    ): FamilyMemberRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        impl: DashboardRepositoryImpl
    ): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindMedicineRepository(
        impl: MedicineRepositoryImpl
    ): MedicineRepository

    @Binds
    @Singleton
    abstract fun bindMemberMedicineRepository(
        impl: MemberMedicineRepositoryImpl
    ): MemberMedicineRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindReportsRepository(
        impl: ReportsRepositoryImpl
    ): ReportsRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindMedicineStockRepository(
        impl: MedicineStockRepositoryImpl
    ): MedicineStockRepository

    @Binds
    @Singleton
    abstract fun bindReminderHistoryRepository(
        impl: ReminderHistoryRepositoryImpl
    ): ReminderHistoryRepository
}
