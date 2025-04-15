package com.example.first_quest.di

import com.example.first_quest.utils.ConnectivityChecker
import com.example.first_quest.utils.ConnectivityCheckerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityChecker(
        connectivityCheckerImpl: ConnectivityCheckerImpl
    ): ConnectivityChecker
}