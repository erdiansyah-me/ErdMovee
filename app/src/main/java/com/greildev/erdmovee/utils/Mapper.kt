package com.greildev.erdmovee.utils

import com.greildev.core.data.source.local.entities.CheckoutMovieListEntities
import com.greildev.erdmovee.ui.model.CheckoutMovieUIModel
import com.greildev.erdmovee.ui.model.PaymentStatusItemUIModel

fun CheckoutMovieListEntities.toUIModel() = CheckoutMovieUIModel(
    id = id,
    originalTitle = originalTitle,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    basePrice = basePrice,
    quantityItem = quantityItem,
    quantityPrice = quantityPrice
)

fun CheckoutMovieListEntities.toPaymentStatusUIModel() = PaymentStatusItemUIModel(
    id = id,
    title = title,
    quantityItem = quantityItem,
    quantityPrice = quantityPrice,
    basePrice = basePrice
)