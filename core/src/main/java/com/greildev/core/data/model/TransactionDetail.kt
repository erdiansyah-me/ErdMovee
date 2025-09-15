package com.greildev.core.data.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.greildev.core.data.source.local.entities.CheckoutMovieListEntities
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class TransactionDetail(

    val itemList: List<CheckoutMovieListEntities> = emptyList(),
    val transactionDate: String = "",
    val transactionId: String = "",
    val amountToken: Int = 0,
) : Parcelable