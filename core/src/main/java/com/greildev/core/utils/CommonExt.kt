package com.greildev.core.utils

import android.os.Build
import com.greildev.core.utils.CoreConstant.EMPTY_STRING
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

fun Any?.orNullToString(): String {
    return this?.toString() ?: EMPTY_STRING
}

fun getCurrentDateTime(): String {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formatter.format(currentDate.time)
    } else {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        currentDateTime.format(formatter)
    }
}