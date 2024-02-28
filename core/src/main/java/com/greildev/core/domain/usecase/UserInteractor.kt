package com.greildev.core.domain.usecase

import com.greildev.core.data.repository.UserRepository
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.domain.model.UserData
import com.greildev.core.utils.DataMapper.toUIData
import com.greildev.core.utils.UIState
import com.greildev.core.utils.suspendSubscribe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class UserInteractor(
    private val userRepository: UserRepository
) {

    val userData: Flow<UserData?> = flow {
        emit(userRepository.userData.first()?.toUIData())
    }

    suspend fun userLogin(authRequest: AuthRequest): Flow<UIState<Boolean>> = flow {
        emit(UIState.Loading())
        userRepository.loginUser(authRequest)
            .collect {
                it.suspendSubscribe(
                    onSuccess = { result ->
                        if (result.data != null) {
                            emit(UIState.Success(result.data))
                        } else {
                            emit(UIState.Error(code = 0, errorMessage = "User not found"))
                        }
                    },
                    onError = { result ->
                        emit(
                            UIState.Error(
                                code = 600,
                                errorMessage = result.message
                            )
                        )
                    }
                )
            }
    }

    suspend fun userRegister(authRequest: AuthRequest): Flow<UIState<Boolean>> = flow {
        emit(UIState.Loading())
        userRepository.registerUser(authRequest)
            .collect {
                it.suspendSubscribe(
                    onSuccess = { result ->
                        if (result.data != null) {
                            emit(UIState.Success(result.data))
                        } else {
                            emit(UIState.Error(code = 404, errorMessage = "User not found"))
                        }
                    },
                    onError = { result ->
                        emit(
                            UIState.Error(
                                code = 600,
                                errorMessage = result.message
                            )
                        )
                    }
                )
            }
    }

    suspend fun updateProfile(profile: ProfileRequest): Flow<UIState<String>> = flow {
        emit(UIState.Loading())
        userRepository.updateProfile(profile).collect {
            it.suspendSubscribe(
                onSuccess = { result ->
                    if (result.data != null) {
                        emit(UIState.Success(result.data))
                    } else {
                        emit(UIState.Error(code = 0, errorMessage = "User not found"))
                    }
                },
                onError = { result ->
                    emit(
                        UIState.Error(
                            code = result.errorCode,
                            errorMessage = result.message
                        )
                    )
                }
            )
        }
    }

    fun userLogout() = userRepository.logOutUser()

    fun getUserOnboardingPreferences(): Flow<Boolean> =
        userRepository.getUserOnboardingPreferences()

    suspend fun saveUserOnboardingPreferences(isShowOnboarding: Boolean) =
        userRepository.saveUserOnboardingPreferences(isShowOnboarding)
}
