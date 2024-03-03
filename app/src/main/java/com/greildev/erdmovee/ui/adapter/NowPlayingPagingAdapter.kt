package com.greildev.erdmovee.ui.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.greildev.core.base.BasePagingAdapter
import com.greildev.core.domain.model.MovieListData
import com.greildev.erdmovee.databinding.CardMovieNowplayingBinding
import com.greildev.erdmovee.utils.formatDecimal
import com.greildev.erdmovee.utils.imgUrlFormatter

class NowPlayingPagingAdapter(private val action: (Int, String) -> Unit) :
    BasePagingAdapter<MovieListData, CardMovieNowplayingBinding>(CardMovieNowplayingBinding::inflate) {
    override fun onItemBind(): (MovieListData, CardMovieNowplayingBinding, View, Int) -> Unit {
        return { item, binding, view, _ ->
            Glide.with(view.context)
                .load(item.posterPath.imgUrlFormatter())
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.ivMoviePoster)
            binding.tvMovieTitle.text = item.title
            binding.tvRating.text = item.voteAverage.formatDecimal()
            binding.chipPrice.text = item.price.toString()
            view.setOnClickListener {
                action.invoke(item.id, item.title)
            }
        }
    }
}