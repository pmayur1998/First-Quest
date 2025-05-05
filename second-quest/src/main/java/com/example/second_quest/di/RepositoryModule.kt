package com.example.second_quest.di

import com.example.second_quest.data.repository.ProductRepositoryImpl
import com.example.second_quest.domain.repository.ProductRepository
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
    abstract fun bindEventRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
}