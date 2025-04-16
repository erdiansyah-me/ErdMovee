package com.greildev.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.greildev.core.data.source.local.LocalDataSource
import com.greildev.core.data.source.local.database.ErdmoveeDatabase
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.core.data.source.local.entities.NowPlayingMovieListEntities
import com.greildev.core.data.source.paging.NowPlayingMovieRemoteMediator
import com.greildev.core.data.source.paging.PopularPagingSource
import com.greildev.core.data.source.paging.RecommendationPagingSource
import com.greildev.core.data.source.paging.SearchPagingSource
import com.greildev.core.data.source.remote.RemoteDataSource
import com.greildev.core.data.source.remote.response.MovieDetailResponse
import com.greildev.core.data.source.remote.response.ResultsItem
import com.greildev.core.domain.repository.MovieRepository
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val database: ErdmoveeDatabase
) : MovieRepository {

    //Remote Movies
    override suspend fun getPopularMovies(): Flow<PagingData<ResultsItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PopularPagingSource(remoteDataSource) }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getNowPlayingMovies(): Flow<PagingData<NowPlayingMovieListEntities>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = NowPlayingMovieRemoteMediator(
                database = database,
                remoteDataSource = remoteDataSource
            ),
            pagingSourceFactory = { database.nowPlayingMovieDao.getAllNowPlayingMovie() }
        ).flow
    }

    override suspend fun searchMovies(query: String): Flow<PagingData<ResultsItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(
                    remoteDataSource = remoteDataSource,
                    query = query
                )
            }
        ).flow
    }

    override suspend fun getMovieDetail(id: Int): Flow<SourceResult<MovieDetailResponse>> {
        return flow {
            emit(remoteDataSource.getMovieDetail(id))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getRecommendationMovies(movieId: Int): Flow<PagingData<ResultsItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                RecommendationPagingSource(
                    remoteDataSource = remoteDataSource,
                    movieId = movieId
                )
            }
        ).flow
    }

    //Local Movies
    //Favorite
    override fun getFavoriteMoviesByUid(uid: String): Flow<List<FavoriteMovieListEntities>> {
        return localDataSource.getFavoriteMoviesByUid(uid)
    }

    override suspend fun saveFavoriteMovie(favoriteMovieListEntities: FavoriteMovieListEntities) {
        localDataSource.saveFavoriteMovie(favoriteMovieListEntities)
    }

    override suspend fun deleteFavoriteMovie(favoriteId: Int) {
        localDataSource.deleteFavoriteMovie(favoriteId)
    }

    override suspend fun deleteFavoriteMovieByIdAndUid(uid: String, id: Int) {
        localDataSource.deleteFavoriteMovieByIdAndUid(uid, id)
    }

    override suspend fun checkFavoriteMovie(id: Int, uid: String): Int {
        return localDataSource.checkFavoriteMovie(id, uid)
    }

    //Cart
    override fun getCartMoviesByUid(uid: String): Flow<List<CartMovieListEntities>> {
        return localDataSource.getCartMoviesByUid(uid)
    }

    override suspend fun saveCartMovie(cartMovieListEntities: CartMovieListEntities) {
        localDataSource.saveCartMovie(cartMovieListEntities)
    }

    override suspend fun deleteCartMovie(cartId: Int) {
        localDataSource.deleteCartMovie(cartId)
    }

    override suspend fun checkCartMovieByUidAndId(uid: String, id: Int): Int {
        return localDataSource.checkCartMovieByUidAndId(uid, id)
    }

    override suspend fun isCheckedByCartId(cartId: Int, newIsChecked: Boolean) {
        localDataSource.isCheckedByCartId(cartId, newIsChecked)
    }

    override suspend fun deleteCheckedByUid(isChecked: Boolean, uid: String) {
        localDataSource.deleteCheckedByUid(isChecked, uid)
    }

    override fun getCheckedCartByUid(
        isChecked: Boolean,
        uid: String
    ): Flow<List<CartMovieListEntities>> {
        return localDataSource.getCheckedCartByUid(isChecked, uid)
    }

    override suspend fun replaceAllCart(cart: List<CartMovieListEntities>) {
        localDataSource.replaceAllCart(cart)
    }
}
