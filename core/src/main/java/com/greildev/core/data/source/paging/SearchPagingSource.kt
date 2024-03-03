package com.greildev.core.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.greildev.core.data.source.remote.RemoteDataSource
import com.greildev.core.data.source.remote.response.ResultsItem

class SearchPagingSource(
    private val remoteDataSource: RemoteDataSource,
    private val query: String,
) : PagingSource<Int, ResultsItem>() {

    override fun getRefreshKey(state: PagingState<Int, ResultsItem>): Int? {
        return state.anchorPosition?.let {
            val anchorPosition = state.closestPageToPosition(it)
            anchorPosition?.prevKey?.plus(1) ?: anchorPosition?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultsItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = remoteDataSource.searchMovies(page = page, query = query)

            LoadResult.Page(
                data = responseData.results ?: emptyList(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if ((responseData.totalPages ?: 0) <= page) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}