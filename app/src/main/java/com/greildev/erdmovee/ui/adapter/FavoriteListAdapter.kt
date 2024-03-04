package com.greildev.erdmovee.ui.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.greildev.core.base.BaseListAdapter
import com.greildev.core.data.source.local.entities.FavoriteMovieListEntities
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.CardMovieFavoriteItemBinding
import com.greildev.erdmovee.utils.formatDecimal
import com.greildev.erdmovee.utils.imgUrlFormatter

class FavoriteListAdapter(
    private val action: (Int, String) -> Unit,
    private val deleteAction: (Int, String) -> Unit
) : BaseListAdapter<FavoriteMovieListEntities, CardMovieFavoriteItemBinding>(
    CardMovieFavoriteItemBinding::inflate
) {
    override fun onItemBind(): (FavoriteMovieListEntities, CardMovieFavoriteItemBinding, View, Int) -> Unit {
        return { item, binding, view, _ ->
            binding.tvTitle.text = item.title
            binding.tvDate.text = item.releaseDate
            binding.tvRating.text = item.voteAverage.formatDecimal()
            Glide.with(view.context)
                .load(item.posterPath.imgUrlFormatter())
                .into(binding.ivPoster)
            binding.chipPrice.text = item.price.toString()
            for (genre in item.genresName.genreIds) {
                val chip = Chip(view.context)
                chip.text = genre
                chip.setEnsureMinTouchTargetSize(false)
                chip.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.md_theme_dark_onPrimary
                    )
                )
                chip.setPadding(0,0,0,0)
                chip.includeFontPadding = false
                chip.textSize = 11F
                chip.isCheckable = false
                chip.isClickable = false
                chip.setChipBackgroundColorResource(R.color.md_theme_dark_primary)
                binding.cgGenre.addView(chip)
            }
            binding.btnDelete.setOnClickListener {
                deleteAction.invoke(item.favoriteId, item.title)
            }
            view.setOnClickListener {
                action.invoke(item.id, item.title)
            }
        }
    }
}