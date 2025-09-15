package com.greildev.erdmovee.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val query = _query.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchMovie = query.flatMapLatest {
        if (it.isBlank()) {
            flowOf(PagingData.empty())
        } else {
            useCase.movieUseCase().searchMovies(it)
        }
    }.cachedIn(viewModelScope)


    fun setQuerySearch(query: String) = viewModelScope.launch {
        _query.update { query }
    }
}
