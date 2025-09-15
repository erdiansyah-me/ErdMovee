package com.greildev.erdmovee.ui.model

data class CheckoutMovieUIModel(
    val id: Int,
    val originalTitle: String,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val basePrice: Int,
    var quantityItem: Int,
    var quantityPrice: Int,
)
