package com.greildev.core.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionToken(
    val transactionId: String,
    val transactionDate: String,
    val amountToken: Int,
    val transactionType: String,
    val transactionMethod: String,
) : Parcelable