package com.greildev.core.domain.usecase

import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.domain.model.UserData
import com.greildev.core.utils.UIState
import kotlinx.coroutines.flow.Flow

interface UserUseCase {
    suspend fun userData(): Flow<UserData?>
    suspend fun userLogin(authRequest: AuthRequest): Flow<UIState<Boolean>>
    suspend fun userRegister(authRequest: AuthRequest): Flow<UIState<Boolean>>
    suspend fun updateProfile(profile: ProfileRequest): Flow<UIState<String>>
    fun userLogout()
    fun getUserOnboardingPreferences(): Flow<Boolean>
    suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean)
}