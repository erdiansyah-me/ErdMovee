package com.greildev.core.data.source.remote

import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.data.source.remote.service.MovieService
import com.greildev.core.data.source.remote.service.RemoteConfigService
import com.greildev.core.data.source.remote.service.TokenTransactionService
import com.greildev.core.utils.safeApiCall
import retrofit2.Retrofit
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val retrofit: Retrofit,
    private val remoteConfig: RemoteConfigService,
    private val transactionService: TokenTransactionService
) {
    private val movieService: MovieService = retrofit.create(MovieService::class.java)

    //Movie
    suspend fun getPopularMovies(page: Int) = movieService.getPopularMovies(page)
    suspend fun getNowPlayingMovies(page: Int) = movieService.getNowPlayingMovies(page)
    suspend fun searchMovies(query: String, page: Int) = movieService.searchMovies(query, page)
    suspend fun getMovieDetail(id: Int) = safeApiCall {
        movieService.getMovieDetail(id)
    }

    suspend fun getMovieRecommendations(movieId: Int, page: Int) =
        movieService.getMovieRecommendations(movieId, page)

    //Remote Config
    fun getPaymentList() = remoteConfig.getPaymentList()
    fun getPaymentListUpdate() = remoteConfig.getPaymentListUpdated()

    //payment
    suspend fun writeTokenTransaction(userId: String, transactionToken: TransactionToken) =
        transactionService.writeTokenTransaction(userId, transactionToken)

    suspend fun updateTokenUser(userId: String, token: Int) =
        transactionService.updateTokenUser(userId, token)

    fun writeTransactionHistory(userId: String, transactionDetail: TransactionDetail) =
        transactionService.writeTransactionHistory(userId, transactionDetail)

    suspend fun getTokenUser(userId: String) = transactionService.getTokenUser(userId)
    fun getAllTransactionHistory(userId: String) =
        transactionService.getAllTransactionHistory(userId)
}
