package com.greildev.erdmovee.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.domain.usecase.UseCase
import com.greildev.erdmovee.utils.FlowState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val useCase: UseCase) : ViewModel() {
    private val _cartEntitites =
        MutableLiveData<List<CartMovieListEntities>>()
    val cartEntitites: LiveData<List<CartMovieListEntities>> = _cartEntitites

    private val _checkoutItemList = MutableLiveData<List<CartMovieListEntities>>()
    val checkoutItemList: LiveData<List<CartMovieListEntities>> = _checkoutItemList

    fun updateCheckoutItemAt(index: Int, item: CartMovieListEntities) {
        val list = _checkoutItemList.value.orEmpty().toMutableList()
        list[index] = item
        _cartEntitites.value = list
    }

    fun updateCartEntitiesAt(index: Int, item: CartMovieListEntities) {
        val list = _cartEntitites.value.orEmpty().toMutableList()
        list[index] = item
        _cartEntitites.value = list
    }

    fun deleteCartEntitiesAt(index: Int) {
        val list = _cartEntitites.value.orEmpty().toMutableList()
        list.removeAt(index)
        _cartEntitites.value = list
    }

    fun deleteCartEntitiesOnChecked() {
        val list = _cartEntitites.value.orEmpty().toMutableList()
        list.removeAll { it.isChecked }
        _cartEntitites.value = list
    }

    fun isAllChecked(isChecked: Boolean) {
        if (isChecked) {
            val list = _cartEntitites.value.orEmpty().toMutableList()
            list.forEach {
                it.isChecked = true
            }
            _cartEntitites.value = list
        } else {
            val list = _cartEntitites.value.orEmpty().toMutableList()
            list.forEach {
                it.isChecked = false
            }
            _cartEntitites.value = list
        }
    }

    val userData = runBlocking { useCase.userUseCase().userData().asLiveData() }

    fun getCartMovies() {
        viewModelScope.launch {
            useCase.cartUseCase().getCartMovies().collect {
                _cartEntitites.value = it
            }
        }
    }

    private fun saveCartMovie(cartMovieListEntities: CartMovieListEntities) {
        viewModelScope.launch {
            useCase.cartUseCase().saveCartMovie(cartMovieListEntities)
        }
    }

    fun replaceCartMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val cartMovies = _cartEntitites.value
            useCase.cartUseCase().replaceAllCart(cartMovies.orEmpty())
        }
    }

    fun updateQuantity(cartId: Int, newQuantity: Int, newQuantityPrice: Int) {
        viewModelScope.launch {
            useCase.cartUseCase().updateQuantity(cartId, newQuantity, newQuantityPrice)
        }
    }
    fun deleteCheckedCartByUid(isChecked: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().deleteCheckedByUid(isChecked)
        }
    }

    fun getCheckedCartByUid(isChecked: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().getCheckedCartByUid(isChecked).collect {
                _checkoutItemList.value = it
            }
        }
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