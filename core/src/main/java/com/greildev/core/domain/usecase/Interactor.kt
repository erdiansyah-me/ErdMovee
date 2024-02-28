package com.greildev.core.domain.usecase

import com.greildev.core.data.repository.UserRepository
import javax.inject.Inject

class Interactor @Inject constructor(
    private val userRepository: UserRepository
) : UseCase{
    override fun userUsecase(): UserInteractor {
        return UserInteractor(userRepository)
    }
}