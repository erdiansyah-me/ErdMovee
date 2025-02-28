package com.greildev.erdmovee.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.domain.usecase.UseCase
import com.greildev.erdmovee.ui.model.MovieDetailUIData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {

    private val _movieDetail = MutableStateFlow(MovieDetailUIData())
    val movieDetail: StateFlow<MovieDetailUIData> = _movieDetail

    fun getMovieDetailData(movieId: Int) {
        viewModelScope.launch {
            val movieDetail = async {  useCase.movieUseCase().getMovieDetail(movieId) }
            val movieRecommend = async { useCase.movieUseCase().getRecommendationMovies(movieId) }
            combine(
                movieDetail.await(), movieRecommend.await()
            ) { detail, recom ->
                MovieDetailUIData(detail, recom)
            }.collect {
                _movieDetail.value = it
            }
        }
    }

    fun deleteFavoriteMovieById(movieId: Int) {
        viewModelScope.launch {
            useCase.favoriteUseCase().deleteFavoriteMovieByIdAndUid(movieId)
        }
    }

    fun saveFavoriteMovie(detailMovie: MovieDetailData) {
        viewModelScope.launch {
            useCase.favoriteUseCase().saveFavoriteMovie(detailMovie)
        }
    }

    fun checkFavoriteMovieById(movieId: Int) = runBlocking {
        useCase.favoriteUseCase().checkFavoriteMovie(movieId)
    }

    fun checkCartMovieById(movieId: Int) = runBlocking {
        useCase.cartUseCase().checkCartMovieByUidAndId(movieId)
    }

    fun saveCartMovie(detailMovie: MovieDetailData, isRentNow: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().saveCartMovie(detailMovie, isRentNow)
        }
    }

    fun deleteCartMovie(cartId: Int) {
        viewModelScope.launch {
            useCase.cartUseCase().deleteCartMovie(cartId)
        }
    }

    fun isCheckedByCartId(cartId: Int, newIsChecked: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().isCheckedByCartId(cartId, newIsChecked)
        }
    }

    fun getCartMovies() = runBlocking {
        useCase.cartUseCase().getCartMovies()
    }
}
