package com.greildev.core.domain.usecase

import com.greildev.core.data.repository.PaymentRepository
import com.greildev.core.domain.repository.MovieRepository
import com.greildev.core.domain.repository.UserRepository
import com.greildev.core.utils.DispatcherProvider
import javax.inject.Inject

class Interactor @Inject constructor(
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository,
    private val paymentRepository: PaymentRepository,
    private val dispatcher: DispatcherProvider
) : UseCase{
    override fun userUseCase(): UserUseCase {
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
        return PaymentInteractor(paymentRepository, userRepository, dispatcher)
    }
}
