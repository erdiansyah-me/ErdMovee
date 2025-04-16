package com.greildev.core.domain.repository

import androidx.paging.PagingData
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.core.data.source.local.entities.NowPlayingMovieListEntities
import com.greildev.core.data.source.remote.response.MovieDetailResponse
import com.greildev.core.data.source.remote.response.ResultsItem
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    //Remote Movies
    suspend fun getPopularMovies(): Flow<PagingData<ResultsItem>>
    suspend fun getNowPlayingMovies(): Flow<PagingData<NowPlayingMovieListEntities>>
    suspend fun searchMovies(query: String): Flow<PagingData<ResultsItem>>
    suspend fun getMovieDetail(id: Int): Flow<SourceResult<MovieDetailResponse>>
    suspend fun getRecommendationMovies(movieId: Int): Flow<PagingData<ResultsItem>>

    //Favorite
    fun getFavoriteMoviesByUid(uid: String): Flow<List<FavoriteMovieListEntities>>
    suspend fun saveFavoriteMovie(favoriteMovieListEntities: FavoriteMovieListEntities)
    suspend fun deleteFavoriteMovie(favoriteId: Int)
    suspend fun deleteFavoriteMovieByIdAndUid(uid: String, id: Int)
    suspend fun checkFavoriteMovie(id: Int, uid: String): Int

    //Cart
    fun getCartMoviesByUid(uid: String): Flow<List<CartMovieListEntities>>
    suspend fun saveCartMovie(cartMovieListEntities: CartMovieListEntities)
    suspend fun deleteCartMovie(cartId: Int)
    suspend fun checkCartMovieByUidAndId(uid: String, id: Int): Int
    suspend fun isCheckedByCartId(cartId: Int, newIsChecked: Boolean)
    suspend fun deleteCheckedByUid(isChecked: Boolean, uid: String)
    fun getCheckedCartByUid(isChecked: Boolean, uid: String): Flow<List<CartMovieListEntities>>
    suspend fun replaceAllCart(cart: List<CartMovieListEntities>)
}
