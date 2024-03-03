package com.greildev.core.domain.model

data class MovieListData(
    val id: Int,
    val title: String,
    val posterPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val price: Int,
)