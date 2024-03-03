package com.greildev.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val paymentList: List<PaymentResponseItem>
)

data class PaymentResponseItemInfo(

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("label")
    val label: String,

    @field:SerializedName("status")
    val status: Boolean
)

data class PaymentResponseItem(

    @field:SerializedName("item")
    val item: List<PaymentResponseItemInfo>,

    @field:SerializedName("title")
    val title: String
)
