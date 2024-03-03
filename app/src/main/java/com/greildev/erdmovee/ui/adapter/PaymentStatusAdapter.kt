package com.greildev.erdmovee.ui.adapter

import android.view.View
import com.greildev.core.base.BaseListAdapter
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.PaymentStatusItemBinding

class PaymentStatusAdapter: BaseListAdapter<CartMovieListEntities, PaymentStatusItemBinding>(PaymentStatusItemBinding::inflate) {
    override fun onItemBind(): (CartMovieListEntities, PaymentStatusItemBinding, View, Int) -> Unit {
        return { data, binding, view, _ ->
            binding.apply {
                tvItemTitle.text = data.title
                tvItemAmount.text = view.context.getString(R.string.cart_quantity_item, data.quantityItem.toString())
                tvItemPrice.text = view.context.getString(R.string.amount_coins, data.quantityPrice.toString())
            }
        }
    }

}