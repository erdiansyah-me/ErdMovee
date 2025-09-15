package com.greildev.erdmovee.ui.model

import com.greildev.core.domain.model.MovieDetailData
import com.greildev.core.utils.UIState

data class MovieDetailUIData(
    val movieDetailUI : UIState<MovieDetailData> = UIState.Loading(),
    val isMovieInCart : Boolean = false,
    val isMovieInFav : Boolean = false
)
