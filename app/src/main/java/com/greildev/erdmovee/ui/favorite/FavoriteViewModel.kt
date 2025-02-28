package com.greildev.erdmovee.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {
    val getFavoriteMovieList: StateFlow<List<FavoriteMovieListEntities>> =
        useCase.favoriteUseCase().getFavoriteMovies().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun deleteFavoriteMovie(favoriteId: Int) {
        viewModelScope.launch {
            useCase.favoriteUseCase().deleteFavoriteMovie(favoriteId)
        }
    }
}
