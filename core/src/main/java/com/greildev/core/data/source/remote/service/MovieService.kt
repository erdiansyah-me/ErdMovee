package com.greildev.core.data.source.remote.service

import com.greildev.core.data.source.remote.response.MovieDetailResponse
import com.greildev.core.data.source.remote.response.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int
    ): MovieListResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query")query: String,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): MovieDetailResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int
    ): MovieListResponse
}