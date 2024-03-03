package com.greildev.erdmovee.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.greildev.core.base.BaseItemCallBack
import com.greildev.core.domain.model.PaymentList
import com.greildev.core.domain.model.PaymentListInfo
import com.greildev.erdmovee.databinding.PaymentMethodCategoryItemBinding
import com.greildev.erdmovee.ui.payment.PaymentListAdapter

class PaymentCategoryAdapter: ListAdapter<PaymentList, PaymentCategoryAdapter.PaymentCategoryViewHolder>(
    BaseItemCallBack()
) {

    private var onItemClickListener: OnPaymentCategoryClickListener? = null

    interface OnPaymentCategoryClickListener {
        fun onItemClicked(data: PaymentListInfo)
    }

    inner class PaymentCategoryViewHolder(var binding: PaymentMethodCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentCategoryViewHolder {
        val binding =
            PaymentMethodCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentCategoryViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PaymentCategoryViewHolder, position: Int) {
        val payment = getItem(position)
        holder.apply {
            val adapter = PaymentListAdapter(payment.item)
            binding.apply {
                tvPaymentCategoryTitle.text = payment.title
                when (position == 2) {
                    true -> adapter.setIconLogoSize(true)
                    else -> adapter.setIconLogoSize(false)
                }
                rvPaymentListItem.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            binding.rvPaymentListItem.layoutManager = LinearLayoutManager(itemView.context)
            adapter.setOnItemClickListener(object : PaymentListAdapter.OnItemClickListener {
                override fun onItemClicked(data: PaymentListInfo) {
                    onItemClickListener?.onItemClicked(data)
                }
            })
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnPaymentCategoryClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}