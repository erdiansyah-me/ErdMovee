package com.greildev.core.domain.usecase

import com.greildev.core.data.repository.MovieRepository
import com.greildev.core.data.repository.PaymentRepository
import com.greildev.core.data.repository.UserRepository
import javax.inject.Inject

class Interactor @Inject constructor(
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository,
    private val paymentRepository: PaymentRepository
) : UseCase{
    override fun userUseCase(): UserInteractor {
        return UserInteractor(userRepository)
    }

    override fun movieUseCase(): MovieInteractor {
        return MovieInteractor(movieRepository)
    }

    override fun cartUseCase(): CartInteractor {
        return CartInteractor(movieRepository, userRepository)
    }

    override fun favoriteUseCase(): FavoriteInteractor {
        return FavoriteInteractor(movieRepository, userRepository)
    }

    override fun paymentUseCase(): PaymentInteractor {
        return PaymentInteractor(paymentRepository)
    }
}