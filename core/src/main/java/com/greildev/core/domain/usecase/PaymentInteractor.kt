package com.greildev.core.domain.usecase

import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.data.repository.PaymentRepository
import com.greildev.core.domain.model.PaymentList
import com.greildev.core.utils.CoreConstant.EMPTY_CODE
import com.greildev.core.utils.CoreConstant.ERROR_CODE
import com.greildev.core.utils.DataMapper.mapToModel
import com.greildev.core.utils.UIState
import com.greildev.core.utils.suspendSubscribe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PaymentInteractor(
    private val paymentRepository: PaymentRepository
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

    suspend fun getTokenUser(userId: String): Flow<Int> = paymentRepository.getTokenUser(userId)
    suspend fun getAllTransactionHistory(userId: String): Flow<UIState<List<TransactionDetail>>> =
        flow {
            emit(UIState.Loading())
            paymentRepository.getAllTransactionHistory(userId).collect { state ->
                state.suspendSubscribe(
                    onSuccess = {
                        if (it.data != null) {
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

    suspend fun writeTransactionHistory(userId: String, transactionDetail: TransactionDetail) =
        paymentRepository.writeTransactionHistory(userId, transactionDetail)

    suspend fun writeTokenTransaction(userId: String, transactionToken: TransactionToken) =
        paymentRepository.writeTokenTransaction(userId, transactionToken)

    suspend fun updateTokenUser(userId: String, token: Int) =
        paymentRepository.updateTokenUser(userId, token)

}