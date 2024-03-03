package com.greildev.core.data.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class TransactionDetail(
    val cartMovieListEntities: List<CartMovieListEntities>? = null,
    val transactionDate: String? = null,
    val transactionId: String? = null,
    val amountToken: Int? = null,
) : Parcelable