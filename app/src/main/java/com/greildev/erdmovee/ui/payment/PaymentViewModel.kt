package com.greildev.erdmovee.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.domain.usecase.UseCase
import com.greildev.erdmovee.utils.FlowState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val useCase: UseCase) : ViewModel() {

    val userData = runBlocking { useCase.userUseCase().userData().asLiveData() }
    fun getCartMovies() = runBlocking {
        useCase.cartUseCase().getCartMovies()
    }

    fun deleteCartMovie(cartId: Int) {
        viewModelScope.launch {
            useCase.cartUseCase().deleteCartMovie(cartId)
        }
    }

    fun updateQuantity(cartId: Int, newQuantity: Int, newQuantityPrice: Int) {
        viewModelScope.launch {
            useCase.cartUseCase().updateQuantity(cartId, newQuantity, newQuantityPrice)
        }
    }

    fun isCheckedByCartId(cartId: Int, newIsChecked: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().isCheckedByCartId(cartId, newIsChecked)
        }
    }

    fun deletedCheckedCartByCartId(cartId: Int, isChecked: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().isCheckedByCartId(cartId = cartId, newIsChecked = isChecked)
        }
    }

    fun deleteCheckedCartByUid(isChecked: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().deleteCheckedByUid(isChecked)
        }
    }

    fun getCheckedCartByUid(isChecked: Boolean) = runBlocking {
        useCase.cartUseCase().getCheckedCartByUid(isChecked)
    }

    fun getPaymentList() = runBlocking {
        useCase.paymentUseCase().getPaymentList()
    }

    fun getPaymentListUpdate() = runBlocking {
        useCase.paymentUseCase().getPaymentListUpdate()
    }

    private val _tokenUser = MutableStateFlow(0)
    val tokenUser: StateFlow<Int> = _tokenUser
    fun getTokenUser(userId: String) {
        viewModelScope.launch {
            _tokenUser.value = useCase.paymentUseCase().getTokenUser(userId).first()
        }
    }

    private val _isUpdateSuccess = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val isUpdateSuccess: StateFlow<FlowState<Boolean>> = _isUpdateSuccess

    fun updateTokenUser(userId: String, token: Int) {
        viewModelScope.launch {
            _isUpdateSuccess.value =
                FlowState.FlowValue(useCase.paymentUseCase().updateTokenUser(userId, token).first())
        }
    }

    private val _isWriteTransactionHistory =
        MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val isWriteTransactionHistory: StateFlow<FlowState<Boolean>> = _isWriteTransactionHistory

    fun writeTransactionHistory(userId: String, transactionDetail: TransactionDetail) {
        viewModelScope.launch {
            _isWriteTransactionHistory.value = FlowState.FlowValue(
                useCase.paymentUseCase().writeTransactionHistory(userId, transactionDetail).first()
            )
        }
    }

    private val _isWriteTokenTransaction =
        MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val isWriteTokenTransaction: StateFlow<FlowState<Boolean>> = _isWriteTokenTransaction
    fun writeTokenTransaction(userId: String, transactionToken: TransactionToken) {
        viewModelScope.launch {
            _isWriteTokenTransaction.value = FlowState.FlowValue(
                useCase.paymentUseCase().writeTokenTransaction(userId, transactionToken).first()
            )
        }
    }

    fun generateTransactionId(): String {
        return UUID.randomUUID().toString()
    }
}