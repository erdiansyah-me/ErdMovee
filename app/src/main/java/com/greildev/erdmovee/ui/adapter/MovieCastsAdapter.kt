package com.greildev.erdmovee.ui.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.greildev.core.base.BaseListAdapter
import com.greildev.core.domain.model.CastsData
import com.greildev.erdmovee.databinding.CardCastsItemBinding
import com.greildev.erdmovee.utils.imgUrlFormatter

class MovieCastsAdapter :
    BaseListAdapter<CastsData, CardCastsItemBinding>(CardCastsItemBinding::inflate) {
    override fun onItemBind(): (CastsData, CardCastsItemBinding, View, Int) -> Unit {
        return { item, binding, view, _ ->
            binding.tvCastsName.text = item.name
            binding.tvCastsRole.text = item.character
            Glide.with(view.context)
                .load(item.profilePath.imgUrlFormatter())
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.ivCastsImage)
        }
    }
}