package com.greildev.core.domain.model

import android.os.Parcelable
import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentStatusModel(
    val isSuccess: Boolean,
    val transactionDetail: TransactionDetail? = null,
    val transactionToken: TransactionToken? = null
) : Parcelable