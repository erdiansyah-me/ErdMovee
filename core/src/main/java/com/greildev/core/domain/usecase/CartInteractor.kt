package com.greildev.core.domain.usecase

import com.greildev.core.data.repository.MovieRepository
import com.greildev.core.data.repository.UserRepository
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.utils.DataMapper.mapToCartEntities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first

class CartInteractor(
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository
) {
    suspend fun getCartMovies(): Flow<List<CartMovieListEntities>> = callbackFlow {
        val user = userRepository.userData().first()
        if (user != null) {
            movieRepository.getCartMoviesByUid(user.uid).collect {
                if (it.isNotEmpty()) {
                    trySend(it)
                } else {
                    trySend(it)
                }
            }
        } else {
            trySend(emptyList())
        }
        awaitClose()
    }

    suspend fun saveCartMovie(detailMovie: MovieDetailData) {
        val user = userRepository.userData().first()
        if (user != null) {
            movieRepository.saveCartMovie(detailMovie.mapToCartEntities(uid = user.uid))
        }
    }

    suspend fun deleteCartMovie(cartId: Int) {
        movieRepository.deleteCartMovie(cartId)
    }

    suspend fun checkCartMovieByUidAndId(movieId: Int): Boolean {
        val user = userRepository.userData().first()
        return if (user != null) {
            movieRepository.checkCartMovieByUidAndId(user.uid, movieId) > 0
        } else {
            false
        }
    }

    suspend fun updateQuantity(cartId: Int, newQuantity: Int, newQuantityPrice: Int) {
        movieRepository.updateQuantity(cartId, newQuantity, newQuantityPrice)
    }

    suspend fun isCheckedByCartId(cartId: Int, newIsChecked: Boolean) {
        movieRepository.isCheckedByCartId(cartId, newIsChecked)
    }

    suspend fun deleteCheckedByUid(isChecked: Boolean) {
        val user = userRepository.userData().first()
        if (user != null) {
            movieRepository.deleteCheckedByUid(isChecked, user.uid)
        }
    }

    suspend fun getCheckedCartByUid(
        isChecked: Boolean,
    ): Flow<List<CartMovieListEntities>> =
        callbackFlow {
            val user = userRepository.userData().first()
            if (user != null) {
                movieRepository.getCheckedCartByUid(isChecked, user.uid).collect {
                    if (it.isNotEmpty()) {
                        trySend(it)
                    } else {
                        trySend(it)
                    }
                }
            } else {
                trySend(emptyList())
            }
            awaitClose()
        }

}