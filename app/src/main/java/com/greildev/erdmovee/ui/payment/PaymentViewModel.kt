package com.greildev.erdmovee.ui.payment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.data.source.local.entities.CheckoutMovieListEntities
import com.greildev.core.domain.model.PaymentStatusModel
import com.greildev.core.domain.repository.MovieRepository
import com.greildev.core.domain.usecase.AddItemToCheckoutUseCase
import com.greildev.core.domain.usecase.DoCheckoutMovieUseCase
import com.greildev.core.domain.usecase.UseCase
import com.greildev.core.utils.UIState
import com.greildev.erdmovee.app.App
import com.greildev.erdmovee.ui.model.CheckoutMovieUIModel
import com.greildev.erdmovee.utils.FlowState
import com.greildev.erdmovee.utils.toUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@Suppress("TooManyFunctions")
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val useCase: UseCase,
    private val movieRepository: MovieRepository,
    private val checkoutMovieUseCase: DoCheckoutMovieUseCase,
    private val addItemToCheckoutUseCase: AddItemToCheckoutUseCase,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _cartEntitites =
        MutableLiveData<List<CartMovieListEntities>>()
    val cartEntitites: LiveData<List<CartMovieListEntities>> = _cartEntitites

    private val app = context as App

    private val _checkoutItemList = MutableLiveData<List<CheckoutMovieUIModel>>()
    val checkoutItemList: LiveData<List<CheckoutMovieUIModel>> = _checkoutItemList
    fun updateCheckoutItemAt(index: Int, item: CheckoutMovieUIModel) = viewModelScope.launch {
        val list = _checkoutItemList.value.orEmpty().toMutableList()
        list[index] = item
        _checkoutItemList.value = list
    }

    fun updateCartEntitiesAt(index: Int, item: CartMovieListEntities) {
        val list = _cartEntitites.value.orEmpty().toMutableList()
        list[index] = item
        _cartEntitites.value = list
    }

    fun deleteCartEntitiesOnChecked() {
        val list = _cartEntitites.value.orEmpty().toMutableList()
        list.removeAll { it.isChecked }
        _cartEntitites.value = list
    }

    fun isAllChecked(isChecked: Boolean) {
        val list = _cartEntitites.value.orEmpty().toMutableList()
        list.forEach {
            it.isChecked = isChecked
        }
        _cartEntitites.value = list
    }

    fun getCartMovies() {
        viewModelScope.launch {
            useCase.cartUseCase().getCartMovies().collect {
                _cartEntitites.value = it
            }
        }
    }

    fun replaceCartMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val cartMovies = _cartEntitites.value
            useCase.cartUseCase().replaceAllCart(cartMovies.orEmpty())
        }
    }

    fun addItemsToCheckout() {
        viewModelScope.launch {
            val cartMovies = _cartEntitites.value?.map {
                CheckoutMovieListEntities(
                    id = it.id,
                    originalTitle = it.originalTitle,
                    title = it.title,
                    posterPath = it.posterPath,
                    backdropPath = it.backdropPath,
                    releaseDate = it.releaseDate,
                    basePrice = it.basePrice,
                    quantityItem = it.quantityItem,
                    quantityPrice = it.quantityPrice
                )
            }
            if (cartMovies != null) {
                addItemToCheckoutUseCase.invoke(cartMovies)
            }
        }
    }

    fun deleteCheckedCartByUid(isChecked: Boolean) {
        viewModelScope.launch {
            useCase.cartUseCase().deleteCheckedByUid(isChecked)
        }
    }

    fun getAllCheckoutItems() {
        viewModelScope.launch {
            movieRepository.getAllCheckoutItems().collect { list ->
                _checkoutItemList.value = list.map { it.toUIModel() }
            }
        }
    }

    fun deleteCheckoutItems() {
        viewModelScope.launch {
            movieRepository.getAllCheckoutItems().collect { list ->
                movieRepository.deleteCheckoutItemsById(list.map { it.id })
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
    fun getTokenUser() {
        viewModelScope.launch {
            _tokenUser.value = useCase.paymentUseCase().getTokenUser().first()
        }
    }

    private val _isUpdateSuccess = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val isUpdateSuccess: StateFlow<FlowState<Boolean>> = _isUpdateSuccess

    fun updateTokenUser(token: Int) {
        viewModelScope.launch {
            _isUpdateSuccess.value =
                FlowState.FlowValue(useCase.paymentUseCase().updateTokenUser(token).first())
        }
    }

    private val _paymentStatusModel =
        MutableStateFlow<FlowState<PaymentStatusModel>>(FlowState.FlowCreated)
    val paymentStatusModel: StateFlow<FlowState<PaymentStatusModel>> = _paymentStatusModel

    fun checkoutMovie() {
        viewModelScope.launch {
            when (val flowData = checkoutMovieUseCase.invoke(generateTransactionId()).last()) {
                is UIState.Loading -> {
                    _paymentStatusModel.value =
                        FlowState.FlowValue(PaymentStatusModel(isLoading = true, isSuccess = false))
                }
                is UIState.Success<Boolean> -> {
                    val isSuccess = flowData.data ?: false
                    if (isSuccess) {
                        updateTokenUser(
                            tokenUser.last().minus(
                                checkoutItemList.value.orEmpty()
                                    .sumOf { it.quantityPrice })
                        )
                        val transactionId = generateTransactionId()
                        _paymentStatusModel.value = FlowState.FlowValue(
                            PaymentStatusModel(
                                isSuccess = true,
                                isLoading = false,
                                transactionDetail = TransactionDetail(
                                    transactionId = transactionId,
                                    itemList = movieRepository.getAllCheckoutItems().last(),
                                    transactionDate = Date(
                                        app.trustedTimeClient?.computeCurrentUnixEpochMillis()
                                            ?: System.currentTimeMillis()
                                    ).toString(),
                                    amountToken = checkoutItemList.value.orEmpty()
                                        .sumOf { it.quantityPrice },
                                )
                            )
                        )
                    } else {
                        _paymentStatusModel.value =
                            FlowState.FlowValue(PaymentStatusModel(isSuccess = false, isLoading = false))
                    }
                }
                else -> {}
            }

        }
    }

    private val _isWriteTokenTransaction =
        MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val isWriteTokenTransaction: StateFlow<FlowState<Boolean>> = _isWriteTokenTransaction
    fun writeTokenTransaction(transactionToken: TransactionToken) {
        viewModelScope.launch {
            _isWriteTokenTransaction.value = FlowState.FlowValue(
                useCase.paymentUseCase().writeTokenTransaction(transactionToken).first()
            )
        }
    }

    fun generateTransactionId(): String {
        val dateTimeNow = app.trustedTimeClient?.computeCurrentUnixEpochMillis()
            ?: System.currentTimeMillis()
        return UUID.randomUUID().toString() + "_" + dateTimeNow.toString()
    }
}
