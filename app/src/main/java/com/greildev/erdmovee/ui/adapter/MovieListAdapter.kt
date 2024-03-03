package com.greildev.erdmovee.ui.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.greildev.core.base.BasePagingAdapter
import com.greildev.core.domain.model.MovieListData
import com.greildev.erdmovee.databinding.CardMovieListItemBinding
import com.greildev.erdmovee.utils.formatDecimal
import com.greildev.erdmovee.utils.imgUrlFormatter

class MovieListAdapter(private val action: (Int, String) -> Unit) :
    BasePagingAdapter<MovieListData, CardMovieListItemBinding>(
        CardMovieListItemBinding::inflate
    ) {
    override fun onItemBind(): (MovieListData, CardMovieListItemBinding, View, Int) -> Unit {
        return { item, binding, view, _ ->
            binding.tvTitle.text = item.title
            binding.tvDate.text = item.releaseDate
            binding.tvRating.text = item.voteAverage.formatDecimal()
            Glide.with(view.context)
                .load(item.posterPath.imgUrlFormatter())
                .into(binding.ivPoster)
            binding.chipPrice.text = item.price.toString()
            view.setOnClickListener {
                action.invoke(item.id, item.title)
            }
        }
    }
}