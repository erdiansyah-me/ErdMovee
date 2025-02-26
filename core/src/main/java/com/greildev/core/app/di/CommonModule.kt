package com.greildev.core.app.di

import com.greildev.core.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {
    @Provides
    fun provideDispatcher() = DispatcherProvider()
}