package com.greildev.core.domain.usecase

import android.util.Log
import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.repository.PaymentRepository
import com.greildev.core.domain.repository.MovieRepository
import com.greildev.core.domain.repository.UserRepository
import com.greildev.core.utils.DispatcherProvider
import com.greildev.core.utils.UIState
import com.greildev.core.utils.getCurrentDateTime
import com.greildev.core.utils.orNullToString
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DoCheckoutMovieUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository,
    private val paymentRepository: PaymentRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    //    suspend operator fun invoke(transactionId: String) : Boolean = withContext(dispatcherProvider.default) {
//        try {
//            val checkoutItems = async { movieRepository.getAllCheckoutItems().last() }
//            val user = userRepository.currentUser()
//            val tokenUser = async { paymentRepository.getTokenUser(user?.uid ?: "").last() }
//            val tokenUsed = async {checkoutItems.await().sumOf {
//                    it.quantityPrice
//                }
//            }
//            val resultToken = tokenUser.await() - tokenUsed.await()
//            paymentRepository.updateTokenUser(user?.uid?:"", resultToken)
//            val transactionHistory = paymentRepository.writeTransactionHistory(
//                userId = user?.uid ?:"",
//                transactionDetail = TransactionDetail(
//                    transactionId = transactionId,
//                    itemList = checkoutItems.await(),
//                    transactionDate = getCurrentDateTime(),
//                    amountToken = tokenUsed.await()
//                )
//            )
//
//            movieRepository.deleteCheckoutItemsById(checkoutItems.await().map { it.id })
//            checkoutItems.await().forEach {
//                movieRepository.deleteCartById(it.id)
//            }
//            return@withContext transactionHistory.last()
//        } catch (e: Exception) {
//            Log.e(DoCheckoutMovieUseCase::class.simpleName, e.message.orNullToString())
//            return@withContext false
//        }
//    }
    operator fun invoke(transactionId: String): Flow<UIState<Boolean>> =
        flow {
            emit(UIState.Loading())
            try {
                val user = userRepository.currentUser()

                if (user != null) {
                    val userId = user.uid.orNullToString()
                    // Combine both flows and collect the latest values once
                    val result = combine(
                        movieRepository.getAllCheckoutItems(),
                        paymentRepository.getTokenUser(userId)
                    ) { checkoutItems, tokenUser ->
                        if (checkoutItems.isEmpty()) return@combine false
                        coroutineScope {
                            val tokenUsed = checkoutItems.sumOf { it.quantityPrice }
                            if (tokenUser < tokenUsed) {
                                throw IllegalStateException("Insufficient balance. You have $tokenUser, but the total cost is $tokenUsed.")
                            }
                            val newToken = tokenUser - tokenUsed
                            val historyWritten = paymentRepository.writeTransactionHistory(
                                userId = userId,
                                transactionDetail = TransactionDetail(
                                    transactionId = transactionId,
                                    itemList = checkoutItems,
                                    transactionDate = getCurrentDateTime(),
                                    amountToken = tokenUsed
                                )
                            ).first()

                            if (!historyWritten) {
                                throw Exception("Failed to write transaction history.")
                            }

                            val tokenUpdated = paymentRepository.updateTokenUser(userId, newToken).first()
                            if (!tokenUpdated) {
                                throw Exception("Failed to update token user.")
                            }
                            movieRepository.deleteCheckoutItemsById(checkoutItems.map { it.id })
                            checkoutItems.forEach {
                                movieRepository.deleteCartById(it.id)
                            }

                            true
                        }
                    }.first()
                    emit(UIState.Success(result))
                } else {
                    emit(UIState.Error(404, "User Not Found"))
                }
            } catch (e: Exception) {
                Log.e(DoCheckoutMovieUseCase::class.simpleName, e.message.orNullToString())
                emit(UIState.Error(500, e.message.orNullToString()))
            }
        }.flowOn(dispatcherProvider.default)
}
