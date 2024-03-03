package com.greildev.erdmovee.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {
    val userData = runBlocking { useCase.userUseCase().userData().asLiveData() }
    fun logout(): Boolean = runBlocking {
        useCase.userUseCase().userLogout()
        val userdata = useCase.userUseCase().userData().first()
        return@runBlocking userdata == null
    }

    fun getPopularMovies() = runBlocking {
        useCase.movieUseCase().getPopularMovies().cachedIn(viewModelScope)
    }

    fun getNowPlayingMovies() = runBlocking {
        useCase.movieUseCase().getNowPlayingMovies().cachedIn(viewModelScope)
    }

    private val _tokenUser = MutableStateFlow(0)
    val tokenUser: StateFlow<Int> = _tokenUser
    fun getTokenUser(userId: String) {
        viewModelScope.launch {
            _tokenUser.value = useCase.paymentUseCase().getTokenUser(userId).first()
        }
    }
}