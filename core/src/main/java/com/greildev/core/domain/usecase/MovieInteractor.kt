package com.greildev.core.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.greildev.core.data.repository.MovieRepository
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.domain.model.MovieListData
import com.greildev.core.utils.DataMapper.mapToModel
import com.greildev.core.utils.UIState
import com.greildev.core.utils.suspendSubscribe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class MovieInteractor(
    private val movieRepository: MovieRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getPopularMovies(): Flow<PagingData<MovieListData>> {
        return movieRepository.getPopularMovies().mapLatest { paging ->
            paging.map {
                it.mapToModel()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getNowPlayingMovies(): Flow<PagingData<MovieListData>> {
        return movieRepository.getNowPlayingMovies().mapLatest { paging ->
            paging.map {
                it.mapToModel()
            }
        }
    }

    suspend fun searchMovies(
        query: String,
    ): Flow<PagingData<MovieListData>> {
        return movieRepository.searchMovies(query).map { pagingData ->
            pagingData.map {
                it.mapToModel()
            }
        }
    }

    suspend fun getRecommendationMovies(movieId: Int): Flow<PagingData<MovieListData>> {
        return movieRepository.getRecommendationMovies(movieId).map { pagingData ->
            pagingData.map {
                it.mapToModel()
            }
        }
    }

    suspend fun getMovieDetail(id: Int): Flow<UIState<MovieDetailData>> = flow {
        emit(UIState.Loading())
        movieRepository.getMovieDetail(id).collect {
            it.suspendSubscribe(
                onSuccess = { result ->
                    if (result.data != null) {
                        emit(UIState.Success(result.data.mapToModel()))
                    } else {
                        emit(UIState.Error(code = 404, errorMessage = "Movie not found"))
                    }
                },
                onError = { exception ->
                    emit(UIState.Error(code = 600, errorMessage = exception.message))
                }
            )
        }
    }
}