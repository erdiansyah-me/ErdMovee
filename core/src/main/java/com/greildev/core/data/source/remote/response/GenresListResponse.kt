package com.greildev.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GenresListResponse(

    @field:SerializedName("genres")
    val genres: List<GenresItem>
)

data class GenresItem(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: Int
)
