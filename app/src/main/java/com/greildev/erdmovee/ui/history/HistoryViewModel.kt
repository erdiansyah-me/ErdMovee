package com.greildev.erdmovee.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.domain.usecase.UseCase
import com.greildev.core.utils.DispatcherProvider
import com.greildev.core.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCase: UseCase,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _transactionHistory = MutableStateFlow<UIState<List<TransactionDetail>>>(UIState.Loading())
    val transactionHistory: StateFlow<UIState<List<TransactionDetail>>> = _transactionHistory.asStateFlow()

    fun fetchAllTransactionHistory() {
        viewModelScope.launch(dispatcher.io) {
            useCase.paymentUseCase().getAllTransactionHistory()
                .collect { data -> _transactionHistory.value = data }
        }
    }
}
