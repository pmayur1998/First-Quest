package com.example.common.di

import com.example.common.utils.ConnectivityChecker
import com.example.common.utils.ConnectivityCheckerImpl
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