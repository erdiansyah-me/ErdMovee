package com.greildev.core.domain.usecase

import com.greildev.core.data.source.local.entities.CheckoutMovieListEntities
import com.greildev.core.domain.repository.MovieRepository
import com.greildev.core.domain.repository.UserRepository
import com.greildev.core.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddItemToCheckoutUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository,
    private val dispatcher: DispatcherProvider
) {
    suspend operator fun invoke(listItem: List<CheckoutMovieListEntities>) = withContext(dispatcher.default) {
        val userId = userRepository.currentUser()?.uid ?: ""
        val itemToAdd = listItem.map {
            it.copy(
                uid = userId
            )
        }
        movieRepository.insertListCheckoutItems(itemToAdd)
    }
}