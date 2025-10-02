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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val database: ErdmoveeDatabase,
    private val prefs: PreferencesDataStore,
    private val dispatcher: DispatcherProvider
) : UserRepository {

    private val accessMutex = Mutex()

    override suspend fun currentUser(): UserDataEntity? = withContext(dispatcher.io) {
        accessMutex.withLock {
            database.userDataDao.getUserData()
        }
    }

    override suspend fun loginUser(authRequest: AuthRequest): Flow<Boolean> = // No longer suspend here
        userService.loginUser(authRequest) // This likely returns Flow<SourceResult<User>>
            .map { sourceResult -> // Transform the SourceResult to Boolean
                when (sourceResult) {
                    is SourceResult.Success -> {
                        sourceResult.data?.let { user ->
                            val userDao = database.userDataDao
                            val userData = UserDataEntity(
                                uid = user.uid,
                                displayName = user.displayName.orEmpty(),
                                photoUrl = user.photoUrl.orNullToString(),
                                email = user.email.orEmpty(),
                                phoneNumber = user.phoneNumber.orEmpty()
                            )
                            // Important: Database operations should also be on an I/O dispatcher
                            // If database.withTransaction is not already suspending and on IO, adjust this.
                            // Assuming database.withTransaction is a suspending function:
                            database.withTransaction {
                                userDao.deleteAllUserData()
                                userDao.insertUserData(userData)
                            }
                        } ?: return@map false // Handle null user data case
                        true
                    }
                    is SourceResult.Error -> {
                        false
                    }
                }
            }.flowOn(dispatcher.io) // Execute all upstream operations (userService.loginUser, map block) on IO dispatcher



    override suspend fun registerUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> =
        withContext(dispatcher.io) {
            userService.registerUser(authRequest = authRequest)
        }


    override suspend fun updateProfile(profile: ProfileRequest): Flow<SourceResult<String>> =
        withContext(dispatcher.io) {
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
