package com.greildev.core.domain.usecase

import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.domain.repository.MovieRepository
import com.greildev.core.domain.repository.UserRepository
import com.greildev.core.utils.DataMapper.mapToCartEntities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// TODO: Need to refactor to new usecase
class CartInteractor(
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository
) {

    fun getCartMovies(): Flow<List<CartMovieListEntities>> = callbackFlow {
        val user = userRepository.currentUser()
        if (user != null) {
            movieRepository.getCartMoviesByUid(user.uid).collect {
                trySend(it)
            }
        } else {
            trySend(emptyList())
        }
        awaitClose()
    }

    suspend fun saveCartMovie(detailMovie: MovieDetailData, isChecked: Boolean) {
        val user = userRepository.currentUser()
        if (user != null) {
            movieRepository.saveCartMovie(detailMovie.mapToCartEntities(uid = user.uid, isChecked))
        }
    }

    suspend fun deleteCartMovie(cartId: Int) {
        movieRepository.deleteCartMovie(cartId)
    }

    suspend fun checkCartMovieByUidAndId(movieId: Int): Boolean {
        val user = userRepository.currentUser()
        return if (user != null) {
            movieRepository.checkCartMovieByUidAndId(user.uid, movieId) > 0
        } else {
            false
        }
    }

    suspend fun deleteCheckedByUid(isChecked: Boolean) {
        val user = userRepository.currentUser()
        if (user != null) {
            movieRepository.deleteCheckedByUid(isChecked, user.uid)
        }
    }

    suspend fun replaceAllCart(carts: List<CartMovieListEntities>) {
        movieRepository.replaceAllCart(carts)
    }
}
