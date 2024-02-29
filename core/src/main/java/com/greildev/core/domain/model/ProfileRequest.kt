package com.greildev.core.domain.model

import java.io.File

data class ProfileRequest(
    val username: String,
    val photo: File? = null
)