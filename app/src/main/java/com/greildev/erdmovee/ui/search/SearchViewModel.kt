package com.greildev.erdmovee.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.greildev.core.domain.model.MovieListData
import com.greildev.core.domain.usecase.UseCase
import com.greildev.core.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val useCase: UseCase,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _searchMovie = MutableStateFlow<PagingData<MovieListData>>(PagingData.empty())
    val searchMovie = _searchMovie.asStateFlow()

    fun searchMovie(query: String) = viewModelScope.launch(dispatcher.io) {
        useCase.movieUseCase().searchMovies(query).cachedIn(viewModelScope).collect {
            _searchMovie.value = it
        }
    }
}
