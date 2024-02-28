package com.greildev.erdmovee.ui.prelogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.domain.model.UserData
import com.greildev.core.domain.usecase.UseCase
import com.greildev.core.utils.UIState
import com.greildev.erdmovee.utils.FlowState
import com.greildev.erdmovee.utils.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class PreloginViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {

    private val _userStateSplash =
        MutableStateFlow<FlowState<SplashState<Nothing>>>(FlowState.FlowCreated)
    val userStateSplash: StateFlow<FlowState<SplashState<Nothing>>> = _userStateSplash

    fun getUserSplash() {
        viewModelScope.launch {
            _userStateSplash.value = FlowState.FlowValue(
                UserParams(
                    user = useCase.userUsecase().userData.first(),
                    isOnboarding = useCase.userUsecase().getUserOnboardingPreferences().first()
                ).toSplashState()
            )
        }
    }

    fun saveUserOnboardingPreferences(isShowOnboarding: Boolean) {
        viewModelScope.launch {
            useCase.userUsecase().saveUserOnboardingPreferences(isShowOnboarding)
        }
    }

    private val _userLogin = MutableStateFlow<UIState<Boolean>>(UIState.NoState())
    val userLogin: StateFlow<UIState<Boolean>> = _userLogin
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            useCase.userUsecase().userLogin(AuthRequest(username, password)).collect {
                _userLogin.value = it
            }
        }
    }

    private val _userRegister = MutableStateFlow<UIState<Boolean>>(UIState.NoState())
    val userRegister: StateFlow<UIState<Boolean>> = _userRegister
    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            useCase.userUsecase().userRegister(AuthRequest(email, password)).collect {
                _userRegister.value = it
            }
        }
    }

    private val _userProfile = MutableStateFlow<UIState<String>>(UIState.NoState())
    val userProfile: StateFlow<UIState<String>> = _userProfile

    fun updateProfile(username: String, photo: File?) {
        viewModelScope.launch {
            useCase.userUsecase().updateProfile(ProfileRequest(username, photo)).collect {
                _userProfile.value = it
            }
        }
    }

    //validate email and password
    private val _validateLoginPassword = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val validateLoginPassword: StateFlow<FlowState<Boolean>> = _validateLoginPassword
    fun validateLoginPassword(password: String) {
        _validateLoginPassword.update { FlowState.FlowValue(password.isPasswordValid()) }
    }

    private val _validateLoginEmail = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val validateLoginEmail: StateFlow<FlowState<Boolean>> = _validateLoginEmail
    fun validateLoginEmail(email: String) {
        _validateLoginEmail.update { FlowState.FlowValue(email.isEmailValid()) }
    }

    private val _validateRegisterPassword =
        MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val validateRegisterPassword: StateFlow<FlowState<Boolean>> = _validateRegisterPassword
    fun validateRegisterPassword(password: String) {
        _validateRegisterPassword.update { FlowState.FlowValue(password.isPasswordValid()) }
    }

    private val _validateRegisterEmail = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val validateRegisterEmail: StateFlow<FlowState<Boolean>> = _validateRegisterEmail
    fun validateRegisterEmail(email: String) {
        _validateRegisterEmail.update { FlowState.FlowValue(email.isEmailValid()) }
    }

    val _validateProfileName = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val validateProfileName: StateFlow<FlowState<Boolean>> = _validateProfileName
    fun validateProfileName(name: String) {
        _validateProfileName.update { FlowState.FlowValue(name.validateRequired()) }
    }

    private val _validateLoginField = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val validateLoginField: StateFlow<FlowState<Boolean>> = _validateLoginField
    fun validateLoginField(email: String, password: String) {
        _validateLoginField.update { FlowState.FlowValue(email.isEmailValid() && password.isPasswordValid()) }
    }

    private val _validateRegisterField = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val validateRegisterField: StateFlow<FlowState<Boolean>> = _validateRegisterField
    fun validateRegisterField(email: String, password: String) {
        _validateRegisterField.update { FlowState.FlowValue(email.isEmailValid() && password.isPasswordValid()) }
    }

    private fun String.isPasswordValid(): Boolean {
        return when {
            this.length >= 8 -> true
            else -> false
        }
    }

    private fun String.validateRequired(): Boolean {
        return when {
            this.isNotEmpty() -> true
            else -> false
        }
    }

    private fun String.isEmailValid(): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(this)
        return matcher.matches()
    }

    private fun UserParams.toSplashState() = when {
        this.user != null && !this.user.username.isNullOrEmpty() -> {
            SplashState.Main()
        }

        this.user?.username.isNullOrEmpty() && !this.user?.email.isNullOrEmpty() -> {
            SplashState.Profile()
        }

        this.user == null && !isOnboarding -> {
            SplashState.Login()
        }

        this.isOnboarding -> SplashState.Onboarding()
        else -> SplashState.Onboarding()
    }
}

data class UserParams(
    val user: UserData?,
    val isOnboarding: Boolean
)

