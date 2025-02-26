package com.greildev.core.domain.repository

import com.greildev.core.data.source.local.entities.UserDataEntity
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    //USER
    suspend fun currentUser(): UserDataEntity?
    suspend fun loginUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>>
    suspend fun registerUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>>
    suspend fun updateProfile(profile: ProfileRequest): Flow<SourceResult<String>>
    fun logOutUser()

    //ONBOARDING
    fun getUserOnboardingPreferences(): Flow<Boolean>
    suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean)
}