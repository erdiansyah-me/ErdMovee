package com.greildev.erdmovee.ui.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.greildev.core.base.BasePagingAdapter
import com.greildev.core.domain.model.MovieListData
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.CardMovieRecommendationItemBinding
import com.greildev.erdmovee.utils.formatDecimal
import com.greildev.erdmovee.utils.imgUrlFormatter

class RecommendationMovieAdapter(private val action: (Int, String) -> Unit) :
    BasePagingAdapter<MovieListData, CardMovieRecommendationItemBinding>(
        CardMovieRecommendationItemBinding::inflate
    ) {
    override fun onItemBind(): (MovieListData, CardMovieRecommendationItemBinding, View, Int) -> Unit {
        return { item, binding, view, _ ->
            binding.tvRating.text = item.voteAverage.formatDecimal()
            binding.tvMovieTitle.text = item.title
            Glide.with(view.context)
                .load(item.posterPath.imgUrlFormatter())
                .error(R.drawable.ic_image_error_24)
                .into(binding.ivMoviePoster)
            view.setOnClickListener {
                action.invoke(item.id, item.title)
            }
        }
    }

}