package com.greildev.erdmovee.ui.prelogin

import UserParams
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.domain.usecase.UserUseCase
import com.greildev.core.utils.UIState
import com.greildev.erdmovee.utils.Constant
import com.greildev.erdmovee.utils.FlowState
import com.greildev.erdmovee.utils.SplashState
import com.greildev.erdmovee.utils.Validate
import com.greildev.erdmovee.utils.toSplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class PreloginViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    private val _userStateSplash =
        MutableStateFlow<FlowState<SplashState<Nothing>>>(FlowState.FlowCreated)
    val userStateSplash: StateFlow<FlowState<SplashState<Nothing>>> = _userStateSplash

    fun getUserSplash() {
        viewModelScope.launch {
            _userStateSplash.value = FlowState.FlowValue(
                UserParams(
                    user = userUseCase.userData().first(),
                    isOnboarding = userUseCase.getUserOnboardingPreferences().first()
                ).toSplashState()
            )
        }
    }

    fun saveUserOnboardingPreferences(isShowOnboarding: Boolean) {
        viewModelScope.launch {
            userUseCase.saveUserOnboardingPreferences(isShowOnboarding)
        }
    }

    private val _userLogin = MutableStateFlow<UIState<Boolean>>(UIState.NoState())
    val userLogin: StateFlow<UIState<Boolean>> = _userLogin
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            userUseCase.userLogin(AuthRequest(username, password)).collect {
                _userLogin.value = it
            }
        }
    }

    private val _userRegister = MutableStateFlow<UIState<Boolean>>(UIState.NoState())
    val userRegister: StateFlow<UIState<Boolean>> = _userRegister
    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            userUseCase.userRegister(AuthRequest(email, password)).collect {
                _userRegister.value = it
            }
        }
    }

    private val _userProfile = MutableStateFlow<UIState<String>>(UIState.NoState())
    val userProfile: StateFlow<UIState<String>> = _userProfile

    fun updateProfile(username: String, photo: File?) {
        viewModelScope.launch {
            userUseCase.updateProfile(ProfileRequest(username, photo)).collect {
                _userProfile.value = it
            }
        }
    }

    //validate email and password
    private val _validateLoginPassword = MutableStateFlow(Validate.INITIAL)
    val validateLoginPassword: StateFlow<Validate> = _validateLoginPassword
    fun validateLoginPassword(password: String) {
        _validateLoginPassword.update {
            if (password.isPasswordValid()) {
                Validate.VALID
            } else {
                Validate.INVALID
            }
        }
    }

    private val _validateLoginEmail = MutableStateFlow(Validate.INITIAL)
    val validateLoginEmail: StateFlow<Validate> = _validateLoginEmail
    fun validateLoginEmail(email: String) {
        _validateLoginEmail.update {
            if (email.isEmailValid()) {
                Validate.VALID
            } else {
                Validate.INVALID
            }
        }
    }

    private val _validateRegisterPassword = MutableStateFlow(Validate.INITIAL)
    val validateRegisterPassword: StateFlow<Validate> = _validateRegisterPassword
    fun validateRegisterPassword(password: String) {
        _validateRegisterPassword.update {
            if (password.isPasswordValid()) {
                Validate.VALID
            } else {
                Validate.INVALID
            }
        }
    }

    private val _validateRegisterEmail = MutableStateFlow(Validate.INITIAL)
    val validateRegisterEmail: StateFlow<Validate> = _validateRegisterEmail
    fun validateRegisterEmail(email: String) {
        _validateRegisterEmail.update {
            if (email.isEmailValid()) {
                Validate.VALID
            } else {
                Validate.INVALID
            }
        }
    }

    private val _validateProfileName = MutableStateFlow<FlowState<Boolean>>(FlowState.FlowCreated)
    val validateProfileName: StateFlow<FlowState<Boolean>> = _validateProfileName
    fun validateProfileName(name: String) {
        _validateProfileName.update { FlowState.FlowValue(name.validateRequired()) }
    }

    val validateLoginField: StateFlow<FlowState<Boolean>> = combine(
        validateLoginEmail, validateLoginPassword
    ) { email, password ->
        if (email == Validate.VALID && password == Validate.VALID) {
            FlowState.FlowValue(true)
        } else {
            FlowState.FlowValue(false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FlowState.FlowCreated
    )

    val validateRegisterField: StateFlow<FlowState<Boolean>> = combine(
        validateRegisterEmail, validateRegisterPassword
    ) { email, password ->
        if (email == Validate.VALID && password == Validate.VALID) {
            FlowState.FlowValue(true)
        } else {
            FlowState.FlowValue(false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FlowState.FlowCreated
    )

    private fun String.isPasswordValid(): Boolean {
        return when {
            this.length >= Constant.PASSWORD_LENGTH -> true
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
}
