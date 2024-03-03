package com.greildev.erdmovee.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {

    fun searchMovie(query: String) = runBlocking {
        useCase.movieUseCase().searchMovies(query).cachedIn(viewModelScope)
    }
}