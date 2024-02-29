package com.greildev.core.utils

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T?
): SourceResult<T> =
    try {
        val result = apiCall.invoke() ?: throw Exception("Network Error")
        SourceResult.Success(result)
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> SourceResult.Error(errorCode = 666, "Cannot connect to server")
            is HttpException -> SourceResult.Error(
                throwable.code(),
                throwable.localizedMessage ?: "Unknown Error"
            )

            else -> SourceResult.Error(
                errorCode = 500,
                throwable.localizedMessage ?: "Internal Server Error"
            )
        }
    }

suspend fun <T> SourceResult<T>.suspendSubscribe(
    onSuccess: suspend (result: SourceResult<T>) -> Unit,
    onError: suspend (result: SourceResult<T>) -> Unit,
) {
    when (this) {
        is SourceResult.Success -> onSuccess.invoke(this)

        is SourceResult.Error -> onError.invoke(this)

    }
}