package com.greildev.core.domain.usecase

import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.data.repository.PaymentRepository
import com.greildev.core.domain.model.PaymentList
import com.greildev.core.domain.repository.UserRepository
import com.greildev.core.utils.CoreConstant.EMPTY_CODE
import com.greildev.core.utils.CoreConstant.ERROR_CODE
import com.greildev.core.utils.DataMapper.mapToModel
import com.greildev.core.utils.DispatcherProvider
import com.greildev.core.utils.UIState
import com.greildev.core.utils.suspendSubscribe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PaymentInteractor(
    private val paymentRepository: PaymentRepository,
    private val userRepository: UserRepository,
    private val dispatcher: DispatcherProvider
) {
    suspend fun getPaymentList(): Flow<List<PaymentList>> = flow {
        paymentRepository.getPaymentList().collect { response ->
            emit(response.paymentList.map {
                it.mapToModel()
            })
        }
    }

    suspend fun getPaymentListUpdate(): Flow<List<PaymentList>> = flow {
        paymentRepository.getPaymentListUpdate().collect { response ->
            emit(response.paymentList.map {
                it.mapToModel()
            })
        }
    }

    suspend fun getTokenUser(): Flow<Int> = withContext(dispatcher.io) {
        val userId = userRepository.currentUser()?.uid.orEmpty()
        paymentRepository.getTokenUser(userId)
    }

    fun getAllTransactionHistory(): Flow<UIState<List<TransactionDetail>>> =
        flow {
            emit(UIState.Loading())
            val userId = userRepository.currentUser()?.uid.orEmpty()
            paymentRepository.getAllTransactionHistory(userId).collect { state ->
                state.suspendSubscribe(
                    onSuccess = {
                        if (!it.data.isNullOrEmpty()) {
                            emit(UIState.Success(it.data))
                        } else {
                            emit(
                                UIState.Error(
                                    code = EMPTY_CODE,
                                    errorMessage = "Empty Transaction"
                                )
                            )
                        }
                    },
                    onError = {
                        emit(UIState.Error(code = ERROR_CODE, errorMessage = it.message))
                    }
                )
            }
        }

    suspend fun writeTransactionHistory(transactionDetail: TransactionDetail) =
        withContext(dispatcher.io) {
            val userId = userRepository.currentUser()?.uid.orEmpty()
            paymentRepository.writeTransactionHistory(userId, transactionDetail)
        }

    suspend fun writeTokenTransaction(transactionToken: TransactionToken) =
        withContext(dispatcher.io) {
            val userId = userRepository.currentUser()?.uid.orEmpty()
            paymentRepository.writeTokenTransaction(userId, transactionToken)
        }

    suspend fun updateTokenUser(token: Int) = withContext(dispatcher.io) {
        val userId = userRepository.currentUser()?.uid.orEmpty()
        paymentRepository.updateTokenUser(userId, token)
    }

}
