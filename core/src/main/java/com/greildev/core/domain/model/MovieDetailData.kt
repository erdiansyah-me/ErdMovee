package com.greildev.core.domain.model

data class MovieDetailData(
    val id: Int,
    val originalLanguage: String,
    val title: String,
    val backdropPath: String,
    val revenue: Long,
    val credits: List<CastsData>?,
    val genres: List<GenresData>?,
    val overview: String,
    val originalTitle: String,
    val runtime: Int,
    val posterPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val tagline: String,
    val popularity: Double,
    val status: String,
    val video: Boolean,
    val voteCount: Int,
    val adult: Boolean,
    val price: Int,
)

data class CastsData(
    val castId: Int,
    val character: String,
    val gender: Int,
    val creditId: String,
    val knownForDepartment: String,
    val originalName: String,
    val popularity: Double,
    val name: String,
    val profilePath: String,
    val id: Int,
    val adult: Boolean,
    val order: Int
)
