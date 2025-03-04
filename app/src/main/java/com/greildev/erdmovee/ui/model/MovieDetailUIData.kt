package com.greildev.erdmovee.ui.model

import androidx.paging.PagingData
import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.domain.model.MovieListData
import com.greildev.core.utils.UIState

data class MovieDetailUIData(
    val movieDetailUI : UIState<MovieDetailData> = UIState.NoState(),
    val movieRecomPaging : PagingData<MovieListData> = PagingData.empty(),
    val isMovieInCart : Boolean = false,
    val isMovieInFav : Boolean = false
)
