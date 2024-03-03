package com.greildev.core.utils

import com.google.firebase.auth.FirebaseUser
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.core.data.source.local.entities.GenresName
import com.greildev.core.data.source.local.entities.NowPlayingMovieListEntities
import com.greildev.core.data.source.remote.response.CastItem
import com.greildev.core.data.source.remote.response.GenresItem
import com.greildev.core.data.source.remote.response.MovieDetailResponse
import com.greildev.core.data.source.remote.response.PaymentResponseItem
import com.greildev.core.data.source.remote.response.PaymentResponseItemInfo
import com.greildev.core.data.source.remote.response.ResultsItem
import com.greildev.core.domain.model.CastsData
import com.greildev.core.domain.model.GenresData
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.domain.model.MovieListData
import com.greildev.core.domain.model.PaymentList
import com.greildev.core.domain.model.PaymentListInfo
import com.greildev.core.domain.model.UserData

object DataMapper {
    fun FirebaseUser.toUIData(): UserData {
        return UserData(
            username = this.displayName,
            email = this.email,
            photoUri = this.photoUrl,
            uid = this.uid
        )
    }

    fun ResultsItem.toNowPlayingEntity(): NowPlayingMovieListEntities {
        return NowPlayingMovieListEntities(
            movieId = this.id,
            title = this.title ?: "",
            overview = this.overview ?: "",
            posterPath = this.posterPath ?: "",
            backdropPath = this.backdropPath ?: "",
            releaseDate = this.releaseDate ?: "",
            voteAverage = this.voteAverage ?: 0.0,
            voteCount = this.voteCount ?: 0,
            popularity = this.popularity ?: 0.0,
            originalLanguage = this.originalLanguage ?: "",
            originalTitle = this.originalTitle ?: "",
            adult = this.adult ?: false,
            video = this.video ?: false,
        )
    }

    fun NowPlayingMovieListEntities.mapToModel(): MovieListData {
        return MovieListData(
            id = this.movieId,
            title = this.title,
            posterPath = this.posterPath,
            releaseDate = this.releaseDate,
            voteAverage = this.voteAverage,
            price = if (this.voteAverage.toInt() == 0) 1000 else if (this.voteAverage.toInt() == 0) 1000 else (this.voteAverage * 100).toInt()
        )
    }

    fun ResultsItem?.mapToModel(): MovieListData {
        return MovieListData(
            id = this?.id ?: 0,
            title = this?.title ?: "",
            posterPath = this?.posterPath ?: "",
            releaseDate = this?.releaseDate ?: "",
            voteAverage = this?.voteAverage ?: 0.0,
            price = if (this?.voteAverage?.toInt() == 0) 1000 else (this?.voteAverage?.times(100))?.toInt()
                ?: 1000
        )
    }

    fun PaymentResponseItem.mapToModel() = PaymentList(
        item = this.item.map { it.mapToModel() },
        title = this.title
    )

    fun PaymentResponseItemInfo.mapToModel() = PaymentListInfo(
        image = this.image,
        label = this.label,
        status = this.status
    )

    fun MovieDetailResponse.mapToModel(): MovieDetailData {
        return MovieDetailData(
            id = this.id ?: 0,
            originalLanguage = this.originalLanguage ?: "",
            title = this.title ?: "",
            backdropPath = this.backdropPath ?: "",
            revenue = this.revenue ?: 0L,
            credits = this.credits?.cast?.map { it.mapToModel() },
            genres = this.genres?.map { it.mapToModel() },
            overview = this.overview ?: "",
            originalTitle = this.originalTitle ?: "",
            runtime = this.runtime ?: 0,
            posterPath = this.posterPath ?: "",
            releaseDate = this.releaseDate ?: "",
            voteAverage = this.voteAverage ?: 0.0,
            tagline = this.tagline ?: "",
            popularity = this.popularity ?: 0.0,
            status = this.status ?: "",
            video = this.video ?: false,
            adult = adult ?: false,
            voteCount = this.voteCount ?: 0,
            price = if (this.voteAverage?.equals(0.0) == true) 1000 else (this.voteAverage?.times(
                100
            ))?.toInt() ?: 1000
        )
    }

    fun GenresItem.mapToModel(): GenresData {
        return GenresData(
            id = this.id,
            name = this.name
        )
    }

    fun CastItem.mapToModel(): CastsData {
        return CastsData(
            castId = this.castId ?: 0,
            character = this.character ?: "",
            gender = this.gender ?: 0,
            creditId = this.creditId ?: "",
            knownForDepartment = this.knownForDepartment ?: "",
            originalName = this.originalName ?: "",
            popularity = this.popularity ?: 0.0,
            name = this.name ?: "",
            profilePath = this.profilePath ?: "",
            id = this.id ?: 0,
            adult = this.adult ?: false,
            order = this.order ?: 0
        )
    }

    fun MovieDetailData.mapToFavoriteEntities(uid: String): FavoriteMovieListEntities {
        return FavoriteMovieListEntities(
            id = this.id,
            title = this.title,
            overview = this.overview,
            posterPath = this.posterPath,
            backdropPath = this.backdropPath,
            releaseDate = this.releaseDate,
            voteAverage = this.voteAverage,
            popularity = this.popularity,
            originalLanguage = this.originalLanguage,
            originalTitle = this.originalTitle,
            voteCount = this.voteCount,
            adult = this.adult,
            video = this.video,
            genresName = this.genres?.map {
                it.name
            }.genresNameMapper(),
            price = this.price,
            uid = uid
        )
    }

    fun MovieDetailData.mapToCartEntities(uid: String): CartMovieListEntities {
        val basePrice = this.price
        val quantityPrice = basePrice * 1
        return CartMovieListEntities(
            id = this.id,
            title = this.title,
            overview = this.overview,
            posterPath = this.posterPath,
            backdropPath = this.backdropPath,
            releaseDate = this.releaseDate,
            voteAverage = this.voteAverage,
            popularity = this.popularity,
            originalLanguage = this.originalLanguage,
            originalTitle = this.originalTitle,
            voteCount = this.voteCount,
            adult = this.adult,
            video = this.video,
            genresName = this.genres?.map {
                it.name
            }.genresNameMapper(),
            basePrice = basePrice,
            uid = uid,
            isChecked = true,
            quantityItem = 1,
            quantityPrice = quantityPrice
        )
    }
    private fun List<String>?.genresNameMapper(): GenresName =
        GenresName(
            genreIds = this ?: emptyList()
        )

}