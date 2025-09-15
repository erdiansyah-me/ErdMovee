package com.greildev.erdmovee.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.greildev.core.data.source.local.entities.CheckoutMovieListEntities
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.domain.usecase.AddItemToCheckoutUseCase
import com.greildev.core.domain.usecase.UseCase
import com.greildev.core.utils.UIState
import com.greildev.erdmovee.ui.model.MovieDetailUIData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val useCase: UseCase,
    private val addItemToCheckout: AddItemToCheckoutUseCase
) : ViewModel() {
    private val _movieDataState: MutableStateFlow<UIState<MovieDetailData>> =
        MutableStateFlow(UIState.NoState())
    private val _check = MutableStateFlow(false)
    private val _fav = MutableStateFlow(false)

    val getMovieDetailData: StateFlow<MovieDetailUIData> =
        combine(
            _movieDataState, _check, _fav
        ) { movie, cek, fav ->
            MovieDetailUIData(movie, cek, fav)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MovieDetailUIData()
        )

    fun getMovieRecommend(movieId: Int) =
        useCase.movieUseCase().getRecommendationMovies(movieId).cachedIn(viewModelScope)

    fun getMovieDetail(movieId: Int) = viewModelScope.launch {
        _movieDataState.value = useCase.movieUseCase().getMovieDetail(movieId).last()
    }

    fun checkFMovie(movieId: Int) = viewModelScope.launch {
        _fav.value = useCase.favoriteUseCase().checkFavoriteMovie(movieId)
    }

    fun checkCartMovie(movieId: Int) = viewModelScope.launch {
        _check.value = useCase.cartUseCase().checkCartMovieByUidAndId(movieId)
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

    fun saveCartMovie(detailMovie: MovieDetailData, isRentNow: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().saveCartMovie(detailMovie, isRentNow)
        }
    }

    fun addItemToCheckout(detailMovieData: MovieDetailData) {
        viewModelScope.launch {
            addItemToCheckout(
                listOf(
                    CheckoutMovieListEntities(
                        id = detailMovieData.id,
                        originalTitle = detailMovieData.originalTitle,
                        title = detailMovieData.title,
                        posterPath = detailMovieData.posterPath,
                        backdropPath = detailMovieData.backdropPath,
                        releaseDate = detailMovieData.releaseDate,
                        basePrice = detailMovieData.price,
                        quantityItem = 1,
                        quantityPrice = detailMovieData.price,
                    )
                )
            )
        }
    }
}
