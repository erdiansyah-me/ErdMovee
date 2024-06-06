package com.greildev.erdmovee.ui.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.greildev.core.base.BaseListAdapter
import com.greildev.core.data.source.local.entities.CartMovieListEntities
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.CartListItemBinding
import com.greildev.erdmovee.utils.imgUrlFormatter

class CartListAdapter(
    private val cbIsChecked: (Int, Boolean) -> Unit,
    private val onIncrement: (Int, Int, Int) -> Unit,
    private val onDecrement: (Int, Int, Int) -> Unit
) : BaseListAdapter<CartMovieListEntities, CartListItemBinding>(CartListItemBinding::inflate)
{
    override fun onItemBind(): (CartMovieListEntities, CartListItemBinding, View, Int) -> Unit {
        return { item, binding, view, position ->
            binding.tvItemTitle.text = item.title
            binding.cbItem.isChecked = item.isChecked
            Glide.with(view.context)
                .load(item.posterPath.imgUrlFormatter())
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.ivItemPoster)
            binding.tvQuantityItem.text = view.context.getString(R.string.cart_quantity_item, item.quantityItem.toString())
            binding.tvPrice.text = view.context.getString(R.string.price_per_day, item.basePrice.toString())
            binding.cbItem.setOnCheckedChangeListener { _, isChecked ->
                cbIsChecked.invoke(item.cartId, isChecked)
            }
            binding.btnDecrement.isEnabled = item.quantityItem != 1
            binding.btnIncrement.setOnClickListener {
                val newQuantity = item.quantityItem + 1
                val newQuantityPrice = item.basePrice * newQuantity
                onIncrement.invoke(item.cartId, newQuantity, newQuantityPrice)
                binding.tvQuantityItem.text = view.context.getString(R.string.cart_quantity_item, newQuantity.toString())
            }
            binding.btnDecrement.setOnClickListener {
                val newQuantity = item.quantityItem - 1
                val newQuantityPrice = item.basePrice * newQuantity
                binding.btnDecrement.isEnabled = newQuantity != 1
                binding.tvQuantityItem.text = view.context.getString(R.string.cart_quantity_item, newQuantity.toString())
                onDecrement.invoke(item.cartId, newQuantity, newQuantityPrice)
            }
        }
    }

}