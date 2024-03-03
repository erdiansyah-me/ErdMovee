package com.greildev.core.domain.model

import android.net.Uri

data class UserData (
    val username: String?,
    val email: String?,
    val photoUri: Uri?,
    val uid: String
)