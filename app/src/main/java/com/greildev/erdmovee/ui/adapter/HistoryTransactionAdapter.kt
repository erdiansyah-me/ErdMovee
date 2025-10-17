package com.greildev.erdmovee.ui.adapter

import android.view.View
import com.greildev.core.base.BaseListAdapter
import com.greildev.core.data.model.TransactionDetail
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.CardTransactionHistoryBinding

class HistoryTransactionAdapter(private val itemClickListener:(TransactionDetail) -> Unit) :
    BaseListAdapter<TransactionDetail, CardTransactionHistoryBinding>(CardTransactionHistoryBinding::inflate) {
    override fun onItemBind(): (TransactionDetail, CardTransactionHistoryBinding, View, Int) -> Unit {
        return { item, binding, view, _ ->
            binding.apply {
                tvTransactionDate.text = item.transactionDate
                if (item.transactionType == "TOPUP") {
                    tvTransactionType.text = "Coins Top Up"
                    icTransactionType.setImageResource(R.drawable.ic_coins_24)
                    tvItemTotalPrice.text = view.context.getString(R.string.price_rupiah, item.amountToken)
                } else {
                    tvTransactionType.text = "Rent Movie"
                    icTransactionType.setImageResource(R.drawable.ic_movie_outline_24)
                    tvItemTotalPrice.text = view.context.getString(R.string.price_coins, item.amountToken)
                }
                tvItemBuy.text = view.context.getString(
                    R.string.transaction_movie_amount,
                    item.itemList.size.toString()
                )
                chipTransactionStatus.text = view.context.getString(R.string.success)
                view.setOnClickListener {
                    itemClickListener.invoke(item)
                }
            }
        }
    }

}
