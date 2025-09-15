package com.greildev.core.data.source.remote.service

import com.google.firebase.database.FirebaseDatabase
import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class TokenTransactionService @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    private val getTokenTransactionRef = firebaseDatabase.reference.child("token_transaction")
    private val getTransactionHistoryRef = firebaseDatabase.reference.child("transaction_history")
    private val getTokenUserRef = firebaseDatabase.reference.child("token_user")

    suspend fun writeTokenTransaction(
        userId: String,
        transactionToken: TransactionToken
    ): Flow<Boolean> = callbackFlow {
        val transactionId = transactionToken.transactionId
        getTokenTransactionRef.child(userId).child(transactionId).push().setValue(transactionToken)
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    suspend fun updateTokenUser(
        userId: String,
        token: Int,
    ): Flow<Boolean> = callbackFlow {
        //update
        val childUpdates = hashMapOf<String, Any>(
            "/$userId/token" to token
        )
        getTokenUserRef.updateChildren(childUpdates)
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    fun writeTransactionHistory(
        userId: String,
        transactionDetail: TransactionDetail
    ): Flow<Boolean> = callbackFlow {
        val transactionId = transactionDetail.transactionId
        getTransactionHistoryRef.child(userId).child(transactionId)
            .setValue(transactionDetail)
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    fun getAllTransactionHistory(userId: String): Flow<SourceResult<List<TransactionDetail>>> =
        callbackFlow<SourceResult<List<TransactionDetail>>> {
            val snapshot = getTransactionHistoryRef.child(userId).get()
            snapshot.addOnCompleteListener {
                if (snapshot.isSuccessful) {
                    val list = snapshot.result.children.mapNotNull {
                        println("HAIYAAA: it ${it}")
                        val items = it.getValue(TransactionDetail::class.java)
                        println("HAIYAAA: items ${items}")
                        return@mapNotNull items
                    }
                    trySend(SourceResult.Success(list))
                } else {
                    trySend(
                        SourceResult.Error(
                            555,
                            snapshot.exception?.message ?: "Something Went Wrong!"
                        )
                    )
                }
            }

//                .addOnSuccessListener {
//                    val transactionList = it.children.mapNotNull { snapshot ->
//                        snapshot.getValue(TransactionDetail::class.java)
//                    }
//                    trySend(SourceResult.Success(transactionList))
//                }
//                .addOnFailureListener {
//                    trySend(SourceResult.Error(555, it.message ?: "Something Went Wrong!"))
//                }
            awaitClose()
        }.flowOn(Dispatchers.IO)

    suspend fun getTokenUser(
        userId: String,
    ): Flow<Int> = callbackFlow {
        getTokenUserRef.child(userId).child("token").get()
            .addOnSuccessListener {
                it.getValue(Int::class.java)?.let { it1 -> trySend(it1) }
            }
            .addOnFailureListener {
                trySend(0)
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)
}