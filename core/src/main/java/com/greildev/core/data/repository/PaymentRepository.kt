package com.greildev.core.data.repository

import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.data.source.remote.RemoteDataSource
import com.greildev.core.data.source.remote.response.PaymentResponse
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PaymentRepository {
    suspend fun getPaymentList(): Flow<PaymentResponse>
    suspend fun getPaymentListUpdate(): Flow<PaymentResponse>

    suspend fun getTokenUser(userId: String): Flow<Int>
    suspend fun getAllTransactionHistory(userId: String): Flow<SourceResult<List<TransactionDetail>>>
    suspend fun writeTransactionHistory(
        userId: String,
        transactionDetail: TransactionDetail
    ): Flow<Boolean>

    suspend fun writeTokenTransaction(
        userId: String,
        transactionToken: TransactionToken
    ): Flow<Boolean>

    suspend fun updateTokenUser(userId: String, token: Int): Flow<Boolean>
}


class PaymentRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : PaymentRepository {

    override suspend fun getPaymentList(): Flow<PaymentResponse> = remoteDataSource.getPaymentList()
    override suspend fun getPaymentListUpdate(): Flow<PaymentResponse> =
        remoteDataSource.getPaymentListUpdate()

    override suspend fun getTokenUser(userId: String): Flow<Int> {
        return remoteDataSource.getTokenUser(userId)
    }

    override suspend fun getAllTransactionHistory(userId: String): Flow<SourceResult<List<TransactionDetail>>> {
        return remoteDataSource.getAllTransactionHistory(userId)
    }

    override suspend fun writeTransactionHistory(
        userId: String,
        transactionDetail: TransactionDetail
    ): Flow<Boolean> {
        return remoteDataSource.writeTransactionHistory(userId, transactionDetail)
    }

    override suspend fun writeTokenTransaction(
        userId: String,
        transactionToken: TransactionToken
    ): Flow<Boolean> {
        return remoteDataSource.writeTokenTransaction(userId, transactionToken)
    }

    override suspend fun updateTokenUser(userId: String, token: Int): Flow<Boolean> {
        return remoteDataSource.updateTokenUser(userId, token)
    }
}