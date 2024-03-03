package com.greildev.core.data.source.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.greildev.core.data.source.local.database.ErdmoveeDatabase
import com.greildev.core.data.source.local.entities.NowPlayingMovieListEntities
import com.greildev.core.data.source.local.entities.NowPlayingRemoteKeys
import com.greildev.core.data.source.remote.RemoteDataSource
import com.greildev.core.utils.DataMapper.toNowPlayingEntity

@OptIn(ExperimentalPagingApi::class)
class NowPlayingMovieRemoteMediator(
    private val database: ErdmoveeDatabase,
    private val remoteDataSource: RemoteDataSource,
) : RemoteMediator<Int, NowPlayingMovieListEntities>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NowPlayingMovieListEntities>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
        }
        try {
            val response = remoteDataSource.getNowPlayingMovies(page)
            val endOfPaginationReached = response.results.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.nowPlayingMovieRemoteKeysDao().deleteRemoteKeys()
                    database.nowPlayingMovieDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.results.map {
                    NowPlayingRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.nowPlayingMovieRemoteKeysDao().insertAll(keys)
                database.nowPlayingMovieDao()
                    .insertAll(response.results.map { it.toNowPlayingEntity() })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, NowPlayingMovieListEntities>): NowPlayingRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.nowPlayingMovieRemoteKeysDao().getRemoteKeysId(data.movieId)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, NowPlayingMovieListEntities>): NowPlayingRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.nowPlayingMovieRemoteKeysDao().getRemoteKeysId(data.movieId)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, NowPlayingMovieListEntities>): NowPlayingRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.nowPlayingMovieRemoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}