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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.lastOrNull
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

                        val tokenUsed = checkoutItems.sumOf { it.quantityPrice }
                        val newToken = tokenUser - tokenUsed

                        // Launch side-effects concurrently in coroutineScope
                        coroutineScope {
                            val updateToken = async {
                                paymentRepository.updateTokenUser(userId, newToken)
                            }

                            val writeHistory = async {
                                paymentRepository.writeTransactionHistory(
                                    userId = userId,
                                    transactionDetail = TransactionDetail(
                                        transactionId = transactionId,
                                        itemList = checkoutItems,
                                        transactionDate = getCurrentDateTime(),
                                        amountToken = tokenUsed
                                    )
                                )
                            }

                            val deleteCheckout = async {
                                movieRepository.deleteCheckoutItemsById(checkoutItems.map { it.id })
                            }

                            val deleteCart = async {
                                checkoutItems.forEach {
                                    movieRepository.deleteCartById(it.id)
                                }
                            }

                            updateToken.await()
                            deleteCheckout.await()
                            deleteCart.await()

                            writeHistory.await().lastOrNull() ?: false
                        }
                    }.first() // collect one combined emission and return its result
                    emit(UIState.Success(result))
                } else {
                    emit(UIState.Error(404, "User Not Found"))
                }
            } catch (e: Exception) {
                Log.e(DoCheckoutMovieUseCase::class.simpleName, e.message.orNullToString())
                emit(UIState.Error(500, "Error"))
            }
        }.flowOn(dispatcherProvider.default)
}