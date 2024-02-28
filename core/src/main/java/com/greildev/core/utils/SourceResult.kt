package com.greildev.core.utils

sealed class SourceResult<T>(
    val data: T? = null,
    val message: String? = null,
    val errorCode: Int? = null
) {
    class Success<T>(data: T) : SourceResult<T>(data)
    class Error<T>(errorCode: Int?, errorMessage: String) :
        SourceResult<T>(errorCode = errorCode, message = errorMessage)
}