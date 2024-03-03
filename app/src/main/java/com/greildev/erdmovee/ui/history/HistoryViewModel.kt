package com.greildev.erdmovee.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val useCase: UseCase) : ViewModel() {

    val userData = runBlocking { useCase.userUseCase().userData().asLiveData() }

    fun getAllTransactionHistory(userId: String) = runBlocking {
        useCase.paymentUseCase().getAllTransactionHistory(userId)
    }
}