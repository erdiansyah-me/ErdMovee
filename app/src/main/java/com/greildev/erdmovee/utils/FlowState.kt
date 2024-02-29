package com.greildev.erdmovee.utils

sealed class FlowState<out R> {
    data object FlowCreated: FlowState<Nothing>()

    data class FlowValue<T>(val data: T): FlowState<T>()
}

fun <T> FlowState<T>.onCreated(
    execute: () -> Unit
) : FlowState<T> = apply {
    if (this is FlowState.FlowCreated) execute()
}

fun <T> FlowState<T>.onValue(
    execute: (data: T) -> Unit
) : FlowState<T> = apply {
    if (this is FlowState.FlowValue) execute(data)
}