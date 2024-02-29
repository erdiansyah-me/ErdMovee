package com.greildev.core.app.di

import com.greildev.core.data.repository.UserRepository
import com.greildev.core.data.repository.UserRepositoryImpl
import com.greildev.core.domain.usecase.Interactor
import com.greildev.core.domain.usecase.UseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {
    @Binds
    abstract fun provideUseCase(interactor: Interactor): UseCase

    @Binds
    abstract fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

}