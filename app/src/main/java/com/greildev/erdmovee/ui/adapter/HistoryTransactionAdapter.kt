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
                tvItemName.text = item.transactionId
                tvTransactionType.text = "Sewa Film"
                tvItemTotalPrice.text = item.amountToken.toString()
                tvItemBuy.text = view.context.getString(
                    R.string.transaction_movie_amount,
                    item.cartMovieListEntities?.size.toString()
                )
                chipTransactionStatus.text = view.context.getString(R.string.success)
                view.setOnClickListener {
                    itemClickListener.invoke(item)
                }
            }
        }
    }

}