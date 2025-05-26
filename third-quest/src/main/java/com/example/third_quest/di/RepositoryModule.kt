package com.example.third_quest.di

import com.example.third_quest.data.repository.StatsRepositoryImpl
import com.example.third_quest.domain.repository.StatsRepository
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
    abstract fun provideStatsRepository(
        statsRepositoryImpl: StatsRepositoryImpl
    ): StatsRepository
}