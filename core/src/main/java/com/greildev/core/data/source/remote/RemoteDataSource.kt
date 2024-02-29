package com.greildev.core.data.source.remote

import com.google.firebase.auth.FirebaseUser
import com.greildev.core.data.source.remote.service.UserService
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val userService: UserService
) {
    val userData: Flow<FirebaseUser?> = userService.userData

    suspend fun loginUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> =
        userService.loginUser(authRequest)

    suspend fun registerUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> {
        return userService.registerUser(authRequest)
    }


    fun updateProfile(profile: ProfileRequest): Flow<SourceResult<String>> =
        userService.updateProfile(profile)

    fun logoutUser() = userService.logoutUser()
}