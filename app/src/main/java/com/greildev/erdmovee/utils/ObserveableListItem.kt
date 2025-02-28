package com.greildev.erdmovee.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ObserveableListItem<T: Any> (item: T) {
    private val _item = MutableLiveData<T>()
    val item: LiveData<T> = _item

    init {
        _item.value = item
    }

    fun update(newItem: T) {
        _item.value = newItem
    }
}
