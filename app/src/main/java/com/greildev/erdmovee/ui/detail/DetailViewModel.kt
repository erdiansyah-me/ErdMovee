package com.greildev.erdmovee.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.domain.usecase.UseCase
import com.greildev.core.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {

    private val _movieDetail = MutableStateFlow<UIState<MovieDetailData>>(UIState.NoState())
    val movieDetail: StateFlow<UIState<MovieDetailData>> = _movieDetail
    fun getMovieDetail(movieId: Int) {
        viewModelScope.launch {
            useCase.movieUseCase().getMovieDetail(movieId).collect {
                _movieDetail.value = it
            }
        }
    }

    fun getRecommendation(movieId: Int) = runBlocking {
        useCase.movieUseCase().getRecommendationMovies(movieId).cachedIn(viewModelScope)
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

    fun saveCartMovie(detailMovie: MovieDetailData) {
        viewModelScope.launch {
            useCase.cartUseCase().saveCartMovie(detailMovie)
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