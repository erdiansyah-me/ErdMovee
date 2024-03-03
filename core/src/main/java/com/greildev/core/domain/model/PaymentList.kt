package com.greildev.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentListInfo(
    val image: String,
    val label: String,
    val status: Boolean
) : Parcelable

data class PaymentList(
    val item: List<PaymentListInfo>,
    val title: String
)
