package com.greildev.core.data.source.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greildev.core.utils.CoreConstant
import kotlinx.parcelize.Parcelize

@Entity(tableName = CoreConstant.POPULAR_ENTITIES)
data class PopularMovieListEntities(
    @PrimaryKey
    val id: Int,
    val overview: String,
    val originalLanguage: String,
    val originalTitle: String,
    val video: Boolean,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val adult: Boolean,
    val voteCount: Int
)
@Parcelize
data class GenresName(
    val genreIds: List<String>
) : Parcelable

@Entity(tableName = CoreConstant.NOW_PLAYING_ENTITIES)
data class NowPlayingMovieListEntities(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val movieId: Int,
    val overview: String,
    val originalLanguage: String,
    val originalTitle: String,
    val video: Boolean,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val adult: Boolean,
    val voteCount: Int
)

@Entity(tableName = CoreConstant.FAVORITE_MOVIE_ENTITIES)
data class FavoriteMovieListEntities(
    @PrimaryKey(autoGenerate = true)
    val favoriteId: Int = 0,
    val id: Int,
    val overview: String,
    val originalLanguage: String,
    val originalTitle: String,
    val video: Boolean,
    val title: String,
    val genresName: GenresName,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val adult: Boolean,
    val voteCount: Int,
    val price: Int,
    val uid: String,
)

@Parcelize
@Entity(tableName = CoreConstant.CART_MOVIE_ENTITIES)
data class CartMovieListEntities(
    @PrimaryKey(autoGenerate = true)
    val cartId: Int = 0,
    val uid: String,
    val id: Int,
    val overview: String,
    val originalLanguage: String,
    val originalTitle: String,
    val video: Boolean,
    val title: String,
    val genresName: GenresName,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val adult: Boolean,
    val voteCount: Int,
    val basePrice: Int,
    val quantityItem: Int,
    val quantityPrice: Int,
    val isChecked: Boolean,
) : Parcelable