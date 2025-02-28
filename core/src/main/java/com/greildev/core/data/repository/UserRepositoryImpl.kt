package com.greildev.core.data.repository

import androidx.room.withTransaction
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val database: ErdmoveeDatabase,
    private val prefs: PreferencesDataStore,
    private val dispatcher: DispatcherProvider
) : UserRepository {

    override suspend fun currentUser(): UserDataEntity? = withContext(dispatcher.io) {
        database.userDataDao.getUserData()
    }

    override suspend fun loginUser(authRequest: AuthRequest): Flow<Boolean> = withContext(dispatcher.io){
        return@withContext callbackFlow {
            val login = userService.loginUser(authRequest)
            login.collectLatest {
                when (it) {
                    is SourceResult.Success -> {
                        val userDao = database.userDataDao
                        it.data?.let {user ->
                            val userData = UserDataEntity(
                                uid = user.uid,
                                displayName = user.displayName.orEmpty(),
                                photoUrl = user.photoUrl.orNullToString(),
                                email = user.email.orEmpty(),
                                phoneNumber = user.phoneNumber.orEmpty()
                            )
                            database.withTransaction {
                                userDao.deleteAllUserData()
                                userDao.insertUserData(userData)
                            }
                        }
                        trySend(true)
                    }
                    is SourceResult.Error -> {
                        trySend(false)
                    }
                }
            }
            awaitClose()
        }
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
