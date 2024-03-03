package com.greildev.erdmovee.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {
    val getFavoriteMovieList =
        runBlocking { useCase.favoriteUseCase().getFavoriteMovies().asLiveData() }
    fun deleteFavoriteMovie(favoriteId: Int) {
        viewModelScope.launch {
            useCase.favoriteUseCase().deleteFavoriteMovie(favoriteId)
        }
    }
}