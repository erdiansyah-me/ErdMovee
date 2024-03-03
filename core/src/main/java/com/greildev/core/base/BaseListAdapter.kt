package com.greildev.core.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseListAdapter<T : Any, VB : ViewBinding>(
    private val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : ListAdapter<T, BaseViewHolder<T, VB>>(BaseItemCallBack<T>()) {
    abstract fun onItemBind(): (T, VB, View, Int) -> Unit
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, VB> {
        val binding = bindingFactory(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding.root, binding, onItemBind())
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, VB>, position: Int) {
        holder.bind(getItem(position))
    }
}

class BaseViewHolder<T : Any, VB : ViewBinding>(
    view: View,
    private val binding: VB,
    private val onItemBind: (T, VB, View, Int) -> Unit
) : RecyclerView.ViewHolder(view) {
    fun bind(item: T) {
        onItemBind(item, binding, itemView, absoluteAdapterPosition)
    }
}

class BaseItemCallBack<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}