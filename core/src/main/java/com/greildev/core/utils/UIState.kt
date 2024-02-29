package com.greildev.core.utils

sealed class UIState<R> (
    val data: R? = null ,
    val message: String? = null,
    val code: Int? = null
){
    class Success<R>(data: R): UIState<R>(data)
    class Error<R>(code: Int?, errorMessage: String?): UIState<R>(code = code, message = errorMessage)
    class Loading<Nothing>: UIState<Nothing>()
    class NoState<Nothing>: UIState<Nothing>()
}

suspend fun <R> UIState<R>.streamData(
    onSuccess: suspend (result: UIState<R>) -> Unit,
    onError: suspend (result: UIState<R>) -> Unit,
    onLoading: suspend (result: UIState<R>) -> Unit,
    onNoState: suspend (result: UIState<R>) -> Unit
){
    when(this){
        is UIState.Success -> onSuccess.invoke(this)
        is UIState.Error -> onError.invoke(this)
        is UIState.Loading -> onLoading.invoke(this)
        is UIState.NoState -> onNoState.invoke(this)
    }
}