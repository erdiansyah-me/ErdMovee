package com.greildev.core.domain.usecase

import com.greildev.core.data.repository.MovieRepository
import com.greildev.core.data.repository.UserRepository
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.utils.DataMapper.mapToFavoriteEntities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first

class FavoriteInteractor(
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository
) {
    suspend fun getFavoriteMovies(): Flow<List<FavoriteMovieListEntities>> = callbackFlow {
        val user = userRepository.userData().first()
        if (user != null) {
            movieRepository.getFavoriteMoviesByUid(user.uid).collect {
                if (it.isNotEmpty()){
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

    suspend fun saveFavoriteMovie(detailMovie: MovieDetailData) {
        val user = userRepository.userData().first()
        if (user != null) {
            movieRepository.saveFavoriteMovie(detailMovie.mapToFavoriteEntities(uid = user.uid))
        }
    }

    suspend fun deleteFavoriteMovie(favoriteId: Int) {
        movieRepository.deleteFavoriteMovie(favoriteId)
    }

    suspend fun deleteFavoriteMovieByIdAndUid(movieId: Int) {
        val user = userRepository.userData().first()
        if (user != null) {
            movieRepository.deleteFavoriteMovieByIdAndUid(user.uid, movieId)
        }
    }

    suspend fun checkFavoriteMovie(movieId: Int): Boolean {
        val user = userRepository.userData().first()
        return if (user != null) {
            movieRepository.checkFavoriteMovie(movieId, user.uid) > 0
        } else {
            false
        }
    }

}