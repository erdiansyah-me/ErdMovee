package com.greildev.erdmovee.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.greildev.core.domain.model.MovieListData
import com.greildev.core.domain.model.UserData
import com.greildev.core.domain.usecase.UseCase
import com.greildev.core.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: UseCase,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData : StateFlow<UserData?> = _userData.asStateFlow()

    private val _popularMovies = MutableStateFlow<PagingData<MovieListData>>(PagingData.empty())
    val popularMovies : StateFlow<PagingData<MovieListData>> = _popularMovies.asStateFlow()

    private val _nowPlayingMovies = MutableStateFlow<PagingData<MovieListData>>(PagingData.empty())
    val nowPlayingMovies : StateFlow<PagingData<MovieListData>> = _nowPlayingMovies.asStateFlow()

    fun fetchUserData() = viewModelScope.launch(dispatcher.io) {
        useCase.userUseCase().userData().collectLatest {
            _userData.value = it
        }
    }
    fun logout() = viewModelScope.launch(dispatcher.io) {
        useCase.userUseCase().userLogout()
        fetchUserData()
    }

    fun getPopularMovies() = viewModelScope.launch {
        useCase.movieUseCase().getPopularMovies()
            .cachedIn(viewModelScope)
            .collectLatest { _popularMovies.value = it }
    }

    fun getNowPlayingMovies() = viewModelScope.launch {
        useCase.movieUseCase().getNowPlayingMovies()
            .cachedIn(viewModelScope)
            .collectLatest { _nowPlayingMovies.value = it }
    }

    private val _tokenUser = MutableStateFlow(0)
    val tokenUser: StateFlow<Int> = _tokenUser
    fun getTokenUser() {
        viewModelScope.launch {
            _tokenUser.value = useCase.paymentUseCase().getTokenUser().first()
        }
    }
}
