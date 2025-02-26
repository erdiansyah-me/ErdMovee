package com.greildev.core.data.repository

import com.google.firebase.auth.FirebaseUser
import com.greildev.core.data.source.local.database.ErdmoveeDatabase
import com.greildev.core.data.source.local.entities.UserDataEntity
import com.greildev.core.data.source.local.preferences.PreferencesDataStore
import com.greildev.core.data.source.remote.service.UserService
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.domain.repository.UserRepository
import com.greildev.core.utils.DispatcherProvider
import com.greildev.core.utils.SourceResult
import com.greildev.core.utils.orNullToString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val database: ErdmoveeDatabase,
    private val prefs: PreferencesDataStore,
    private val dispatcher: DispatcherProvider
) : UserRepository {
    private suspend fun userData(): Flow<FirebaseUser?> = withContext(dispatcher.io) {
        val userDao = database.userDataDao
        val firebaseUser = userService.userData().last()
        firebaseUser?.let {
            val userData = UserDataEntity(
                uid = it.uid,
                displayName = it.displayName.orEmpty(),
                photoUrl = it.photoUrl.orNullToString(),
                email = it.email.orEmpty(),
                phoneNumber = it.phoneNumber.orEmpty()
            )

            userDao.insertUserData(userData)
        }
        return@withContext userService.userData()
    }

    override suspend fun currentUser(): UserDataEntity? = withContext(dispatcher.io) {
        database.userDataDao.getUserData()
    }

    override suspend fun loginUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> = withContext(dispatcher.io){
        val login = userService.loginUser(authRequest)
        userData()
        return@withContext login
    }


    override suspend fun registerUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> = withContext(dispatcher.io) {
        userService.registerUser(authRequest = authRequest)
    }


    override suspend fun updateProfile(profile: ProfileRequest): Flow<SourceResult<String>> = withContext(dispatcher.io) {
        userService.updateProfile(profile = profile)
    }

    override fun logOutUser() {
        database.userDataDao.deleteAllUserData()
        userService.logoutUser()
    }

    override fun getUserOnboardingPreferences(): Flow<Boolean> {
        return prefs.getUserOnboardingPreferences()
    }

    override suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean) {
        prefs.saveUserOnboardingPreferences(isShowOnboarding)
    }
}