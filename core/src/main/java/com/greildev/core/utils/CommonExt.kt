package com.greildev.core.utils

import com.greildev.core.utils.CoreConstant.EMPTY_STRING

fun Any?.orNullToString(): String {
    return this?.toString() ?: EMPTY_STRING
}