package com.greildev.core.utils

import com.google.firebase.auth.FirebaseUser
import com.greildev.core.domain.model.UserData

object DataMapper {
    fun FirebaseUser.toUIData(): UserData {
        return UserData(
            username = this.displayName,
            email = this.email,
            photoUri = this.photoUrl
        )
    }
}