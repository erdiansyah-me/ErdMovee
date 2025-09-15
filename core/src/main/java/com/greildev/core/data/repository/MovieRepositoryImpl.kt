package com.greildev.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.greildev.core.data.source.local.database.ErdmoveeDatabase
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.core.data.source.local.entities.CheckoutMovieListEntities
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
import com.greildev.core.utils.DispatcherProvider
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("TooManyFunctions")
internal class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val database: ErdmoveeDatabase,
    private val dispatcher: DispatcherProvider
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

    override fun searchMovies(query: String): Flow<PagingData<ResultsItem>> {
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

    override fun getRecommendationMovies(movieId: Int): Flow<PagingData<ResultsItem>> {
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
        return database.favoriteMovieDao.getFavoriteMovieByUid(uid)
    }

    override suspend fun saveFavoriteMovie(favoriteMovieListEntities: FavoriteMovieListEntities) {
        database.favoriteMovieDao.insertFavoriteMovie(favoriteMovieListEntities)
    }

    override suspend fun deleteFavoriteMovie(favoriteId: Int) {
        database.favoriteMovieDao.deleteNonFavoriteMovie(favoriteId)
    }

    override suspend fun deleteFavoriteMovieByIdAndUid(uid: String, id: Int) {
        database.favoriteMovieDao.deleteNonFavoriteMovieByIdAndUid(uid, id)
    }

    override suspend fun checkFavoriteMovie(id: Int, uid: String): Int {
        return database.favoriteMovieDao.checkFavoriteMovieByIdAndUid(id, uid)
    }

    //Cart
    override fun getCartMoviesByUid(uid: String): Flow<List<CartMovieListEntities>> {
        return database.cartMovieDao.getCartMovieByUid(uid)
    }

    override suspend fun saveCartMovie(cartMovieListEntities: CartMovieListEntities) {
        database.cartMovieDao.insertCart(cartMovieListEntities)
    }

    override suspend fun deleteCartMovie(cartId: Int) {
        database.cartMovieDao.deleteNonCart(cartId)
    }

    override suspend fun checkCartMovieByUidAndId(uid: String, id: Int): Int {
        return database.cartMovieDao.checkCartByUidAndId(uid, id)
    }

    override suspend fun isCheckedByCartId(cartId: Int, newIsChecked: Boolean) {
        database.cartMovieDao.isCheckedByCartId(cartId, newIsChecked)
    }

    override suspend fun deleteCheckedByUid(isChecked: Boolean, uid: String) {
        database.cartMovieDao.deleteCheckedByUid(isChecked, uid)
    }

    override fun getCheckedCartByUid(
        isChecked: Boolean,
        uid: String
    ): Flow<List<CartMovieListEntities>> {
        return database.cartMovieDao.getCheckedCartByUid(isChecked, uid)
    }

    override suspend fun replaceAllCart(cart: List<CartMovieListEntities>) {
        with(database){
            withTransaction {
                cartMovieDao.deleteAllCart()
                cartMovieDao.insertAllCart(cart)
            }
        }
    }

    override suspend fun deleteCartById(itemId: Int)
    = withContext(dispatcher.io) {
        database.cartMovieDao.deleteCartByItemId(itemId)
        println()
    }

    override suspend fun getAllCheckoutItems(): Flow<List<CheckoutMovieListEntities>>
    = withContext(dispatcher.io) {
        database.checkoutMovieDao.getAllItems()
    }

    override suspend fun deleteCheckoutItemsById(ids: List<Int>)
    = withContext(dispatcher.io){
        database.checkoutMovieDao.deleteById(ids)
    }

    override suspend fun insertListCheckoutItems(checkoutItems: List<CheckoutMovieListEntities>)
    = withContext(dispatcher.io){
        database.checkoutMovieDao.insertListCheckoutItem(checkoutItems)
    }
}
