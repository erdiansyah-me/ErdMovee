package com.greildev.erdmovee.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greildev.core.domain.model.PaymentListInfo
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.PaymentMethodListItemBinding

class PaymentListAdapter(
    private val submitList: List<PaymentListInfo>
) : RecyclerView.Adapter<PaymentListAdapter.PaymentListViewHolder>() {
    inner class PaymentListViewHolder(var binding: PaymentMethodListItemBinding): RecyclerView.ViewHolder(binding.root)

    private var onItemClickListener: OnItemClickListener? = null
    private var isEwallet: Boolean? = null
    interface OnItemClickListener {
        fun onItemClicked(data: PaymentListInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentListViewHolder {
        val binding = PaymentMethodListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentListViewHolder(binding)
    }

    override fun getItemCount(): Int = submitList.size

    override fun onBindViewHolder(holder: PaymentListViewHolder, position: Int) {
        val payment = submitList[position]
        holder.apply {
            binding.apply {
                tvPaymentName.text = payment.label
                if (isEwallet == true) {
                    ivPaymentVirtualLogo.isVisible = true
                    ivPaymentLogo.isVisible = false
                    Glide.with(itemView.context)
                        .load(payment.image)
                        .into(ivPaymentVirtualLogo)
                } else {
                    ivPaymentVirtualLogo.isVisible = false
                    ivPaymentLogo.isVisible = true
                    Glide.with(itemView.context)
                        .load(payment.image)
                        .into(ivPaymentLogo)
                }

                if (!payment.status) {
                    itemView.isClickable = false
                    itemView.setBackgroundColor(itemView.context.getColor(R.color.md_theme_light_outline))
                } else {
                    itemView.isClickable = true
                    itemView.setOnClickListener {
                        onItemClickListener?.onItemClicked(payment)
                    }
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
    fun setIconLogoSize(isEwallet: Boolean) {
        this.isEwallet = isEwallet
    }
}