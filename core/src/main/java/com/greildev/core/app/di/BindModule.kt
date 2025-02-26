package com.greildev.core.app.di

import com.greildev.core.data.repository.MovieRepository
import com.greildev.core.data.repository.MovieRepositoryImpl
import com.greildev.core.data.repository.PaymentRepository
import com.greildev.core.data.repository.PaymentRepositoryImpl
import com.greildev.core.data.repository.UserRepositoryImpl
import com.greildev.core.domain.repository.UserRepository
import com.greildev.core.domain.usecase.Interactor
import com.greildev.core.domain.usecase.UseCase
import com.greildev.core.domain.usecase.UserInteractor
import com.greildev.core.domain.usecase.UserUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BindModule {
    @Binds
    abstract fun provideUseCase(interactor: Interactor): UseCase

    @Binds
    abstract fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun provideMovieRepository(movieRepositoryImpl: MovieRepositoryImpl): MovieRepository

    @Binds
    abstract fun providePaymentRepository(paymentRepositoryImpl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    abstract fun provideUserUseCase(userInteractor: UserInteractor): UserUseCase
}