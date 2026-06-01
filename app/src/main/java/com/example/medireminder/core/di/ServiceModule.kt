package com.example.medireminder.core.di

import com.example.medireminder.features.stock.data.repository.StockReductionServiceImpl
import com.example.medireminder.features.stock.domain.StockReductionService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindStockReductionService(
        impl: StockReductionServiceImpl
    ): StockReductionService
}
