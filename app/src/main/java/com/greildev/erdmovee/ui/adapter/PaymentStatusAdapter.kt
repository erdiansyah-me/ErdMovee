package com.greildev.erdmovee.ui.adapter

import android.view.View
import com.greildev.core.base.BaseListAdapter
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.PaymentStatusItemBinding
import com.greildev.erdmovee.ui.model.PaymentStatusItemUIModel

class PaymentStatusAdapter: BaseListAdapter<PaymentStatusItemUIModel, PaymentStatusItemBinding>(
    PaymentStatusItemBinding::inflate
) {
    override fun onItemBind(): (PaymentStatusItemUIModel, PaymentStatusItemBinding, View, Int) -> Unit {
        return { data, binding, view, _ ->
            binding.apply {
                tvItemTitle.text = data.title
                tvItemAmount.text = view.context.getString(R.string.cart_quantity_item, data.quantityItem.toString())
                tvItemPrice.text = view.context.getString(R.string.amount_coins, data.quantityPrice.toString())
            }
        }
    }

}
