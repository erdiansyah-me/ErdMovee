package com.greildev.core.domain.usecase

interface UseCase {
    fun userUseCase(): UserUseCase
    fun movieUseCase(): MovieInteractor
    fun cartUseCase(): CartInteractor
    fun favoriteUseCase(): FavoriteInteractor
    fun paymentUseCase(): PaymentInteractor
}