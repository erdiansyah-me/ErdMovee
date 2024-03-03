package com.greildev.core.data.repository

import com.google.firebase.auth.FirebaseUser
import com.greildev.core.data.source.local.LocalDataSource
import com.greildev.core.data.source.remote.RemoteDataSource
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepository {

    //USER
    suspend fun userData(): Flow<FirebaseUser?>
    suspend fun loginUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>>
    suspend fun registerUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>>
    suspend fun updateProfile(profile: ProfileRequest): Flow<SourceResult<String>>
    fun logOutUser()

    //ONBOARDING
    fun getUserOnboardingPreferences(): Flow<Boolean>
    suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean)
}

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : UserRepository {
    override suspend fun userData(): Flow<FirebaseUser?> = remoteDataSource.userData()

    override suspend fun loginUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> =
        remoteDataSource.loginUser(authRequest = authRequest)

    override suspend fun registerUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> =
        remoteDataSource.registerUser(authRequest = authRequest)

    override suspend fun updateProfile(profile: ProfileRequest): Flow<SourceResult<String>> =
        remoteDataSource.updateProfile(profile = profile)

    override fun logOutUser() = remoteDataSource.logoutUser()

    override fun getUserOnboardingPreferences(): Flow<Boolean> {
        return localDataSource.userOnboardingPreferences()
    }

    override suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean) {
        localDataSource.saveUserOnboardingPreferences(isShowOnboarding)
    }
}