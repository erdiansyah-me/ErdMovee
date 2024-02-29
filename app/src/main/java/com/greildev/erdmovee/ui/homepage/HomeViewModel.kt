package com.greildev.erdmovee.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {
    val userData = useCase.userUsecase().userData.asLiveData()
    fun logout(): Boolean = runBlocking {
        useCase.userUsecase().userLogout()
        val userdata = useCase.userUsecase().userData.first()
        return@runBlocking userdata == null
    }


}